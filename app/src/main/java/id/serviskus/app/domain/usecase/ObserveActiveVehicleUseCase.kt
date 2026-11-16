package id.serviskus.app.domain.usecase

import id.serviskus.app.domain.model.Vehicle
import id.serviskus.app.domain.repository.ActiveVehicleRepository
import id.serviskus.app.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class ActiveVehicleSelection(
    val vehicles: List<Vehicle>,
    val activeVehicle: Vehicle,
)

class ObserveActiveVehicleUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val activeVehicleRepository: ActiveVehicleRepository,
) {
    operator fun invoke(): Flow<ActiveVehicleSelection?> =
        combine(
            vehicleRepository.observeVehicles(),
            activeVehicleRepository.observeSelectedVehicleId(),
        ) { vehicles, selectedId ->
            if (vehicles.isEmpty()) {
                null
            } else {
                val activeVehicle = vehicles.firstOrNull { it.id == selectedId } ?: vehicles.first()
                ActiveVehicleSelection(
                    vehicles = vehicles,
                    activeVehicle = activeVehicle,
                )
            }
        }
}
