package id.serviskus.app.data.local

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import id.serviskus.app.data.local.dao.PartTypeDao
import id.serviskus.app.data.local.dao.ServiceRecordDao
import id.serviskus.app.data.local.dao.VehicleDao
import id.serviskus.app.data.local.entity.PartTypeEntity
import id.serviskus.app.data.local.entity.ServiceItemEntity
import id.serviskus.app.data.local.entity.ServiceRecordEntity
import id.serviskus.app.data.local.entity.VehicleEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class ServisKuDaoTest {

    private lateinit var database: ServisKuDatabase
    private lateinit var vehicleDao: VehicleDao
    private lateinit var partTypeDao: PartTypeDao
    private lateinit var serviceRecordDao: ServiceRecordDao

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ServisKuDatabase::class.java).build()
        vehicleDao = database.vehicleDao()
        partTypeDao = database.partTypeDao()
        serviceRecordDao = database.serviceRecordDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun hapus_motor_ikut_menghapus_servis_dan_itemnya() = runTest {
        val vehicleId = insertSampleVehicle()
        val recordId = serviceRecordDao.insertWithItems(
            record = ServiceRecordEntity(
                vehicleId = vehicleId,
                date = LocalDate.of(2026, 11, 1),
                odometer = 12_000,
                workshop = "Bengkel Jaya",
                totalCost = 75_000L,
                note = null,
                receiptImagePath = null,
            ),
            items = listOf(
                ServiceItemEntity(
                    recordId = 0L,
                    partTypeId = null,
                    name = "Oli mesin",
                    cost = 75_000L,
                ),
            ),
        )

        vehicleDao.deleteById(vehicleId)

        assertTrue(serviceRecordDao.observeByVehicle(vehicleId).first().isEmpty())
        assertTrue(serviceRecordDao.getItemsByRecordId(recordId).isEmpty())
    }

    @Test
    fun hapus_PartType_membuat_partTypeId_pada_ServiceItem_menjadi_null() = runTest {
        val vehicleId = insertSampleVehicle()
        val partTypeId = partTypeDao.insert(
            PartTypeEntity(
                name = "Oli mesin",
                defaultIntervalKm = 2_000,
                defaultIntervalMonths = 3,
            ),
        )
        val recordId = serviceRecordDao.insertWithItems(
            record = ServiceRecordEntity(
                vehicleId = vehicleId,
                date = LocalDate.of(2026, 11, 2),
                odometer = 12_500,
                workshop = null,
                totalCost = 62_000L,
                note = null,
                receiptImagePath = null,
            ),
            items = listOf(
                ServiceItemEntity(
                    recordId = 0L,
                    partTypeId = partTypeId,
                    name = "Oli mesin",
                    cost = 62_000L,
                ),
            ),
        )

        partTypeDao.deleteById(partTypeId)

        val items = serviceRecordDao.getItemsByRecordId(recordId)
        assertEquals(1, items.size)
        assertNull(items.single().partTypeId)
        assertEquals("Oli mesin", items.single().name)
    }

    @Test
    fun insertWithItems_bersifat_atomik_saat_simpan_item_gagal() = runTest {
        val vehicleId = insertSampleVehicle()

        try {
            serviceRecordDao.insertWithItems(
                record = ServiceRecordEntity(
                    vehicleId = vehicleId,
                    date = LocalDate.of(2026, 11, 3),
                    odometer = 13_000,
                    workshop = "AHASS",
                    totalCost = 50_000L,
                    note = null,
                    receiptImagePath = null,
                ),
                items = listOf(
                    ServiceItemEntity(
                        recordId = 0L,
                        partTypeId = 999L,
                        name = "Komponen tidak ada",
                        cost = 50_000L,
                    ),
                ),
            )
            fail("Expected SQLiteConstraintException for missing partTypeId")
        } catch (_: SQLiteConstraintException) {
            // Expected foreign key failure to test transaction rollback.
        }

        assertTrue(serviceRecordDao.observeByVehicle(vehicleId).first().isEmpty())
    }

    @Test
    fun observeByVehicle_urutkan_tanggal_terbaru_lalu_odometer_terbesar() = runTest {
        val vehicleId = insertSampleVehicle()
        val olderDate = LocalDate.of(2026, 10, 15)
        val sameDate = LocalDate.of(2026, 11, 10)
        val newestDate = LocalDate.of(2026, 11, 14)

        val oldestId = serviceRecordDao.insertRecord(
            ServiceRecordEntity(
                vehicleId = vehicleId,
                date = olderDate,
                odometer = 11_000,
                workshop = null,
                totalCost = 50_000L,
                note = null,
                receiptImagePath = null,
            ),
        )
        val sameDateLowerKmId = serviceRecordDao.insertRecord(
            ServiceRecordEntity(
                vehicleId = vehicleId,
                date = sameDate,
                odometer = 12_100,
                workshop = null,
                totalCost = 40_000L,
                note = null,
                receiptImagePath = null,
            ),
        )
        val sameDateHigherKmId = serviceRecordDao.insertRecord(
            ServiceRecordEntity(
                vehicleId = vehicleId,
                date = sameDate,
                odometer = 12_450,
                workshop = null,
                totalCost = 85_000L,
                note = null,
                receiptImagePath = null,
            ),
        )
        val newestId = serviceRecordDao.insertRecord(
            ServiceRecordEntity(
                vehicleId = vehicleId,
                date = newestDate,
                odometer = 12_900,
                workshop = null,
                totalCost = 120_000L,
                note = null,
                receiptImagePath = null,
            ),
        )

        val observedIds = serviceRecordDao.observeByVehicle(vehicleId).first().map { it.id }

        assertEquals(
            listOf(newestId, sameDateHigherKmId, sameDateLowerKmId, oldestId),
            observedIds,
        )
    }

    @Test
    fun round_trip_LocalDate_dan_Long_nilai_batas_nol_dan_satu_miliar() = runTest {
        val vehicleId = insertSampleVehicle()
        val epochDate = LocalDate.ofEpochDay(0L)
        val modernDate = LocalDate.of(2026, 11, 15)

        serviceRecordDao.insertWithItems(
            record = ServiceRecordEntity(
                vehicleId = vehicleId,
                date = modernDate,
                odometer = 1_000_000_000,
                workshop = "Bengkel Resmi",
                totalCost = 1_000_000_000L,
                note = null,
                receiptImagePath = null,
            ),
            items = listOf(
                ServiceItemEntity(
                    recordId = 0L,
                    partTypeId = null,
                    name = "Overhaul",
                    cost = 1_000_000_000L,
                ),
            ),
        )
        serviceRecordDao.insertWithItems(
            record = ServiceRecordEntity(
                vehicleId = vehicleId,
                date = epochDate,
                odometer = 0,
                workshop = null,
                totalCost = 0L,
                note = null,
                receiptImagePath = null,
            ),
            items = listOf(
                ServiceItemEntity(
                    recordId = 0L,
                    partTypeId = null,
                    name = "Cek garansi",
                    cost = 0L,
                ),
            ),
        )

        val records = serviceRecordDao.observeWithItemsByVehicle(vehicleId).first()
        assertEquals(2, records.size)
        assertEquals(modernDate, records[0].record.date)
        assertEquals(1_000_000_000L, records[0].record.totalCost)
        assertEquals(1_000_000_000L, records[0].items.single().cost)
        assertEquals(epochDate, records[1].record.date)
        assertEquals(0L, records[1].record.totalCost)
        assertEquals(0L, records[1].items.single().cost)
    }

    private suspend fun insertSampleVehicle(): Long =
        vehicleDao.insert(
            VehicleEntity(
                name = "Vario 160",
                plateNumber = "B 4123 XYZ",
                currentOdometer = 10_000,
                colorKey = "biru-oli",
            ),
        )
}
