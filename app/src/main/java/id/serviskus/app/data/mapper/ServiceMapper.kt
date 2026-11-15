package id.serviskus.app.data.mapper

import id.serviskus.app.data.local.entity.ServiceItemEntity
import id.serviskus.app.data.local.entity.ServiceRecordEntity
import id.serviskus.app.data.local.entity.ServiceRecordWithItems
import id.serviskus.app.domain.model.ServiceItem
import id.serviskus.app.domain.model.ServiceRecord

fun ServiceItemEntity.toDomain(): ServiceItem = ServiceItem(
    id = id,
    recordId = recordId,
    partTypeId = partTypeId,
    name = name,
    cost = cost,
)

fun ServiceItem.toEntity(recordId: Long = this.recordId): ServiceItemEntity = ServiceItemEntity(
    id = id,
    recordId = recordId,
    partTypeId = partTypeId,
    name = name,
    cost = cost,
)

fun ServiceRecordEntity.toDomain(items: List<ServiceItem> = emptyList()): ServiceRecord = ServiceRecord(
    id = id,
    vehicleId = vehicleId,
    date = date,
    odometer = odometer,
    workshop = workshop,
    totalCost = totalCost,
    note = note,
    receiptImagePath = receiptImagePath,
    items = items,
)

fun ServiceRecordWithItems.toDomain(): ServiceRecord = record.toDomain(
    items = items.map { it.toDomain() },
)

fun ServiceRecord.toEntity(): ServiceRecordEntity = ServiceRecordEntity(
    id = id,
    vehicleId = vehicleId,
    date = date,
    odometer = odometer,
    workshop = workshop,
    totalCost = totalCost,
    note = note,
    receiptImagePath = receiptImagePath,
)
