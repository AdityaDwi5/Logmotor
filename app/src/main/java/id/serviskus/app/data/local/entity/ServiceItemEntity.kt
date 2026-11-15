package id.serviskus.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "service_item",
    foreignKeys = [
        ForeignKey(
            entity = ServiceRecordEntity::class,
            parentColumns = ["id"],
            childColumns = ["record_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PartTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["part_type_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index("record_id"),
        Index("part_type_id"),
    ],
)
data class ServiceItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "record_id") val recordId: Long,
    @ColumnInfo(name = "part_type_id") val partTypeId: Long?,
    val name: String,
    val cost: Long,
)
