package id.serviskus.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.serviskus.app.data.repository.FakeUpcomingMaintenanceProvider
import id.serviskus.app.domain.repository.UpcomingMaintenanceProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UpcomingMaintenanceDebugModule {

    @Binds
    @Singleton
    abstract fun bindUpcomingMaintenanceProvider(
        impl: FakeUpcomingMaintenanceProvider,
    ): UpcomingMaintenanceProvider
}
