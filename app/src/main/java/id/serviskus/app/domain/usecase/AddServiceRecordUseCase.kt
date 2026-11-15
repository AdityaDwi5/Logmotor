package id.serviskus.app.domain.usecase

import id.serviskus.app.domain.model.ServiceItem
import id.serviskus.app.domain.model.ServiceRecord
import id.serviskus.app.domain.repository.ServiceRepository
import id.serviskus.app.domain.repository.VehicleRepository
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

data class NewServiceItem(
    val partTypeId: Long? = null,
    val name: String,
    val cost: Long,
)

data class AddServiceRecordRequest(
    val vehicleId: Long,
    val date: LocalDate,
    val odometer: Int,
    val workshop: String? = null,
    val totalCost: Long? = null,
    val note: String? = null,
    val receiptImagePath: String? = null,
    val items: List<NewServiceItem> = emptyList(),
    val confirmOdometerCorrection: Boolean = false,
)

sealed interface AddServiceRecordResult {
    data class Success(val recordId: Long) : AddServiceRecordResult
    data object VehicleNotFound : AddServiceRecordResult
    data object NegativeOdometer : AddServiceRecordResult
    data object BlankItemName : AddServiceRecordResult
    data object InvalidCost : AddServiceRecordResult
    data class OdometerOutOfRange(
        val minAllowed: Int?,
        val maxAllowed: Int?,
    ) : AddServiceRecordResult
}

class AddServiceRecordUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val serviceRepository: ServiceRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(request: AddServiceRecordRequest): AddServiceRecordResult {
        val vehicle = vehicleRepository.getVehicleById(request.vehicleId)
            ?: return AddServiceRecordResult.VehicleNotFound

        if (request.odometer < 0) {
            return AddServiceRecordResult.NegativeOdometer
        }

        val itemsValidation = validateAndNormalizeItems(request.items)
        if (itemsValidation is ItemsValidation.Invalid) {
            return itemsValidation.failure
        }
        val normalizedItems = (itemsValidation as ItemsValidation.Valid).items
        val itemsSum = itemsValidation.totalCost

        val resolvedTotalCost = resolveTotalCost(request.totalCost, itemsSum)
            ?: return AddServiceRecordResult.InvalidCost

        if (!request.confirmOdometerCorrection) {
            val existingRecords = serviceRepository.getServiceRecords(request.vehicleId)
            val bounds = computeOdometerBounds(
                newDate = request.date,
                today = LocalDate.now(clock),
                vehicleCurrentOdometer = vehicle.currentOdometer,
                existingRecords = existingRecords,
            )
            if (isOutOfRange(request.odometer, bounds)) {
                return AddServiceRecordResult.OdometerOutOfRange(
                    minAllowed = bounds.minAllowed,
                    maxAllowed = bounds.maxAllowed,
                )
            }
        }

        val recordId = serviceRepository.addServiceRecord(
            ServiceRecord(
                vehicleId = request.vehicleId,
                date = request.date,
                odometer = request.odometer,
                workshop = request.workshop.normalizeOptionalText(),
                totalCost = resolvedTotalCost,
                note = request.note.normalizeOptionalText(),
                receiptImagePath = request.receiptImagePath.normalizeOptionalText(),
                items = normalizedItems,
            ),
        )
        return AddServiceRecordResult.Success(recordId)
    }

    private fun validateAndNormalizeItems(items: List<NewServiceItem>): ItemsValidation {
        val normalized = mutableListOf<ServiceItem>()
        var sum = 0L
        for (item in items) {
            val trimmedName = item.name.trim()
            if (trimmedName.isEmpty()) {
                return ItemsValidation.Invalid(AddServiceRecordResult.BlankItemName)
            }
            if (item.cost < 0L || item.cost > Long.MAX_VALUE - sum) {
                return ItemsValidation.Invalid(AddServiceRecordResult.InvalidCost)
            }
            sum += item.cost
            normalized += ServiceItem(
                partTypeId = item.partTypeId,
                name = trimmedName,
                cost = item.cost,
            )
        }
        return ItemsValidation.Valid(normalized, sum)
    }

    private fun resolveTotalCost(providedTotal: Long?, itemsSum: Long): Long? =
        when (providedTotal) {
            null -> itemsSum
            else -> if (providedTotal < 0L || providedTotal < itemsSum) null else providedTotal
        }

    private fun computeOdometerBounds(
        newDate: LocalDate,
        today: LocalDate,
        vehicleCurrentOdometer: Int,
        existingRecords: List<ServiceRecord>,
    ): OdometerBounds {
        val previousNeighbor = existingRecords
            .filter { !it.date.isAfter(newDate) }
            .maxWithOrNull(CHRONOLOGICAL_ORDER)
            ?.odometer

        val nextNeighbor = existingRecords
            .filter { it.date.isAfter(newDate) }
            .minWithOrNull(CHRONOLOGICAL_ORDER)
            ?.odometer

        val lowerCandidates = buildList {
            previousNeighbor?.let(::add)
            if (!newDate.isBefore(today)) {
                add(vehicleCurrentOdometer)
            }
        }

        return OdometerBounds(
            minAllowed = lowerCandidates.maxOrNull(),
            maxAllowed = nextNeighbor,
        )
    }

    private fun isOutOfRange(odometer: Int, bounds: OdometerBounds): Boolean {
        val belowMin = bounds.minAllowed != null && odometer < bounds.minAllowed
        val aboveMax = bounds.maxAllowed != null && odometer > bounds.maxAllowed
        return belowMin || aboveMax
    }

    private fun String?.normalizeOptionalText(): String? = this?.trim()?.takeIf { it.isNotEmpty() }

    private sealed interface ItemsValidation {
        data class Valid(val items: List<ServiceItem>, val totalCost: Long) : ItemsValidation
        data class Invalid(val failure: AddServiceRecordResult) : ItemsValidation
    }

    private data class OdometerBounds(
        val minAllowed: Int?,
        val maxAllowed: Int?,
    )

    private companion object {
        val CHRONOLOGICAL_ORDER: Comparator<ServiceRecord> =
            compareBy<ServiceRecord> { it.date }
                .thenBy { it.odometer }
                .thenBy { it.id }
    }
}
