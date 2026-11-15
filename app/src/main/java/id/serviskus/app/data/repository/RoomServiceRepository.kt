package id.serviskus.app.data.repository

import androidx.room.withTransaction
import id.serviskus.app.core.dispatcher.IoDispatcher
import id.serviskus.app.data.local.ServisKuDatabase
import id.serviskus.app.data.local.dao.ServiceRecordDao
import id.serviskus.app.data.local.dao.VehicleDao
import id.serviskus.app.data.mapper.toDomain
import id.serviskus.app.data.mapper.toEntity
import id.serviskus.app.domain.model.ServiceRecord
import id.serviskus.app.domain.repository.ServiceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomServiceRepository @Inject constructor(
    private val database: ServisKuDatabase,
    private val serviceRecordDao: ServiceRecordDao,
    private val vehicleDao: VehicleDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ServiceRepository {

    override fun observeServiceRecords(vehicleId: Long): Flow<List<ServiceRecord>> =
        serviceRecordDao.observeWithItemsByVehicle(vehicleId)
            .map { records -> records.map { it.toDomain() } }
            .flowOn(ioDispatcher)

    override suspend fun getServiceRecords(vehicleId: Long): List<ServiceRecord> =
        withContext(ioDispatcher) {
            serviceRecordDao.getByVehicleChronological(vehicleId)
                .map { it.toDomain() }
        }

    override suspend fun addServiceRecord(record: ServiceRecord): Long =
        withContext(ioDispatcher) {
            database.withTransaction {
                vehicleDao.advanceOdometer(
                    vehicleId = record.vehicleId,
                    newOdometer = record.odometer,
                )
                serviceRecordDao.insertWithItems(
                    record = record.toEntity(),
                    items = record.items.map { it.toEntity(recordId = 0L) },
                )
            }
        }
}
