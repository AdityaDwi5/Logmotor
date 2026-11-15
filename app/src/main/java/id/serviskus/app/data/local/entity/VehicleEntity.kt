package id.serviskus.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicle")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "plate_number") val plateNumber: String?,
    @ColumnInfo(name = "current_odometer") val currentOdometer: Int,
    @ColumnInfo(name = "color_key") val colorKey: String,
)
