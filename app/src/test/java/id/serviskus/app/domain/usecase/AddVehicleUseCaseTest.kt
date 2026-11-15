package id.serviskus.app.domain.usecase

import id.serviskus.app.domain.model.Vehicle
import id.serviskus.app.domain.model.VehicleColor
import id.serviskus.app.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AddVehicleUseCaseTest {

    @Test
    fun `menambah motor valid memangkas spasi dan mengembalikan id`() = runTest {
        val repository = FakeVehicleRepository()
        val useCase = AddVehicleUseCase(repository)

        val result = useCase(
            AddVehicleRequest(
                name = "  NMAX 155  ",
                plateNumber = "   ",
                currentOdometer = 0,
                color = VehicleColor.TEAL,
            ),
        )

        assertEquals(AddVehicleResult.Success(1L), result)
        val saved = repository.stored.single()
        assertEquals("NMAX 155", saved.name)
        assertNull(saved.plateNumber)
        assertEquals(0, saved.currentOdometer)
        assertEquals(VehicleColor.TEAL, saved.color)
    }

    @Test
    fun `nama motor kosong ditolak tanpa menyimpan`() = runTest {
        val repository = FakeVehicleRepository()
        val useCase = AddVehicleUseCase(repository)

        val result = useCase(
            AddVehicleRequest(
                name = "   ",
                plateNumber = "B 1234 AA",
                currentOdometer = 1_200,
            ),
        )

        assertEquals(AddVehicleResult.BlankName, result)
        assertTrue(repository.stored.isEmpty())
    }

    @Test
    fun `odometer awal negatif ditolak tanpa menyimpan`() = runTest {
        val repository = FakeVehicleRepository()
        val useCase = AddVehicleUseCase(repository)

        val result = useCase(
            AddVehicleRequest(
                name = "Beat Street",
                plateNumber = "D 9876 ZZ",
                currentOdometer = -1,
            ),
        )

        assertEquals(AddVehicleResult.NegativeOdometer, result)
        assertTrue(repository.stored.isEmpty())
    }

    private class FakeVehicleRepository : VehicleRepository {
        val stored = mutableListOf<Vehicle>()
        private val flow = MutableStateFlow<List<Vehicle>>(emptyList())
        private var nextId = 1L

        override fun observeVehicles(): Flow<List<Vehicle>> = flow

        override suspend fun getVehicleById(vehicleId: Long): Vehicle? =
            stored.firstOrNull { it.id == vehicleId }

        override suspend fun addVehicle(vehicle: Vehicle): Long {
            val id = nextId++
            val saved = vehicle.copy(id = id)
            stored += saved
            flow.value = stored.toList()
            return id
        }
    }
}
