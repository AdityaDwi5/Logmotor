package id.serviskus.app.feature.home

import app.cash.turbine.test
import id.serviskus.app.R
import id.serviskus.app.core.designsystem.VehicleAccent
import id.serviskus.app.domain.model.MaintenanceCategory
import id.serviskus.app.domain.model.MaintenanceStatus
import id.serviskus.app.domain.model.ServiceItem
import id.serviskus.app.domain.model.ServiceRecord
import id.serviskus.app.domain.model.UpcomingMaintenance
import id.serviskus.app.domain.model.Vehicle
import id.serviskus.app.domain.model.VehicleColor
import id.serviskus.app.domain.repository.ActiveVehicleRepository
import id.serviskus.app.domain.repository.ServiceRepository
import id.serviskus.app.domain.repository.UpcomingMaintenanceProvider
import id.serviskus.app.domain.repository.VehicleRepository
import id.serviskus.app.domain.usecase.ObserveActiveVehicleUseCase
import id.serviskus.app.domain.usecase.ObserveHomeDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `urutan state loading lalu konten serta pergantian motor aktif`() = runTest {
        val vario = Vehicle(
            id = 1L,
            name = "Vario 160 ABS",
            plateNumber = "B 4123 XYZ",
            currentOdometer = 12_450,
            color = VehicleColor.BIRU_OLI,
        )
        val supra = Vehicle(
            id = 2L,
            name = "Supra X 125",
            plateNumber = "AB 5381 KM",
            currentOdometer = 34_820,
            color = VehicleColor.TEAL,
        )
        val serviceRecord = ServiceRecord(
            id = 10L,
            vehicleId = 1L,
            date = LocalDate.of(2026, 11, 14),
            odometer = 12_450,
            workshop = "Bengkel Maju Jaya",
            totalCost = 158_500L,
            note = null,
            receiptImagePath = null,
            items = listOf(ServiceItem(id = 1L, recordId = 10L, name = "Oli mesin MPX2", cost = 58_500L)),
        )
        val vehicleRepo = FakeVehicleRepository(listOf(vario, supra))
        val activeRepo = FakeActiveVehicleRepository(initialId = 1L)
        val serviceRepo = FakeServiceRepository(listOf(serviceRecord))
        val upcomingProvider = FakeUpcomingProvider()
        val viewModel = createViewModel(vehicleRepo, activeRepo, serviceRepo, upcomingProvider)

        viewModel.uiState.test {
            assertEquals(HomeUiState.Loading, awaitItem())

            val firstContent = awaitItem() as HomeUiState.Content
            assertEquals("Vario 160 ABS", firstContent.activeVehicle.name)
            assertEquals("12.450", firstContent.activeVehicle.formattedOdometerValue)
            assertEquals(VehicleAccent.BIRU_OLI, firstContent.activeVehicle.accent)
            assertEquals("Rp 158.500", firstContent.recentServices.single().formattedTotalCost)

            viewModel.onSelectVehicle(2L)
            advanceUntilIdle()

            val switchedContent = awaitItem() as HomeUiState.Content
            assertEquals("Supra X 125", switchedContent.activeVehicle.name)
            assertEquals("34.820", switchedContent.activeVehicle.formattedOdometerValue)
            assertEquals(VehicleAccent.TEAL, switchedContent.activeVehicle.accent)
            assertTrue(switchedContent.recentServices.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `urutan state loading lalu kosong dan galat`() = runTest {
        val emptyVehicleRepo = FakeVehicleRepository(emptyList())
        val emptyViewModel = createViewModel(
            emptyVehicleRepo,
            FakeActiveVehicleRepository(null),
            FakeServiceRepository(emptyList()),
            FakeUpcomingProvider(),
        )

        emptyViewModel.uiState.test {
            assertEquals(HomeUiState.Loading, awaitItem())
            assertEquals(HomeUiState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        val failingRepo = object : VehicleRepository {
            override fun observeVehicles(): Flow<List<Vehicle>> = flow {
                throw IllegalStateException("Corrupt DB")
            }

            override suspend fun getVehicleById(vehicleId: Long): Vehicle? = null
            override suspend fun addVehicle(vehicle: Vehicle): Long = 0L
        }
        val errorViewModel = createViewModel(
            failingRepo,
            FakeActiveVehicleRepository(null),
            FakeServiceRepository(emptyList()),
            FakeUpcomingProvider(),
        )

        errorViewModel.uiState.test {
            assertEquals(HomeUiState.Loading, awaitItem())
            assertEquals(
                HomeUiState.Error(
                    messageRes = R.string.home_error_load_title,
                    actionHintRes = R.string.home_error_load_action,
                ),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createViewModel(
        vehicleRepo: VehicleRepository,
        activeRepo: ActiveVehicleRepository,
        serviceRepo: ServiceRepository,
        upcomingProvider: UpcomingMaintenanceProvider,
    ): HomeViewModel {
        val observeActiveVehicle = ObserveActiveVehicleUseCase(vehicleRepo, activeRepo)
        val observeHomeData = ObserveHomeDataUseCase(
            observeActiveVehicle = observeActiveVehicle,
            serviceRepository = serviceRepo,
            upcomingMaintenanceProvider = upcomingProvider,
        )
        return HomeViewModel(
            observeHomeData = observeHomeData,
            activeVehicleRepository = activeRepo,
        )
    }

    class MainDispatcherRule(
        private val dispatcher: TestDispatcher = StandardTestDispatcher(),
    ) : TestWatcher() {
        override fun starting(description: Description) {
            Dispatchers.setMain(dispatcher)
        }

        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }
    }

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

    private class FakeUpcomingProvider : UpcomingMaintenanceProvider {
        override fun observeUpcomingMaintenance(vehicle: Vehicle): Flow<List<UpcomingMaintenance>> =
            MutableStateFlow(
                listOf(
                    UpcomingMaintenance(
                        id = 1L,
                        vehicleId = vehicle.id,
                        partName = "Kampas rem depan",
                        category = MaintenanceCategory.BRAKES,
                        remainingKm = 320,
                        remainingDays = 12L,
                        status = MaintenanceStatus.SOON,
                    ),
                ),
            )
    }
}
