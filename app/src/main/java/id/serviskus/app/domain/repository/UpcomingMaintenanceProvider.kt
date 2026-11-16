package id.serviskus.app.domain.repository

import id.serviskus.app.domain.model.UpcomingMaintenance
import id.serviskus.app.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface UpcomingMaintenanceProvider {
    fun observeUpcomingMaintenance(vehicle: Vehicle): Flow<List<UpcomingMaintenance>>
}
