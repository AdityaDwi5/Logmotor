package id.serviskus.app.domain.usecase

import id.serviskus.app.domain.model.Vehicle
import id.serviskus.app.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveVehiclesUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository,
) {
    operator fun invoke(): Flow<List<Vehicle>> = vehicleRepository.observeVehicles()
}
