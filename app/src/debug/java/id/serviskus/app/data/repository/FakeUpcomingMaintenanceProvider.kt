package id.serviskus.app.data.repository

import id.serviskus.app.domain.model.MaintenanceCategory
import id.serviskus.app.domain.model.MaintenanceStatus
import id.serviskus.app.domain.model.UpcomingMaintenance
import id.serviskus.app.domain.model.Vehicle
import id.serviskus.app.domain.repository.UpcomingMaintenanceProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * Temporary sample provider for debug builds until ReminderRule is implemented.
 */
class FakeUpcomingMaintenanceProvider @Inject constructor() : UpcomingMaintenanceProvider {

    override fun observeUpcomingMaintenance(vehicle: Vehicle): Flow<List<UpcomingMaintenance>> {
        val items = if (vehicle.id % 2L == 1L) {
            listOf(
                UpcomingMaintenance(
                    id = 1L,
                    vehicleId = vehicle.id,
                    partName = "Oli mesin",
                    category = MaintenanceCategory.ENGINE_OIL,
                    remainingKm = -140,
                    remainingDays = -2L,
                    status = MaintenanceStatus.OVERDUE,
                ),
                UpcomingMaintenance(
                    id = 2L,
                    vehicleId = vehicle.id,
                    partName = "Kampas rem depan",
                    category = MaintenanceCategory.BRAKES,
                    remainingKm = 320,
                    remainingDays = 12L,
                    status = MaintenanceStatus.SOON,
                ),
                UpcomingMaintenance(
                    id = 3L,
                    vehicleId = vehicle.id,
                    partName = "V-belt dan roller",
                    category = MaintenanceCategory.DRIVE_BELT,
                    remainingKm = 1_450,
                    remainingDays = 34L,
                    status = MaintenanceStatus.OK,
                ),
                UpcomingMaintenance(
                    id = 4L,
                    vehicleId = vehicle.id,
                    partName = "Filter udara",
                    category = MaintenanceCategory.AIR_FILTER,
                    remainingKm = 2_780,
                    remainingDays = 65L,
                    status = MaintenanceStatus.OK,
                ),
            )
        } else {
            listOf(
                UpcomingMaintenance(
                    id = 11L,
                    vehicleId = vehicle.id,
                    partName = "Oli gardan",
                    category = MaintenanceCategory.ENGINE_OIL,
                    remainingKm = 480,
                    remainingDays = 9L,
                    status = MaintenanceStatus.SOON,
                ),
                UpcomingMaintenance(
                    id = 12L,
                    vehicleId = vehicle.id,
                    partName = "Busi",
                    category = MaintenanceCategory.SPARK_PLUG,
                    remainingKm = 3_120,
                    remainingDays = null,
                    status = MaintenanceStatus.OK,
                ),
            )
        }
        return flowOf(items)
    }
}
