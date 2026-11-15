package id.serviskus.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import id.serviskus.app.data.local.entity.ServiceItemEntity
import id.serviskus.app.data.local.entity.ServiceRecordEntity
import id.serviskus.app.data.local.entity.ServiceRecordWithItems
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ServiceRecordDao {

    @Query("SELECT * FROM service_record WHERE vehicle_id = :vehicleId ORDER BY date DESC, odometer DESC, id DESC")
    abstract fun observeByVehicle(vehicleId: Long): Flow<List<ServiceRecordEntity>>

    @Transaction
    @Query("SELECT * FROM service_record WHERE vehicle_id = :vehicleId ORDER BY date DESC, odometer DESC, id DESC")
    abstract fun observeWithItemsByVehicle(vehicleId: Long): Flow<List<ServiceRecordWithItems>>

    @Query("SELECT * FROM service_record WHERE vehicle_id = :vehicleId ORDER BY date ASC, odometer ASC, id ASC")
    abstract suspend fun getByVehicleChronological(vehicleId: Long): List<ServiceRecordEntity>

    @Query("SELECT * FROM service_item WHERE record_id = :recordId ORDER BY id ASC")
    abstract suspend fun getItemsByRecordId(recordId: Long): List<ServiceItemEntity>

    @Insert
    abstract suspend fun insertRecord(record: ServiceRecordEntity): Long

    @Insert
    abstract suspend fun insertItems(items: List<ServiceItemEntity>)

    @Transaction
    open suspend fun insertWithItems(
        record: ServiceRecordEntity,
        items: List<ServiceItemEntity>,
    ): Long {
        val recordId = insertRecord(record)
        if (items.isNotEmpty()) {
            insertItems(items.map { it.copy(recordId = recordId) })
        }
        return recordId
    }
}
