package id.serviskus.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import id.serviskus.app.data.local.entity.VehicleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicle ORDER BY id ASC")
    fun observeAll(): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicle WHERE id = :vehicleId")
    fun observeById(vehicleId: Long): Flow<VehicleEntity?>

    @Query("SELECT * FROM vehicle WHERE id = :vehicleId")
    suspend fun getById(vehicleId: Long): VehicleEntity?

    @Insert
    suspend fun insert(vehicle: VehicleEntity): Long

    @Update
    suspend fun update(vehicle: VehicleEntity)

    @Query("UPDATE vehicle SET current_odometer = :newOdometer WHERE id = :vehicleId AND current_odometer < :newOdometer")
    suspend fun advanceOdometer(vehicleId: Long, newOdometer: Int)

    @Query("DELETE FROM vehicle WHERE id = :vehicleId")
    suspend fun deleteById(vehicleId: Long)
}
