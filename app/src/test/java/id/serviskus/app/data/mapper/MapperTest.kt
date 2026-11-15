package id.serviskus.app.data.mapper

import id.serviskus.app.data.local.entity.ServiceItemEntity
import id.serviskus.app.data.local.entity.ServiceRecordEntity
import id.serviskus.app.data.local.entity.ServiceRecordWithItems
import id.serviskus.app.data.local.entity.VehicleEntity
import id.serviskus.app.domain.model.ServiceItem
import id.serviskus.app.domain.model.ServiceRecord
import id.serviskus.app.domain.model.Vehicle
import id.serviskus.app.domain.model.VehicleColor
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class MapperTest {

    @Test
    fun `pemetaan VehicleEntity ke domain dan kembali menjaga semua nilai`() {
        val domain = Vehicle(
            id = 4L,
            name = "Vario 160",
            plateNumber = "B 4123 XYZ",
            currentOdometer = 18_450,
            color = VehicleColor.PLUM,
        )

        val entity = domain.toEntity()
        val roundTripped = entity.toDomain()

        assertEquals(domain, roundTripped)
    }

    @Test
    fun `kunci warna tidak dikenal di VehicleEntity dipetakan ke biru-oli`() {
        val entity = VehicleEntity(
            id = 1L,
            name = "Scoopy",
            plateNumber = null,
            currentOdometer = 0,
            colorKey = "kuning-status",
        )

        assertEquals(VehicleColor.BIRU_OLI, entity.toDomain().color)
    }

    @Test
    fun `pemetaan ServiceRecordWithItems dan batas angka menjaga nilai`() {
        val epochStart = LocalDate.ofEpochDay(0L)
        val record = ServiceRecord(
            id = 9L,
            vehicleId = 2L,
            date = epochStart,
            odometer = 24_120,
            workshop = "Bengkel Jaya",
            totalCost = 1_000_000_000L,
            note = "Overhaul ringan",
            receiptImagePath = "/files/nota.jpg",
            items = listOf(
                ServiceItem(
                    id = 11L,
                    recordId = 9L,
                    partTypeId = null,
                    name = "Oli mesin",
                    cost = 0L,
                ),
                ServiceItem(
                    id = 12L,
                    recordId = 9L,
                    partTypeId = 5L,
                    name = "Paket suku cadang",
                    cost = 1_000_000_000L,
                ),
            ),
        )

        val recordEntity: ServiceRecordEntity = record.toEntity()
        val itemEntities: List<ServiceItemEntity> = record.items.map { it.toEntity() }
        val roundTripped = ServiceRecordWithItems(
            record = recordEntity,
            items = itemEntities,
        ).toDomain()

        assertEquals(record, roundTripped)
    }
}
