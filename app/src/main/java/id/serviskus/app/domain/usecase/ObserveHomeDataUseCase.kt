package id.serviskus.app.domain.usecase

import id.serviskus.app.domain.model.ServiceRecord
import id.serviskus.app.domain.model.UpcomingMaintenance
import id.serviskus.app.domain.model.Vehicle
import id.serviskus.app.domain.repository.ServiceRepository
import id.serviskus.app.domain.repository.UpcomingMaintenanceProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

sealed interface HomeData {
    data object Empty : HomeData

    data class Content(
        val vehicles: List<Vehicle>,
        val activeVehicle: Vehicle,
        val upcomingMaintenance: List<UpcomingMaintenance>,
        val recentServices: List<ServiceRecord>,
    ) : HomeData
}

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveHomeDataUseCase @Inject constructor(
    private val observeActiveVehicle: ObserveActiveVehicleUseCase,
    private val serviceRepository: ServiceRepository,
    private val upcomingMaintenanceProvider: UpcomingMaintenanceProvider,
) {
    operator fun invoke(): Flow<HomeData> =
        observeActiveVehicle().flatMapLatest { selection ->
            if (selection == null) {
                flowOf(HomeData.Empty)
            } else {
                combine(
                    upcomingMaintenanceProvider.observeUpcomingMaintenance(selection.activeVehicle),
                    serviceRepository.observeServiceRecords(selection.activeVehicle.id),
                ) { upcoming, services ->
                    HomeData.Content(
                        vehicles = selection.vehicles,
                        activeVehicle = selection.activeVehicle,
                        upcomingMaintenance = upcoming.sortedWith(URGENCY_ORDER),
                        recentServices = services,
                    )
                }
            }
        }

    private companion object {
        val URGENCY_ORDER: Comparator<UpcomingMaintenance> =
            compareBy<UpcomingMaintenance> { it.status.ordinal }
                .thenBy { it.remainingDays ?: Long.MAX_VALUE }
                .thenBy { it.remainingKm ?: Int.MAX_VALUE }
                .thenBy { it.id }
    }
}
