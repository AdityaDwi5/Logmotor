package id.serviskus.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.serviskus.app.core.designsystem.VehicleAccent
import id.serviskus.app.domain.usecase.ObserveActiveVehicleUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val STOP_TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class MainViewModel @Inject constructor(
    observeActiveVehicle: ObserveActiveVehicleUseCase,
) : ViewModel() {

    val activeAccent: StateFlow<VehicleAccent> = observeActiveVehicle()
        .map { selection -> VehicleAccent.fromKey(selection?.activeVehicle?.color?.key) }
        .catch { throwable ->
            if (throwable is CancellationException) throw throwable
            emit(VehicleAccent.DEFAULT)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = VehicleAccent.DEFAULT,
        )
}
