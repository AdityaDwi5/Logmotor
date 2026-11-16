package id.serviskus.app.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import id.serviskus.app.R
import id.serviskus.app.feature.home.HomeDestination
import id.serviskus.app.feature.home.homeScreen
import kotlinx.coroutines.launch

@Composable
fun ServisKuNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val addVehicleUnavailableMessage = stringResource(R.string.home_notice_add_vehicle_unavailable)
    val updateOdometerUnavailableMessage =
        stringResource(R.string.home_notice_update_odometer_unavailable)

    NavHost(
        navController = navController,
        startDestination = HomeDestination,
        modifier = modifier,
    ) {
        homeScreen(
            snackbarHostState = snackbarHostState,
            onAddVehicleClick = {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(addVehicleUnavailableMessage)
                }
            },
            onUpdateOdometerClick = {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(updateOdometerUnavailableMessage)
                }
            },
        )
    }
}
