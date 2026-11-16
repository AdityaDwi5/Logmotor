package id.serviskus.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface ActiveVehicleRepository {
    fun observeSelectedVehicleId(): Flow<Long?>
    suspend fun setSelectedVehicleId(vehicleId: Long)
}
