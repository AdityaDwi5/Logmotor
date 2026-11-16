package id.serviskus.app.data.repository

import id.serviskus.app.domain.model.UpcomingMaintenance
import id.serviskus.app.domain.model.Vehicle
import id.serviskus.app.domain.repository.UpcomingMaintenanceProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class EmptyUpcomingMaintenanceProvider @Inject constructor() : UpcomingMaintenanceProvider {
    override fun observeUpcomingMaintenance(vehicle: Vehicle): Flow<List<UpcomingMaintenance>> =
        flowOf(emptyList())
}
