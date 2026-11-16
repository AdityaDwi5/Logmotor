package id.serviskus.app.feature.home

import androidx.annotation.StringRes
import id.serviskus.app.core.designsystem.VehicleAccent
import id.serviskus.app.domain.model.MaintenanceCategory
import id.serviskus.app.domain.model.MaintenanceStatus

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data object Empty : HomeUiState

    data class Error(
        @param:StringRes val messageRes: Int,
        @param:StringRes val actionHintRes: Int,
    ) : HomeUiState

    data class Content(
        val vehicles: List<VehicleSummaryUi>,
        val activeVehicle: ActiveVehicleUi,
        val upcomingMaintenance: List<UpcomingMaintenanceUi>,
        val recentServices: List<ServiceTimelineItemUi>,
    ) : HomeUiState
}

data class VehicleSummaryUi(
    val id: Long,
    val name: String,
    val plateNumber: String?,
    val accent: VehicleAccent,
)

data class ActiveVehicleUi(
    val id: Long,
    val name: String,
    val plateNumber: String?,
    val formattedOdometerValue: String,
    val formattedOdometerFull: String,
    val accent: VehicleAccent,
)

data class UpcomingMaintenanceUi(
    val id: Long,
    val partName: String,
    val category: MaintenanceCategory,
    val remainingKm: Int?,
    val remainingDays: Long?,
    val status: MaintenanceStatus,
)

data class ServiceTimelineItemUi(
    val id: Long,
    val formattedDate: String,
    val formattedOdometer: String,
    val formattedTotalCost: String,
    val workshop: String?,
    val itemsSummary: String,
)
