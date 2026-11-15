package id.serviskus.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.serviskus.app.data.local.dao.PartTypeDao
import id.serviskus.app.data.local.dao.ServiceRecordDao
import id.serviskus.app.data.local.dao.VehicleDao
import id.serviskus.app.data.local.entity.PartTypeEntity
import id.serviskus.app.data.local.entity.ServiceItemEntity
import id.serviskus.app.data.local.entity.ServiceRecordEntity
import id.serviskus.app.data.local.entity.VehicleEntity

@Database(
    entities = [
        VehicleEntity::class,
        PartTypeEntity::class,
        ServiceRecordEntity::class,
        ServiceItemEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class ServisKuDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun partTypeDao(): PartTypeDao
    abstract fun serviceRecordDao(): ServiceRecordDao
}
