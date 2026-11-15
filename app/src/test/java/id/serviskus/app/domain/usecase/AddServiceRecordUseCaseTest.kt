package id.serviskus.app.domain.usecase

import id.serviskus.app.domain.model.ServiceRecord
import id.serviskus.app.domain.model.Vehicle
import id.serviskus.app.domain.repository.ServiceRepository
import id.serviskus.app.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class AddServiceRecordUseCaseTest {

    private val zone: ZoneId = ZoneId.of("Asia/Jakarta")
    private val fixedClock: Clock = Clock.fixed(Instant.parse("2026-11-15T05:00:00Z"), zone)
    private val today: LocalDate = LocalDate.now(fixedClock) // 2026-11-15

    @Test
    fun `servis lama dengan km lebih rendah lolos tanpa menurunkan currentOdometer`() = runTest {
        val harness = createHarness(currentOdometer = 15_000)

        val result = harness.useCase(
            AddServiceRecordRequest(
                vehicleId = VEHICLE_ID,
                date = today.minusMonths(3),
                odometer = 9_500,
                items = listOf(NewServiceItem(name = "Oli mesin", cost = 65_000L)),
            ),
        )

        assertTrue(result is AddServiceRecordResult.Success)
        assertEquals(15_000, harness.currentVehicle().currentOdometer)
        assertEquals(9_500, harness.serviceRepo.records.single().odometer)
    }

    @Test
    fun `km di antara dua catatan lolos`() = runTest {
        val harness = createHarness(
            currentOdometer = 15_000,
            initialRecords = listOf(
                record(id = 1L, date = LocalDate.of(2026, 6, 1), odometer = 8_000),
                record(id = 2L, date = LocalDate.of(2026, 10, 1), odometer = 14_000),
            ),
        )

        val result = harness.useCase(
            AddServiceRecordRequest(
                vehicleId = VEHICLE_ID,
                date = LocalDate.of(2026, 8, 1),
                odometer = 11_000,
            ),
        )

        assertTrue(result is AddServiceRecordResult.Success)
        assertEquals(3, harness.serviceRepo.records.size)
        assertEquals(15_000, harness.currentVehicle().currentOdometer)
    }

    @Test
    fun `km melewati catatan sesudahnya ditolak dengan batas bawah dan atas`() = runTest {
        val harness = createHarness(
            currentOdometer = 15_000,
            initialRecords = listOf(
                record(id = 1L, date = LocalDate.of(2026, 6, 1), odometer = 8_000),
                record(id = 2L, date = LocalDate.of(2026, 10, 1), odometer = 14_000),
            ),
        )

        val result = harness.useCase(
            AddServiceRecordRequest(
                vehicleId = VEHICLE_ID,
                date = LocalDate.of(2026, 8, 1),
                odometer = 14_001,
            ),
        )

        assertEquals(
            AddServiceRecordResult.OdometerOutOfRange(
                minAllowed = 8_000,
                maxAllowed = 14_000,
            ),
            result,
        )
        assertEquals(2, harness.serviceRepo.records.size)
    }

    @Test
    fun `km di bawah catatan sebelumnya menurut tanggal ditolak`() = runTest {
        val harness = createHarness(
            currentOdometer = 15_000,
            initialRecords = listOf(
                record(id = 1L, date = LocalDate.of(2026, 6, 1), odometer = 8_000),
                record(id = 2L, date = LocalDate.of(2026, 10, 1), odometer = 14_000),
            ),
        )

        val result = harness.useCase(
            AddServiceRecordRequest(
                vehicleId = VEHICLE_ID,
                date = LocalDate.of(2026, 8, 1),
                odometer = 7_999,
            ),
        )

        assertEquals(
            AddServiceRecordResult.OdometerOutOfRange(
                minAllowed = 8_000,
                maxAllowed = 14_000,
            ),
            result,
        )
        assertEquals(2, harness.serviceRepo.records.size)
    }

    @Test
    fun `tepat di batas bawah atas dan currentOdometer lolos`() = runTest {
        val harness = createHarness(
            currentOdometer = 15_000,
            initialRecords = listOf(
                record(id = 1L, date = LocalDate.of(2026, 6, 1), odometer = 8_000),
                record(id = 2L, date = LocalDate.of(2026, 10, 1), odometer = 14_000),
            ),
        )

        val lowerBoundaryResult = harness.useCase(
            AddServiceRecordRequest(
                vehicleId = VEHICLE_ID,
                date = LocalDate.of(2026, 8, 1),
                odometer = 8_000,
            ),
        )
        val upperBoundaryResult = harness.useCase(
            AddServiceRecordRequest(
                vehicleId = VEHICLE_ID,
                date = LocalDate.of(2026, 9, 1),
                odometer = 14_000,
            ),
        )
        val todayBoundaryResult = harness.useCase(
            AddServiceRecordRequest(
                vehicleId = VEHICLE_ID,
                date = today,
                odometer = 15_000,
            ),
        )

        assertTrue(lowerBoundaryResult is AddServiceRecordResult.Success)
        assertTrue(upperBoundaryResult is AddServiceRecordResult.Success)
        assertTrue(todayBoundaryResult is AddServiceRecordResult.Success)
    }

    @Test
    fun `servis hari ini di bawah currentOdometer ditolak`() = runTest {
        val harness = createHarness(
            currentOdometer = 15_000,
            initialRecords = listOf(
                record(id = 1L, date = today.minusDays(10), odometer = 13_000),
            ),
        )

        val result = harness.useCase(
            AddServiceRecordRequest(
                vehicleId = VEHICLE_ID,
                date = today,
                odometer = 14_999,
            ),
        )

        assertEquals(
            AddServiceRecordResult.OdometerOutOfRange(
                minAllowed = 15_000,
                maxAllowed = null,
            ),
            result,
        )
        assertEquals(1, harness.serviceRepo.records.size)
    }

    @Test
    fun `perbandingan memakai catatan terakhir menurut tanggal bukan maksimum global`() = runTest {
        val harness = createHarness(
            currentOdometer = 12_000,
            initialRecords = listOf(
                record(id = 1L, date = LocalDate.of(2026, 5, 1), odometer = 50_000),
                record(id = 2L, date = LocalDate.of(2026, 6, 1), odometer = 12_000),
            ),
        )

        val result = harness.useCase(
            AddServiceRecordRequest(
                vehicleId = VEHICLE_ID,
                date = LocalDate.of(2026, 7, 1),
                odometer = 12_500,
            ),
        )

        assertTrue(result is AddServiceRecordResult.Success)
        assertEquals(12_500, harness.currentVehicle().currentOdometer)
    }

    @Test
    fun `konfirmasi koreksi odometer melewati batas tetap tersimpan dan tidak menurunkan currentOdometer`() = runTest {
        val harness = createHarness(currentOdometer = 15_000)

        val result = harness.useCase(
            AddServiceRecordRequest(
                vehicleId = VEHICLE_ID,
                date = today,
                odometer = 11_000,
                confirmOdometerCorrection = true,
            ),
        )

        assertTrue(result is AddServiceRecordResult.Success)
        assertEquals(15_000, harness.currentVehicle().currentOdometer)
        assertEquals(11_000, harness.serviceRepo.records.single().odometer)
    }

    @Test
    fun `totalCost tidak diisi menghitung jumlah biaya item`() = runTest {
        val harness = createHarness(currentOdometer = 10_000)

        val result = harness.useCase(
            AddServiceRecordRequest(
                vehicleId = VEHICLE_ID,
                date = today,
                odometer = 10_500,
                totalCost = null,
                items = listOf(
                    NewServiceItem(name = "Oli MPX2", cost = 58_500L),
                    NewServiceItem(name = "Oli gardan", cost = 18_000L),
                ),
            ),
        )

        assertTrue(result is AddServiceRecordResult.Success)
        assertEquals(76_500L, harness.serviceRepo.records.single().totalCost)
        assertEquals(10_500, harness.currentVehicle().currentOdometer)
    }

    @Test
    fun `totalCost tidak diisi dengan item kosong menghasilkan nol`() = runTest {
        val harness = createHarness(currentOdometer = 10_000)

        val result = harness.useCase(
            AddServiceRecordRequest(
                vehicleId = VEHICLE_ID,
                date = today,
                odometer = 10_000,
                totalCost = null,
                items = emptyList(),
            ),
        )

        assertTrue(result is AddServiceRecordResult.Success)
        assertEquals(0L, harness.serviceRepo.records.single().totalCost)
    }

    @Test
    fun `totalCost lebih besar dari jumlah item diizinkan`() = runTest {
        val harness = createHarness(currentOdometer = 10_000)

        val result = harness.useCase(
            AddServiceRecordRequest(
                vehicleId = VEHICLE_ID,
                date = today,
                odometer = 10_500,
                totalCost = 135_000L,
                items = listOf(NewServiceItem(name = "Kampas rem", cost = 85_000L)),
            ),
        )

        assertTrue(result is AddServiceRecordResult.Success)
        assertEquals(135_000L, harness.serviceRepo.records.single().totalCost)
    }

    @Test
    fun `totalCost lebih kecil dari jumlah biaya item ditolak sebagai InvalidCost`() = runTest {
        val harness = createHarness(currentOdometer = 10_000)

        val result = harness.useCase(
            AddServiceRecordRequest(
                vehicleId = VEHICLE_ID,
                date = today,
                odometer = 10_500,
                totalCost = 99_999L,
                items = listOf(
                    NewServiceItem(name = "Busi", cost = 35_000L),
                    NewServiceItem(name = "Filter udara", cost = 65_000L),
                ),
            ),
        )

        assertEquals(AddServiceRecordResult.InvalidCost, result)
        assertTrue(harness.serviceRepo.records.isEmpty())
    }

    @Test
    fun `biaya negatif nama item kosong odometer negatif dan motor hilang ditolak`() = runTest {
        val harness = createHarness(currentOdometer = 10_000)

        assertEquals(
            AddServiceRecordResult.VehicleNotFound,
            harness.useCase(
                AddServiceRecordRequest(vehicleId = 999L, date = today, odometer = 10_000),
            ),
        )
        assertEquals(
            AddServiceRecordResult.NegativeOdometer,
            harness.useCase(
                AddServiceRecordRequest(vehicleId = VEHICLE_ID, date = today, odometer = -1),
            ),
        )
        assertEquals(
            AddServiceRecordResult.BlankItemName,
            harness.useCase(
                AddServiceRecordRequest(
                    vehicleId = VEHICLE_ID,
                    date = today,
                    odometer = 10_000,
                    items = listOf(NewServiceItem(name = "  ", cost = 10_000L)),
                ),
            ),
        )
        assertEquals(
            AddServiceRecordResult.InvalidCost,
            harness.useCase(
                AddServiceRecordRequest(
                    vehicleId = VEHICLE_ID,
                    date = today,
                    odometer = 10_000,
                    items = listOf(NewServiceItem(name = "Oli", cost = -500L)),
                ),
            ),
        )
        assertEquals(
            AddServiceRecordResult.InvalidCost,
            harness.useCase(
                AddServiceRecordRequest(
                    vehicleId = VEHICLE_ID,
                    date = today,
                    odometer = 10_000,
                    totalCost = -1L,
                ),
            ),
        )
    }

    private fun createHarness(
        currentOdometer: Int,
        initialRecords: List<ServiceRecord> = emptyList(),
    ): TestHarness {
        val vehicleRepo = FakeVehicleRepository(
            Vehicle(
                id = VEHICLE_ID,
                name = "Vario 160",
                plateNumber = "B 4123 XYZ",
                currentOdometer = currentOdometer,
            ),
        )
        val serviceRepo = FakeServiceRepository(vehicleRepo, initialRecords)
        val useCase = AddServiceRecordUseCase(vehicleRepo, serviceRepo, fixedClock)
        return TestHarness(useCase, vehicleRepo, serviceRepo)
    }

    private fun record(
        id: Long,
        date: LocalDate,
        odometer: Int,
    ): ServiceRecord = ServiceRecord(
        id = id,
        vehicleId = VEHICLE_ID,
        date = date,
        odometer = odometer,
        workshop = null,
        totalCost = 50_000L,
        note = null,
        receiptImagePath = null,
    )

    private data class TestHarness(
        val useCase: AddServiceRecordUseCase,
        val vehicleRepo: FakeVehicleRepository,
        val serviceRepo: FakeServiceRepository,
    ) {
        fun currentVehicle(): Vehicle = vehicleRepo.vehicles.single()
    }

    private class FakeVehicleRepository(initial: Vehicle) : VehicleRepository {
        val vehicles = mutableListOf(initial)
        private val flow = MutableStateFlow(vehicles.toList())

        override fun observeVehicles(): Flow<List<Vehicle>> = flow

        override suspend fun getVehicleById(vehicleId: Long): Vehicle? =
            vehicles.firstOrNull { it.id == vehicleId }

        override suspend fun addVehicle(vehicle: Vehicle): Long = vehicle.id

        fun advanceOdometer(vehicleId: Long, newOdometer: Int) {
            val index = vehicles.indexOfFirst { it.id == vehicleId }
            if (index >= 0 && vehicles[index].currentOdometer < newOdometer) {
                vehicles[index] = vehicles[index].copy(currentOdometer = newOdometer)
                flow.value = vehicles.toList()
            }
        }
    }

    private class FakeServiceRepository(
        private val vehicleRepo: FakeVehicleRepository,
        initial: List<ServiceRecord>,
    ) : ServiceRepository {
        val records = initial.toMutableList()
        private val flow = MutableStateFlow(records.toList())
        private var nextId = (initial.maxOfOrNull { it.id } ?: 0L) + 1L

        override fun observeServiceRecords(vehicleId: Long): Flow<List<ServiceRecord>> =
            flow.map { list -> list.filter { it.vehicleId == vehicleId } }

        override suspend fun getServiceRecords(vehicleId: Long): List<ServiceRecord> =
            records.filter { it.vehicleId == vehicleId }

        override suspend fun addServiceRecord(record: ServiceRecord): Long {
            val id = nextId++
            records += record.copy(id = id)
            vehicleRepo.advanceOdometer(record.vehicleId, record.odometer)
            flow.value = records.toList()
            return id
        }
    }

    private companion object {
        const val VEHICLE_ID = 1L
    }
}
