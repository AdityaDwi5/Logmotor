---
name: room-entity
description: Dipakai saat membuat atau mengubah entity, DAO, TypeConverter, atau query Room di ServisKu, termasuk relasi motor, servis, dan item servis.
---

# Entity dan DAO Room di ServisKu

Aturan tipe data: uang `Long` rupiah, kilometer `Int`, tanggal `LocalDate` (disimpan epoch day). Perubahan skema selalu lewat `/db-migration`.

## Entity

```kotlin
@Entity(
    tableName = "service_record",
    foreignKeys = [ForeignKey(
        entity = VehicleEntity::class,
        parentColumns = ["id"],
        childColumns = ["vehicle_id"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index(value = ["vehicle_id", "date"])],
)
data class ServiceRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "vehicle_id") val vehicleId: Long,
    val date: LocalDate,
    val odometer: Int,
    val workshop: String?,
    @ColumnInfo(name = "total_cost") val totalCost: Long,
    val note: String?,
    @ColumnInfo(name = "receipt_image_path") val receiptImagePath: String?,
)

@Entity(
    tableName = "service_item",
    foreignKeys = [ForeignKey(
        entity = ServiceRecordEntity::class,
        parentColumns = ["id"],
        childColumns = ["record_id"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("record_id")],
)
data class ServiceItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "record_id") val recordId: Long,
    @ColumnInfo(name = "part_type_id") val partTypeId: Long?,
    val name: String,
    val cost: Long,
)
```

Indeks komposit `(vehicle_id, date)` sudah melayani pencarian lewat `vehicle_id`, jadi tidak perlu indeks terpisah untuk kolom itu.

## TypeConverter

```kotlin
class Converters {
    @TypeConverter fun fromEpochDay(value: Long?): LocalDate? = value?.let(LocalDate::ofEpochDay)
    @TypeConverter fun toEpochDay(date: LocalDate?): Long? = date?.toEpochDay()
}
```

Daftarkan dengan `@TypeConverters(Converters::class)` pada kelas `@Database`. Karena tanggal disimpan sebagai angka, `ORDER BY date` mengurutkan dengan benar.

## DAO

Untuk operasi yang butuh `@Transaction` di atas beberapa `@Insert`, pakai `abstract class`, bukan interface, agar metode terbuka (`open`) didukung tanpa konfigurasi tambahan.

```kotlin
@Dao
abstract class ServiceRecordDao {

    @Query("SELECT * FROM service_record WHERE vehicle_id = :vehicleId ORDER BY date DESC, odometer DESC")
    abstract fun observeByVehicle(vehicleId: Long): Flow<List<ServiceRecordEntity>>

    @Insert abstract suspend fun insertRecord(record: ServiceRecordEntity): Long
    @Insert abstract suspend fun insertItems(items: List<ServiceItemEntity>)

    @Transaction
    open suspend fun insertWithItems(
        record: ServiceRecordEntity,
        items: List<ServiceItemEntity>,
    ): Long {
        val recordId = insertRecord(record)
        insertItems(items.map { it.copy(recordId = recordId) })
        return recordId
    }
}
```

## Mapper

Entity tidak keluar dari paket `data`. Petakan ke model domain lewat fungsi ekstensi `toDomain()` dan `toEntity()` di `data/mapper/`.

## Skema

Pastikan `exportSchema = true` dan lokasi skema diatur, lewat Room Gradle Plugin (`room { schemaDirectory("$projectDir/schemas") }`) atau argumen KSP `room.schemaLocation`, mana pun yang sudah dipakai proyek. Folder `schemas/` ikut di-commit.

## Test yang harus ada

- Hapus motor ikut menghapus servis dan item-nya
- `insertWithItems` bersifat atomik: jika menyimpan item gagal, record ikut dibatalkan
- `observeByVehicle` mengurutkan tanggal terbaru lebih dulu, dan odometer terbesar lebih dulu pada tanggal yang sama
- Round trip `LocalDate` dan `Long` untuk nilai batas (0, 1.000.000.000)
