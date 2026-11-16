package id.serviskus.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import id.serviskus.app.core.dispatcher.IoDispatcher
import id.serviskus.app.domain.repository.ActiveVehicleRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class DataStoreActiveVehicleRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ActiveVehicleRepository {

    override fun observeSelectedVehicleId(): Flow<Long?> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences -> preferences[ACTIVE_VEHICLE_ID_KEY] }
            .flowOn(ioDispatcher)

    override suspend fun setSelectedVehicleId(vehicleId: Long) {
        withContext(ioDispatcher) {
            dataStore.edit { preferences ->
                preferences[ACTIVE_VEHICLE_ID_KEY] = vehicleId
            }
        }
    }

    private companion object {
        val ACTIVE_VEHICLE_ID_KEY = longPreferencesKey("active_vehicle_id")
    }
}
