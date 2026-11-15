package id.serviskus.app.domain.usecase

import id.serviskus.app.domain.model.ServiceRecord
import id.serviskus.app.domain.model.Vehicle
import id.serviskus.app.domain.model.VehicleColor
import id.serviskus.app.domain.repository.ServiceRepository
import id.serviskus.app.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class ObserveVehiclesAndServicesUseCaseTest {

    @Test
    fun `ObserveVehicles memancarkan daftar motor dari repository`() = runTest {
        val vehicleRepo = FakeVehicleRepository(
            listOf(
                Vehicle(id = 1L, name = "Supra X", plateNumber = "AB 1234 CD", currentOdometer = 32_100),
                Vehicle(
                    id = 2L,
                    name = "PCX 160",
                    plateNumber = null,
                    currentOdometer = 4_500,
                    color = VehicleColor.UNGU,
                ),
            ),
        )
        val useCase = ObserveVehiclesUseCase(vehicleRepo)

        val vehicles = useCase().first()

        assertEquals(2, vehicles.size)
        assertEquals("Supra X", vehicles[0].name)
        assertEquals("PCX 160", vehicles[1].name)
    }

    @Test
    fun `ObserveServiceRecords hanya memancarkan catatan milik motor terkait`() = runTest {
        val targetRecord = ServiceRecord(
            id = 10L,
            vehicleId = 1L,
            date = LocalDate.of(2026, 11, 10),
            odometer = 12_450,
            workshop = "AHASS Merdeka",
            totalCost = 95_000L,
            note = null,
            receiptImagePath = null,
        )
        val otherVehicleRecord = targetRecord.copy(id = 11L, vehicleId = 2L)
        val serviceRepo = FakeServiceRepository(listOf(targetRecord, otherVehicleRecord))
        val useCase = ObserveServiceRecordsUseCase(serviceRepo)

        val records = useCase(vehicleId = 1L).first()

        assertEquals(listOf(targetRecord), records)
    }

    private class FakeVehicleRepository(initial: List<Vehicle>) : VehicleRepository {
        private val flow = MutableStateFlow(initial)
        override fun observeVehicles(): Flow<List<Vehicle>> = flow
        override suspend fun getVehicleById(vehicleId: Long): Vehicle? = flow.value.firstOrNull { it.id == vehicleId }
        override suspend fun addVehicle(vehicle: Vehicle): Long = vehicle.id
    }

    private class FakeServiceRepository(initial: List<ServiceRecord>) : ServiceRepository {
        private val flow = MutableStateFlow(initial)
        override fun observeServiceRecords(vehicleId: Long): Flow<List<ServiceRecord>> =
            flow.map { list -> list.filter { it.vehicleId == vehicleId } }

        override suspend fun getServiceRecords(vehicleId: Long): List<ServiceRecord> =
            flow.value.filter { it.vehicleId == vehicleId }

        override suspend fun addServiceRecord(record: ServiceRecord): Long = record.id
    }
}
