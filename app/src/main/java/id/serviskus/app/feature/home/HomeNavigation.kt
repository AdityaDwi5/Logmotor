package id.serviskus.app.feature.home

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object HomeDestination

fun NavGraphBuilder.homeScreen(
    snackbarHostState: SnackbarHostState,
    onAddVehicleClick: () -> Unit,
    onUpdateOdometerClick: (Long) -> Unit,
) {
    composable<HomeDestination> {
        HomeRoute(
            snackbarHostState = snackbarHostState,
            onAddVehicleClick = onAddVehicleClick,
            onUpdateOdometerClick = onUpdateOdometerClick,
        )
    }
}
