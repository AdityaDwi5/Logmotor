package id.serviskus.app.domain.model

import java.time.LocalDate

data class ServiceRecord(
    val id: Long = 0L,
    val vehicleId: Long,
    val date: LocalDate,
    val odometer: Int,
    val workshop: String?,
    val totalCost: Long,
    val note: String?,
    val receiptImagePath: String?,
    val items: List<ServiceItem> = emptyList(),
)
