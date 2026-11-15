package id.serviskus.app.data.repository

import id.serviskus.app.core.dispatcher.IoDispatcher
import id.serviskus.app.data.local.dao.VehicleDao
import id.serviskus.app.data.mapper.toDomain
import id.serviskus.app.data.mapper.toEntity
import id.serviskus.app.domain.model.Vehicle
import id.serviskus.app.domain.repository.VehicleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomVehicleRepository @Inject constructor(
    private val vehicleDao: VehicleDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : VehicleRepository {

    override fun observeVehicles(): Flow<List<Vehicle>> =
        vehicleDao.observeAll()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(ioDispatcher)

    override suspend fun getVehicleById(vehicleId: Long): Vehicle? =
        withContext(ioDispatcher) {
            vehicleDao.getById(vehicleId)?.toDomain()
        }

    override suspend fun addVehicle(vehicle: Vehicle): Long =
        withContext(ioDispatcher) {
            vehicleDao.insert(vehicle.toEntity())
        }
}
