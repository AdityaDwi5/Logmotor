package id.serviskus.app.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.serviskus.app.data.local.ServisKuDatabase
import id.serviskus.app.data.local.dao.PartTypeDao
import id.serviskus.app.data.local.dao.ServiceRecordDao
import id.serviskus.app.data.local.dao.VehicleDao
import javax.inject.Singleton

private const val DATABASE_NAME = "servisku.db"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideServisKuDatabase(
        @ApplicationContext context: Context,
    ): ServisKuDatabase =
        Room.databaseBuilder(
            context,
            ServisKuDatabase::class.java,
            DATABASE_NAME,
        ).build()

    @Provides
    fun provideVehicleDao(database: ServisKuDatabase): VehicleDao = database.vehicleDao()

    @Provides
    fun providePartTypeDao(database: ServisKuDatabase): PartTypeDao = database.partTypeDao()

    @Provides
    fun provideServiceRecordDao(database: ServisKuDatabase): ServiceRecordDao = database.serviceRecordDao()
}
