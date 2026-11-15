package id.serviskus.app.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ServiceRecordWithItems(
    @Embedded val record: ServiceRecordEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "record_id",
    )
    val items: List<ServiceItemEntity>,
)
