package id.serviskus.app.domain.usecase

import id.serviskus.app.domain.model.Vehicle
import id.serviskus.app.domain.model.VehicleColor
import id.serviskus.app.domain.repository.VehicleRepository
import javax.inject.Inject

data class AddVehicleRequest(
    val name: String,
    val plateNumber: String? = null,
    val currentOdometer: Int,
    val color: VehicleColor = VehicleColor.DEFAULT,
)

sealed interface AddVehicleResult {
    data class Success(val vehicleId: Long) : AddVehicleResult
    data object BlankName : AddVehicleResult
    data object NegativeOdometer : AddVehicleResult
}

class AddVehicleUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository,
) {
    suspend operator fun invoke(request: AddVehicleRequest): AddVehicleResult {
        val trimmedName = request.name.trim()
        if (trimmedName.isEmpty()) {
            return AddVehicleResult.BlankName
        }
        if (request.currentOdometer < 0) {
            return AddVehicleResult.NegativeOdometer
        }

        val normalizedPlate = request.plateNumber?.trim()?.takeIf { it.isNotEmpty() }
        val vehicleId = vehicleRepository.addVehicle(
            Vehicle(
                name = trimmedName,
                plateNumber = normalizedPlate,
                currentOdometer = request.currentOdometer,
                color = request.color,
            ),
        )
        return AddVehicleResult.Success(vehicleId)
    }
}
