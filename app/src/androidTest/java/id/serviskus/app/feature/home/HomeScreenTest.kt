package id.serviskus.app.feature.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import id.serviskus.app.core.designsystem.ServisKuTheme
import id.serviskus.app.core.designsystem.VehicleAccent
import id.serviskus.app.domain.model.MaintenanceCategory
import id.serviskus.app.domain.model.MaintenanceStatus
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun keadaan_kosong_menampilkan_kalimat_dan_tombol_tambah_motor() {
        composeRule.setContent {
            ServisKuTheme {
                HomeScreen(
                    state = HomeUiState.Empty,
                    onSelectVehicle = {},
                    onUpdateOdometerClick = {},
                    onAddVehicleClick = {},
                    onRetryClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Belum ada motor tercatat.").assertIsDisplayed()
        composeRule.onNodeWithText("Tambah motor").assertIsDisplayed()
    }

    @Test
    fun keadaan_berisi_menampilkan_header_perawatan_dan_linimasa_servis() {
        val contentState = HomeUiState.Content(
            vehicles = listOf(
                VehicleSummaryUi(
                    id = 1L,
                    name = "Vario 160 ABS",
                    plateNumber = "B 4123 XYZ",
                    accent = VehicleAccent.BIRU_OLI,
                ),
            ),
            activeVehicle = ActiveVehicleUi(
                id = 1L,
                name = "Vario 160 ABS",
                plateNumber = "B 4123 XYZ",
                formattedOdometerValue = "12.450",
                formattedOdometerFull = "12.450 km",
                accent = VehicleAccent.BIRU_OLI,
            ),
            upcomingMaintenance = listOf(
                UpcomingMaintenanceUi(
                    id = 1L,
                    partName = "Kampas rem depan",
                    category = MaintenanceCategory.BRAKES,
                    remainingKm = 320,
                    remainingDays = 12L,
                    status = MaintenanceStatus.SOON,
                ),
            ),
            recentServices = listOf(
                ServiceTimelineItemUi(
                    id = 10L,
                    formattedDate = "14 Nov 2026",
                    formattedOdometer = "12.450 km",
                    formattedTotalCost = "Rp 158.500",
                    workshop = "Bengkel Maju Jaya",
                    itemsSummary = "Oli mesin MPX2",
                ),
            ),
        )

        composeRule.setContent {
            ServisKuTheme(accent = VehicleAccent.BIRU_OLI) {
                HomeScreen(
                    state = contentState,
                    onSelectVehicle = {},
                    onUpdateOdometerClick = {},
                    onAddVehicleClick = {},
                    onRetryClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Vario 160 ABS").assertIsDisplayed()
        composeRule.onNodeWithText("B 4123 XYZ").assertIsDisplayed()
        composeRule.onNodeWithText("12.450").assertIsDisplayed()
        composeRule.onNodeWithText("Perbarui km").assertIsDisplayed()
        composeRule.onNodeWithText("Perawatan mendatang").assertIsDisplayed()
        composeRule.onNodeWithText("Kampas rem depan").assertIsDisplayed()
        composeRule.onNodeWithText("320 km lagi atau 12 hari").assertIsDisplayed()
        composeRule.onNodeWithText("Segera").assertIsDisplayed()
        composeRule.onNodeWithText("Riwayat servis terakhir").assertIsDisplayed()
        composeRule.onNodeWithText("Rp 158.500").assertIsDisplayed()
        composeRule.onNodeWithText("12.450 km · Bengkel Maju Jaya").assertIsDisplayed()
    }

    @Test
    fun keadaan_motor_ada_perawatan_kosong_menampilkan_pesan_perawatan_kosong() {
        val stateWithEmptyLists = HomeUiState.Content(
            vehicles = listOf(
                VehicleSummaryUi(
                    id = 1L,
                    name = "Supra X 125",
                    plateNumber = "AB 5381 KM",
                    accent = VehicleAccent.TEAL,
                ),
            ),
            activeVehicle = ActiveVehicleUi(
                id = 1L,
                name = "Supra X 125",
                plateNumber = "AB 5381 KM",
                formattedOdometerValue = "8.200",
                formattedOdometerFull = "8.200 km",
                accent = VehicleAccent.TEAL,
            ),
            upcomingMaintenance = emptyList(),
            recentServices = emptyList(),
        )

        composeRule.setContent {
            ServisKuTheme(accent = VehicleAccent.TEAL) {
                HomeScreen(
                    state = stateWithEmptyLists,
                    onSelectVehicle = {},
                    onUpdateOdometerClick = {},
                    onAddVehicleClick = {},
                    onRetryClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Supra X 125").assertIsDisplayed()
        composeRule.onNodeWithText("Belum ada jadwal perawatan tercatat.").assertIsDisplayed()
        composeRule.onNodeWithText("Belum ada servis tercatat.").assertIsDisplayed()
    }
}
