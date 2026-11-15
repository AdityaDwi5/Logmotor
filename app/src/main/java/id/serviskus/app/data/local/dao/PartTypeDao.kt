package id.serviskus.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import id.serviskus.app.data.local.entity.PartTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PartTypeDao {
    @Query("SELECT * FROM part_type ORDER BY id ASC")
    fun observeAll(): Flow<List<PartTypeEntity>>

    @Insert
    suspend fun insert(partType: PartTypeEntity): Long

    @Insert
    suspend fun insertAll(partTypes: List<PartTypeEntity>): List<Long>

    @Query("DELETE FROM part_type WHERE id = :partTypeId")
    suspend fun deleteById(partTypeId: Long)
}
