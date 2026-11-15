package id.serviskus.app.domain.repository

import id.serviskus.app.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    fun observeVehicles(): Flow<List<Vehicle>>
    suspend fun getVehicleById(vehicleId: Long): Vehicle?
    suspend fun addVehicle(vehicle: Vehicle): Long
}
