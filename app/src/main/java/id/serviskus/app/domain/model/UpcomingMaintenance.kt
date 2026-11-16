package id.serviskus.app.domain.model

data class UpcomingMaintenance(
    val id: Long,
    val vehicleId: Long,
    val partName: String,
    val category: MaintenanceCategory,
    val remainingKm: Int?,
    val remainingDays: Long?,
    val status: MaintenanceStatus,
)
