package id.serviskus.app.domain.model

data class Vehicle(
    val id: Long = 0L,
    val name: String,
    val plateNumber: String?,
    val currentOdometer: Int,
    val color: VehicleColor = VehicleColor.DEFAULT,
)
