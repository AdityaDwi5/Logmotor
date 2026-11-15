package id.serviskus.app.data.repository

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import id.serviskus.app.data.local.ServisKuDatabase
import id.serviskus.app.data.local.entity.VehicleEntity
import id.serviskus.app.domain.model.ServiceItem
import id.serviskus.app.domain.model.ServiceRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class RoomServiceRepositoryTest {

    private lateinit var database: ServisKuDatabase
    private lateinit var repository: RoomServiceRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ServisKuDatabase::class.java).build()
        repository = RoomServiceRepository(
            database = database,
            serviceRecordDao = database.serviceRecordDao(),
            vehicleDao = database.vehicleDao(),
            ioDispatcher = Dispatchers.IO,
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addServiceRecord_menaikkan_currentOdometer_dan_abaikan_km_lebih_rendah() = runTest {
        val vehicleId = insertVehicle(initialOdometer = 10_000)

        repository.addServiceRecord(
            ServiceRecord(
                vehicleId = vehicleId,
                date = LocalDate.of(2026, 11, 15),
                odometer = 12_450,
                workshop = "Bengkel Maju",
                totalCost = 90_000L,
                note = null,
                receiptImagePath = null,
                items = listOf(ServiceItem(name = "Oli mesin", cost = 90_000L)),
            ),
        )
        assertEquals(12_450, database.vehicleDao().getById(vehicleId)?.currentOdometer)

        repository.addServiceRecord(
            ServiceRecord(
                vehicleId = vehicleId,
                date = LocalDate.of(2026, 8, 1),
                odometer = 9_100,
                workshop = null,
                totalCost = 55_000L,
                note = null,
                receiptImagePath = null,
            ),
        )
        assertEquals(12_450, database.vehicleDao().getById(vehicleId)?.currentOdometer)
    }

    @Test
    fun addServiceRecord_atomik_membatalkan_pembaruan_odometer_saat_item_gagal() = runTest {
        val vehicleId = insertVehicle(initialOdometer = 10_000)

        try {
            repository.addServiceRecord(
                ServiceRecord(
                    vehicleId = vehicleId,
                    date = LocalDate.of(2026, 11, 15),
                    odometer = 15_000,
                    workshop = "Bengkel Maju",
                    totalCost = 70_000L,
                    note = null,
                    receiptImagePath = null,
                    items = listOf(
                        ServiceItem(
                            partTypeId = 999L,
                            name = "Item dengan partType invalid",
                            cost = 70_000L,
                        ),
                    ),
                ),
            )
            fail("Expected SQLiteConstraintException when inserting invalid partTypeId")
        } catch (_: SQLiteConstraintException) {
            // Expected foreign key violation inside transaction.
        }

        assertEquals(10_000, database.vehicleDao().getById(vehicleId)?.currentOdometer)
        assertTrue(repository.observeServiceRecords(vehicleId).first().isEmpty())
    }

    private suspend fun insertVehicle(initialOdometer: Int): Long =
        database.vehicleDao().insert(
            VehicleEntity(
                name = "Vario 160",
                plateNumber = "B 4123 XYZ",
                currentOdometer = initialOdometer,
                colorKey = "biru-oli",
            ),
        )
}
