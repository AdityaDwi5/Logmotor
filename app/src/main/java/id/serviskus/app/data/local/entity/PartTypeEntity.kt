package id.serviskus.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "part_type")
data class PartTypeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "default_interval_km") val defaultIntervalKm: Int? = null,
    @ColumnInfo(name = "default_interval_months") val defaultIntervalMonths: Int? = null,
)
