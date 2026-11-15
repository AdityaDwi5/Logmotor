package id.serviskus.app.domain.model

data class ServiceItem(
    val id: Long = 0L,
    val recordId: Long = 0L,
    val partTypeId: Long? = null,
    val name: String,
    val cost: Long,
)
