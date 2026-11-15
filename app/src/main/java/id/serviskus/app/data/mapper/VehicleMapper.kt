package id.serviskus.app.data.mapper

import id.serviskus.app.data.local.entity.VehicleEntity
import id.serviskus.app.domain.model.Vehicle
import id.serviskus.app.domain.model.VehicleColor

fun VehicleEntity.toDomain(): Vehicle = Vehicle(
    id = id,
    name = name,
    plateNumber = plateNumber,
    currentOdometer = currentOdometer,
    color = VehicleColor.fromKey(colorKey),
)

fun Vehicle.toEntity(): VehicleEntity = VehicleEntity(
    id = id,
    name = name,
    plateNumber = plateNumber,
    currentOdometer = currentOdometer,
    colorKey = color.key,
)
