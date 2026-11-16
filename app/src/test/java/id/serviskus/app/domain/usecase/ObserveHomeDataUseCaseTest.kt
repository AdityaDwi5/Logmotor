package id.serviskus.app.domain.usecase

import id.serviskus.app.domain.model.MaintenanceCategory
import id.serviskus.app.domain.model.MaintenanceStatus
import id.serviskus.app.domain.model.ServiceRecord
import id.serviskus.app.domain.model.UpcomingMaintenance
import id.serviskus.app.domain.model.Vehicle
import id.serviskus.app.domain.model.VehicleColor
import id.serviskus.app.domain.repository.ActiveVehicleRepository
import id.serviskus.app.domain.repository.ServiceRepository
import id.serviskus.app.domain.repository.UpcomingMaintenanceProvider
import id.serviskus.app.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ObserveHomeDataUseCaseTest {

    @Test
    fun `ID motor aktif di DataStore tidak ada di database jatuh ke motor pertama`() = runTest {
        val firstVehicle = Vehicle(
            id = 1L,
            name = "Vario 160",
            plateNumber = "B 4123 XYZ",
            currentOdometer = 12_450,
            color = VehicleColor.BIRU_OLI,
        )
        val secondVehicle = Vehicle(
            id = 2L,
            name = "Supra X 125",
            plateNumber = "AB 5381 KM",
            currentOdometer = 34_100,
            color = VehicleColor.TEAL,
        )
        val harness = createHarness(
            initialVehicles = listOf(firstVehicle, secondVehicle),
            initialSelectedVehicleId = 999L,
        )

        val result = harness.useCase().first()

        assertTrue(result is HomeData.Content)
        val content = result as HomeData.Content
        assertEquals(firstVehicle, content.activeVehicle)
    }

    @Test
    fun `tidak ada motor mengembalikan kondisi kosong walau DataStore punya ID tersimpan`() = runTest {
        val harness = createHarness(
            initialVehicles = emptyList(),
            initialSelectedVehicleId = 1L,
        )

        val result = harness.useCase().first()

        assertEquals(HomeData.Empty, result)
    }

    @Test
    fun `perawatan mendatang diurutkan dari paling mendesak`() = runTest {
        val vehicle = Vehicle(
            id = 1L,
            name = "Vario 160",
            plateNumber = "B 4123 XYZ",
            currentOdometer = 12_450,
        )
        val okItem = UpcomingMaintenance(
            id = 1L,
            vehicleId = 1L,
            partName = "Filter udara",
            category = MaintenanceCategory.AIR_FILTER,
            remainingKm = 2_400,
            remainingDays = 60L,
            status = MaintenanceStatus.OK,
        )
        val soonItem = UpcomingMaintenance(
            id = 2L,
            vehicleId = 1L,
            partName = "Kampas rem depan",
            category = MaintenanceCategory.BRAKES,
            remainingKm = 320,
            remainingDays = 12L,
            status = MaintenanceStatus.SOON,
        )
        val overdueItem = UpcomingMaintenance(
            id = 3L,
            vehicleId = 1L,
            partName = "Oli mesin",
            category = MaintenanceCategory.ENGINE_OIL,
            remainingKm = -140,
            remainingDays = -2L,
            status = MaintenanceStatus.OVERDUE,
        )
        val harness = createHarness(
            initialVehicles = listOf(vehicle),
            initialSelectedVehicleId = 1L,
            upcomingByVehicle = mapOf(1L to listOf(okItem, soonItem, overdueItem)),
        )

        val content = harness.useCase().first() as HomeData.Content

        assertEquals(
            listOf(overdueItem, soonItem, okItem),
            content.upcomingMaintenance,
        )
    }

    @Test
    fun `mengganti ID motor aktif memuat perawatan dan riwayat servis motor baru`() = runTest {
        val first = Vehicle(id = 1L, name = "Vario", plateNumber = null, currentOdometer = 12_000)
        val second = Vehicle(id = 2L, name = "NMAX", plateNumber = "D 9012 AA", currentOdometer = 8_500)
        val firstRecord = ServiceRecord(
            id = 10L,
            vehicleId = 1L,
            date = LocalDate.of(2026, 11, 1),
            odometer = 12_000,
            workshop = null,
            totalCost = 75_000L,
            note = null,
            receiptImagePath = null,
        )
        val secondRecord = firstRecord.copy(id = 20L, vehicleId = 2L, odometer = 8_500)
        val harness = createHarness(
            initialVehicles = listOf(first, second),
            initialSelectedVehicleId = 1L,
            initialServices = listOf(firstRecord, secondRecord),
        )

        harness.activeVehicleRepository.setSelectedVehicleId(2L)
        val content = harness.useCase().first() as HomeData.Content

        assertEquals(second, content.activeVehicle)
        assertEquals(listOf(secondRecord), content.recentServices)
    }

    private fun createHarness(
        initialVehicles: List<Vehicle>,
        initialSelectedVehicleId: Long?,
        initialServices: List<ServiceRecord> = emptyList(),
        upcomingByVehicle: Map<Long, List<UpcomingMaintenance>> = emptyMap(),
    ): Harness {
        val vehicleRepo = FakeVehicleRepository(initialVehicles)
        val activeRepo = FakeActiveVehicleRepository(initialSelectedVehicleId)
        val serviceRepo = FakeServiceRepository(initialServices)
        val upcomingProvider = FakeUpcomingProvider(upcomingByVehicle)
        val observeActiveVehicle = ObserveActiveVehicleUseCase(vehicleRepo, activeRepo)
        val useCase = ObserveHomeDataUseCase(
            observeActiveVehicle = observeActiveVehicle,
            serviceRepository = serviceRepo,
            upcomingMaintenanceProvider = upcomingProvider,
        )
        return Harness(useCase, activeRepo)
    }

    private data class Harness(
        val useCase: ObserveHomeDataUseCase,
        val activeVehicleRepository: FakeActiveVehicleRepository,
    )

    private class FakeVehicleRepository(initial: List<Vehicle>) : VehicleRepository {
        private val flow = MutableStateFlow(initial)
        override fun observeVehicles(): Flow<List<Vehicle>> = flow
        override suspend fun getVehicleById(vehicleId: Long): Vehicle? =
            flow.value.firstOrNull { it.id == vehicleId }

        override suspend fun addVehicle(vehicle: Vehicle): Long = vehicle.id
    }

    private class FakeActiveVehicleRepository(initialId: Long?) : ActiveVehicleRepository {
        private val flow = MutableStateFlow(initialId)
        override fun observeSelectedVehicleId(): Flow<Long?> = flow
        override suspend fun setSelectedVehicleId(vehicleId: Long) {
            flow.value = vehicleId
        }
    }

    private class FakeServiceRepository(initial: List<ServiceRecord>) : ServiceRepository {
        private val flow = MutableStateFlow(initial)
        override fun observeServiceRecords(vehicleId: Long): Flow<List<ServiceRecord>> =
            flow.map { list -> list.filter { it.vehicleId == vehicleId } }

        override suspend fun getServiceRecords(vehicleId: Long): List<ServiceRecord> =
            flow.value.filter { it.vehicleId == vehicleId }

        override suspend fun addServiceRecord(record: ServiceRecord): Long = record.id
    }

    private class FakeUpcomingProvider(
        private val itemsByVehicle: Map<Long, List<UpcomingMaintenance>>,
    ) : UpcomingMaintenanceProvider {
        override fun observeUpcomingMaintenance(vehicle: Vehicle): Flow<List<UpcomingMaintenance>> =
            MutableStateFlow(itemsByVehicle[vehicle.id].orEmpty())
    }
}
