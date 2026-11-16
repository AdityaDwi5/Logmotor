package id.serviskus.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.serviskus.app.data.repository.DataStoreActiveVehicleRepository
import id.serviskus.app.data.repository.RoomServiceRepository
import id.serviskus.app.data.repository.RoomVehicleRepository
import id.serviskus.app.domain.repository.ActiveVehicleRepository
import id.serviskus.app.domain.repository.ServiceRepository
import id.serviskus.app.domain.repository.VehicleRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVehicleRepository(impl: RoomVehicleRepository): VehicleRepository

    @Binds
    @Singleton
    abstract fun bindServiceRepository(impl: RoomServiceRepository): ServiceRepository

    @Binds
    @Singleton
    abstract fun bindActiveVehicleRepository(impl: DataStoreActiveVehicleRepository): ActiveVehicleRepository
}
