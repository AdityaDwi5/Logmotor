package id.serviskus.app.domain.repository

import id.serviskus.app.domain.model.ServiceRecord
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    fun observeServiceRecords(vehicleId: Long): Flow<List<ServiceRecord>>
    suspend fun getServiceRecords(vehicleId: Long): List<ServiceRecord>
    suspend fun addServiceRecord(record: ServiceRecord): Long
}
