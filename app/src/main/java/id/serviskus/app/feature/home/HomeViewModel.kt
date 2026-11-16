package id.serviskus.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.serviskus.app.R
import id.serviskus.app.core.designsystem.VehicleAccent
import id.serviskus.app.core.format.ServisKuFormatter
import id.serviskus.app.domain.model.ServiceRecord
import id.serviskus.app.domain.model.UpcomingMaintenance
import id.serviskus.app.domain.model.Vehicle
import id.serviskus.app.domain.repository.ActiveVehicleRepository
import id.serviskus.app.domain.usecase.HomeData
import id.serviskus.app.domain.usecase.ObserveHomeDataUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val STOP_TIMEOUT_MILLIS = 5_000L
private const val MAX_RECENT_SERVICES = 5

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeHomeData: ObserveHomeDataUseCase,
    private val activeVehicleRepository: ActiveVehicleRepository,
) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<HomeUiState> = retryTrigger
        .flatMapLatest {
            observeHomeData()
                .map { data -> data.toUiState() }
                .catch { throwable ->
                    if (throwable is CancellationException) throw throwable
                    emit(
                        HomeUiState.Error(
                            messageRes = R.string.home_error_load_title,
                            actionHintRes = R.string.home_error_load_action,
                        ),
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = HomeUiState.Loading,
        )

    fun onSelectVehicle(vehicleId: Long) {
        viewModelScope.launch {
            activeVehicleRepository.setSelectedVehicleId(vehicleId)
        }
    }

    fun onRetryClick() {
        retryTrigger.value += 1
    }

    private fun HomeData.toUiState(): HomeUiState =
        when (this) {
            HomeData.Empty -> HomeUiState.Empty
            is HomeData.Content -> HomeUiState.Content(
                vehicles = vehicles.map { it.toSummaryUi() },
                activeVehicle = activeVehicle.toActiveUi(),
                upcomingMaintenance = upcomingMaintenance.map { it.toUi() },
                recentServices = recentServices
                    .take(MAX_RECENT_SERVICES)
                    .map { it.toTimelineUi() },
            )
        }

    private fun Vehicle.toSummaryUi(): VehicleSummaryUi = VehicleSummaryUi(
        id = id,
        name = name,
        plateNumber = plateNumber,
        accent = VehicleAccent.fromKey(color.key),
    )

    private fun Vehicle.toActiveUi(): ActiveVehicleUi = ActiveVehicleUi(
        id = id,
        name = name,
        plateNumber = plateNumber,
        formattedOdometerValue = ServisKuFormatter.formatNumber(currentOdometer),
        formattedOdometerFull = ServisKuFormatter.formatKilometers(currentOdometer),
        accent = VehicleAccent.fromKey(color.key),
    )

    private fun UpcomingMaintenance.toUi(): UpcomingMaintenanceUi = UpcomingMaintenanceUi(
        id = id,
        partName = partName,
        category = category,
        remainingKm = remainingKm,
        remainingDays = remainingDays,
        status = status,
    )

    private fun ServiceRecord.toTimelineUi(): ServiceTimelineItemUi = ServiceTimelineItemUi(
        id = id,
        formattedDate = ServisKuFormatter.formatDate(date),
        formattedOdometer = ServisKuFormatter.formatKilometers(odometer),
        formattedTotalCost = ServisKuFormatter.formatRupiah(totalCost),
        workshop = workshop,
        itemsSummary = items.joinToString(separator = ", ") { it.name },
    )
}
