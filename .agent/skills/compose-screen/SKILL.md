---
name: compose-screen
description: Dipakai saat membuat atau mengubah layar, halaman, form, atau composable di ServisKu. Memuat pola Route/Screen, UiState, ViewModel, navigasi type-safe, preview, dan UI test.
---

# Layar Compose di ServisKu

Baca `DESIGN.md` dulu. Skill ini hanya soal struktur kode.

## Pola

Setiap layar punya lima bagian di `feature/<fitur>/`.

**1. UiState**: satu kelas immutable.

```kotlin
data class VehicleListUiState(
    val isLoading: Boolean = true,
    val vehicles: List<VehicleItem> = emptyList(),
    @StringRes val errorRes: Int? = null,
)
```

**2. ViewModel**

```kotlin
@HiltViewModel
class VehicleListViewModel @Inject constructor(
    observeVehicles: ObserveVehiclesUseCase,
) : ViewModel() {

    val uiState: StateFlow<VehicleListUiState> = observeVehicles()
        .map { list -> VehicleListUiState(isLoading = false, vehicles = list.map { it.toItem() }) }
        .catch { emit(VehicleListUiState(isLoading = false, errorRes = R.string.error_load_vehicles)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), VehicleListUiState())
}
```

**3. Route** mengambil ViewModel, **4. Screen** stateless:

```kotlin
@Composable
fun VehicleListRoute(
    onOpenVehicle: (Long) -> Unit,
    viewModel: VehicleListViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    VehicleListScreen(state = state, onOpenVehicle = onOpenVehicle)
}

@Composable
fun VehicleListScreen(
    state: VehicleListUiState,
    onOpenVehicle: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading -> LoadingContent(modifier)
        state.errorRes != null -> ErrorContent(stringResource(state.errorRes), modifier)
        state.vehicles.isEmpty() -> EmptyContent(modifier)
        else -> LazyColumn(modifier) {
            items(state.vehicles, key = { it.id }, contentType = { "vehicle" }) { item ->
                VehicleRow(item, onClick = { onOpenVehicle(item.id) })
            }
        }
    }
}
```

**5. Navigasi** type-safe (perlu plugin kotlinx.serialization):

```kotlin
@Serializable data object VehicleListDestination
@Serializable data class VehicleDetailDestination(val vehicleId: Long)

fun NavGraphBuilder.vehicleListScreen(onOpenVehicle: (Long) -> Unit) {
    composable<VehicleListDestination> { VehicleListRoute(onOpenVehicle) }
}

fun NavController.navigateToVehicleDetail(id: Long) = navigate(VehicleDetailDestination(id))
```

Di ViewModel tujuan, ambil argumen dengan `savedStateHandle.toRoute<VehicleDetailDestination>()`.

## Anotasi preview bersama

```kotlin
@Preview(name = "Terang", showBackground = true)
@Preview(name = "Gelap", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Font 200%", showBackground = true, fontScale = 2f)
annotation class ServisKuPreviews
```

Pasang `@ServisKuPreviews` pada preview setiap `Screen`, bungkus isinya dengan `ServisKuTheme`, dan isi dengan data yang realistis.

## Daftar periksa

- [ ] Empat kondisi tertangani: memuat, kosong, galat, berisi
- [ ] Edge-to-edge: pakai `paddingValues` dari `Scaffold`, form memakai `imePadding()`
- [ ] Isian form bertahan saat layar diputar (`rememberSaveable` atau state di ViewModel)
- [ ] `LazyColumn` punya `key` dan `contentType`
- [ ] Tidak ada warna, ukuran font, atau dp lepas di luar token `DESIGN.md`
- [ ] Semua teks di `strings.xml`
- [ ] Target sentuh 48dp, ikon bermakna punya `contentDescription`
- [ ] Ada test ViewModel (Turbine) dan satu UI test untuk alur utama

## Contoh UI test

```kotlin
@get:Rule val composeRule = createComposeRule()

@Test
fun `keadaan kosong menampilkan tombol tambah motor`() {
    composeRule.setContent {
        ServisKuTheme {
            VehicleListScreen(
                state = VehicleListUiState(isLoading = false),
                onOpenVehicle = {},
            )
        }
    }
    composeRule.onNodeWithText("Tambah motor").assertIsDisplayed()
}
```
