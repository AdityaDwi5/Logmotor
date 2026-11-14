package id.serviskus.app.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.serviskus.app.data.local.ServisKuDatabase
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
}
