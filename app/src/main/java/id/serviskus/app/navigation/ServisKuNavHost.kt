package id.serviskus.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import id.serviskus.app.feature.placeholder.PlaceholderRoute
import kotlinx.serialization.Serializable

@Serializable
data object PlaceholderDestination

@Composable
fun ServisKuNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = PlaceholderDestination,
        modifier = modifier,
    ) {
        composable<PlaceholderDestination> {
            PlaceholderRoute()
        }
    }
}
