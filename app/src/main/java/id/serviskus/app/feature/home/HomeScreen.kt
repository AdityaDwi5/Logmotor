package id.serviskus.app.feature.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.serviskus.app.R
import id.serviskus.app.core.designsystem.LocalStatusColors
import id.serviskus.app.core.designsystem.ServisKuPreviews
import id.serviskus.app.core.designsystem.ServisKuTheme
import id.serviskus.app.core.designsystem.Spacing
import id.serviskus.app.core.designsystem.VehicleAccent
import id.serviskus.app.core.designsystem.mono
import id.serviskus.app.core.designsystem.odometerHero
import id.serviskus.app.core.designsystem.tabular
import id.serviskus.app.core.format.ServisKuFormatter
import id.serviskus.app.domain.model.MaintenanceCategory
import id.serviskus.app.domain.model.MaintenanceStatus

@Composable
fun HomeRoute(
    onAddVehicleClick: () -> Unit,
    onUpdateOdometerClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        state = uiState,
        onSelectVehicle = viewModel::onSelectVehicle,
        onUpdateOdometerClick = onUpdateOdometerClick,
        onAddVehicleClick = onAddVehicleClick,
        onRetryClick = viewModel::onRetryClick,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onSelectVehicle: (Long) -> Unit,
    onUpdateOdometerClick: (Long) -> Unit,
    onAddVehicleClick: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (state) {
                HomeUiState.Loading -> HomeLoadingContent()
                HomeUiState.Empty -> HomeEmptyContent(onAddVehicleClick = onAddVehicleClick)
                is HomeUiState.Error -> HomeErrorContent(
                    state = state,
                    onRetryClick = onRetryClick,
                )

                is HomeUiState.Content -> HomeLoadedContent(
                    state = state,
                    onSelectVehicle = onSelectVehicle,
                    onUpdateOdometerClick = onUpdateOdometerClick,
                )
            }
        }
    }
}

@Composable
private fun HomeLoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun HomeEmptyContent(
    onAddVehicleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Spacing.base),
        verticalArrangement = Arrangement.spacedBy(Spacing.base),
    ) {
        Text(
            text = stringResource(R.string.home_empty_message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Button(
            onClick = onAddVehicleClick,
            modifier = Modifier.heightIn(min = Spacing.xxl),
            shape = MaterialTheme.shapes.small,
        ) {
            Text(
                text = stringResource(R.string.home_action_add_vehicle),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun HomeErrorContent(
    state: HomeUiState.Error,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Spacing.base),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = stringResource(state.messageRes),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = stringResource(state.actionHintRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        Button(
            onClick = onRetryClick,
            modifier = Modifier.heightIn(min = Spacing.xxl),
            shape = MaterialTheme.shapes.small,
        ) {
            Text(
                text = stringResource(R.string.home_action_retry),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun HomeLoadedContent(
    state: HomeUiState.Content,
    onSelectVehicle: (Long) -> Unit,
    onUpdateOdometerClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.base),
        verticalArrangement = Arrangement.Top,
    ) {
        item(
            key = "active-vehicle-header",
            contentType = "vehicle_header",
        ) {
            ActiveVehicleHeaderCard(
                activeVehicle = state.activeVehicle,
                vehicles = state.vehicles,
                onSelectVehicle = onSelectVehicle,
                onUpdateOdometerClick = { onUpdateOdometerClick(state.activeVehicle.id) },
            )
        }

        item(
            key = "heading-upcoming",
            contentType = "section_heading",
        ) {
            SectionHeading(
                title = stringResource(R.string.home_section_upcoming),
                modifier = Modifier.padding(top = Spacing.lg, bottom = Spacing.sm),
            )
        }

        if (state.upcomingMaintenance.isEmpty()) {
            item(
                key = "upcoming-empty",
                contentType = "empty_section",
            ) {
                Text(
                    text = stringResource(R.string.home_upcoming_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = Spacing.sm),
                )
            }
        } else {
            itemsIndexed(
                items = state.upcomingMaintenance,
                key = { _, item -> "upcoming-${item.id}" },
                contentType = { _, _ -> "upcoming_maintenance" },
            ) { index, item ->
                UpcomingMaintenanceRow(item = item)
                if (index < state.upcomingMaintenance.lastIndex) {
                    HorizontalDivider(
                        thickness = Spacing.strokeThin,
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
            }
        }

        item(
            key = "heading-history",
            contentType = "section_heading",
        ) {
            SectionHeading(
                title = stringResource(R.string.home_section_history),
                modifier = Modifier.padding(top = Spacing.lg, bottom = Spacing.sm),
            )
        }

        if (state.recentServices.isEmpty()) {
            item(
                key = "history-empty",
                contentType = "empty_section",
            ) {
                Text(
                    text = stringResource(R.string.home_history_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = Spacing.sm),
                )
            }
        } else {
            itemsIndexed(
                items = state.recentServices,
                key = { _, item -> "history-${item.id}" },
                contentType = { _, _ -> "service_timeline" },
            ) { index, item ->
                ServiceTimelineRow(
                    item = item,
                    isLast = index == state.recentServices.lastIndex,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ActiveVehicleHeaderCard(
    activeVehicle: ActiveVehicleUi,
    vehicles: List<VehicleSummaryUi>,
    onSelectVehicle: (Long) -> Unit,
    onUpdateOdometerClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var pickerExpanded by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = Spacing.strokeThin,
            color = MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(Spacing.base),
            verticalArrangement = Arrangement.spacedBy(Spacing.base),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_two_wheeler),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Spacing.lg),
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    Text(
                        text = activeVehicle.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (activeVehicle.plateNumber != null) {
                        Text(
                            text = activeVehicle.plateNumber,
                            style = MaterialTheme.typography.bodySmall.mono,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Box {
                    val selectCd = stringResource(R.string.home_cd_select_vehicle)
                    OutlinedButton(
                        onClick = { pickerExpanded = true },
                        modifier = Modifier
                            .widthIn(min = Spacing.xxl)
                            .heightIn(min = Spacing.xxl)
                            .semantics { contentDescription = selectCd },
                        shape = MaterialTheme.shapes.small,
                        border = BorderStroke(
                            width = Spacing.strokeThin,
                            color = MaterialTheme.colorScheme.outline,
                        ),
                        contentPadding = PaddingValues(horizontal = Spacing.md),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_expand_more),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(Spacing.lg),
                        )
                    }
                    DropdownMenu(
                        expanded = pickerExpanded,
                        onDismissRequest = { pickerExpanded = false },
                        containerColor = MaterialTheme.colorScheme.surface,
                    ) {
                        vehicles.forEach { vehicle ->
                            DropdownMenuItem(
                                modifier = Modifier.heightIn(min = Spacing.xxl),
                                text = {
                                    Column {
                                        Text(
                                            text = vehicle.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                        if (vehicle.plateNumber != null) {
                                            Text(
                                                text = vehicle.plateNumber,
                                                style = MaterialTheme.typography.labelLarge.mono,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }
                                },
                                leadingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .size(Spacing.md)
                                            .clip(CircleShape)
                                            .background(
                                                if (vehicle.id == activeVehicle.id) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.outlineVariant
                                                },
                                            ),
                                    )
                                },
                                onClick = {
                                    pickerExpanded = false
                                    if (vehicle.id != activeVehicle.id) {
                                        onSelectVehicle(vehicle.id)
                                    }
                                },
                            )
                        }
                    }
                }
            }

            val odometerCd = stringResource(
                R.string.home_cd_odometer_value,
                activeVehicle.formattedOdometerFull,
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                itemVerticalAlignment = Alignment.CenterVertically,
            ) {
                FlowRow(
                    modifier = Modifier.semantics(mergeDescendants = true) {
                        contentDescription = odometerCd
                    },
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                    itemVerticalAlignment = Alignment.Bottom,
                ) {
                    Text(
                        text = activeVehicle.formattedOdometerValue,
                        style = MaterialTheme.typography.odometerHero,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(R.string.home_unit_km),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = Spacing.xs),
                    )
                }

                Button(
                    onClick = onUpdateOdometerClick,
                    modifier = Modifier.heightIn(min = Spacing.xxl),
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        text = stringResource(R.string.home_action_update_odometer),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeading(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier.semantics { heading() },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UpcomingMaintenanceRow(
    item: UpcomingMaintenanceUi,
    modifier: Modifier = Modifier,
) {
    val statusSpec = item.status.toVisualSpec()
    val detailText = formatMaintenanceDetail(item.remainingKm, item.remainingDays)
    val rowAccessibilityText = if (detailText != null) {
        stringResource(
            R.string.home_cd_maintenance_row,
            item.partName,
            statusSpec.label,
            detailText,
        )
    } else {
        stringResource(
            R.string.home_cd_maintenance_row_no_detail,
            item.partName,
            statusSpec.label,
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Spacing.xxl)
            .semantics(mergeDescendants = true) {
                contentDescription = rowAccessibilityText
            }
            .padding(vertical = Spacing.md),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Icon(
            painter = painterResource(item.category.toIconRes()),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(top = Spacing.xs)
                .size(Spacing.lg),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                itemVerticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.partName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                MaintenanceStatusChip(spec = statusSpec)
            }
            if (detailText != null) {
                Text(
                    text = detailText,
                    style = MaterialTheme.typography.bodySmall.tabular,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun MaintenanceStatusChip(
    spec: StatusVisualSpec,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(
            width = Spacing.strokeThin,
            color = spec.color,
        ),
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = Spacing.sm,
                vertical = Spacing.xs,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Icon(
                painter = painterResource(spec.iconRes),
                contentDescription = null,
                tint = spec.color,
                modifier = Modifier.size(Spacing.base),
            )
            Text(
                text = spec.label,
                style = MaterialTheme.typography.labelLarge,
                color = spec.color,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ServiceTimelineRow(
    item: ServiceTimelineItemUi,
    isLast: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Column(
            modifier = Modifier
                .width(Spacing.lg)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(Spacing.xs))
            Box(
                modifier = Modifier
                    .size(Spacing.sm)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .padding(top = Spacing.xs)
                        .width(Spacing.strokeThin)
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.outlineVariant),
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) Spacing.sm else Spacing.base),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                itemVerticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.formattedDate,
                    style = MaterialTheme.typography.labelLarge.tabular,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = item.formattedTotalCost,
                    style = MaterialTheme.typography.titleMedium.mono,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(
                text = item.itemsSummary.ifEmpty { stringResource(R.string.home_history_no_items) },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            val metadata = if (item.workshop != null) {
                stringResource(
                    R.string.home_history_odometer_workshop,
                    item.formattedOdometer,
                    item.workshop,
                )
            } else {
                item.formattedOdometer
            }
            Text(
                text = metadata,
                style = MaterialTheme.typography.bodySmall.mono,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun formatMaintenanceDetail(
    remainingKm: Int?,
    remainingDays: Long?,
): String? {
    val kmPart = remainingKm?.let { km ->
        if (km < 0) {
            stringResource(
                R.string.home_maintenance_overdue_km,
                ServisKuFormatter.formatNumber(-km),
            )
        } else {
            stringResource(
                R.string.home_maintenance_remaining_km,
                ServisKuFormatter.formatNumber(km),
            )
        }
    }

    val daysPart = remainingDays?.let { days ->
        val overdueCount = (-days).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        val remainingCount = days.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        when {
            days < 0L -> pluralStringResource(
                R.plurals.home_maintenance_overdue_days,
                overdueCount,
                overdueCount,
            )

            days == 0L -> stringResource(R.string.home_maintenance_due_today)
            kmPart != null -> pluralStringResource(
                R.plurals.home_maintenance_remaining_days_pair,
                remainingCount,
                remainingCount,
            )

            else -> pluralStringResource(
                R.plurals.home_maintenance_remaining_days_alone,
                remainingCount,
                remainingCount,
            )
        }
    }

    return when {
        kmPart != null && daysPart != null -> stringResource(
            R.string.home_maintenance_or_pair,
            kmPart,
            daysPart,
        )

        kmPart != null -> kmPart
        else -> daysPart
    }
}

@DrawableRes
private fun MaintenanceCategory.toIconRes(): Int =
    when (this) {
        MaintenanceCategory.ENGINE_OIL -> R.drawable.ic_part_oil
        MaintenanceCategory.BRAKES -> R.drawable.ic_part_brakes
        MaintenanceCategory.DRIVE_BELT -> R.drawable.ic_part_drive_belt
        MaintenanceCategory.AIR_FILTER -> R.drawable.ic_part_air_filter
        MaintenanceCategory.SPARK_PLUG -> R.drawable.ic_part_spark_plug
        MaintenanceCategory.GENERAL -> R.drawable.ic_part_general
    }

private data class StatusVisualSpec(
    val color: Color,
    val label: String,
    @param:DrawableRes val iconRes: Int,
)

@Composable
private fun MaintenanceStatus.toVisualSpec(): StatusVisualSpec {
    val statusColors = LocalStatusColors.current
    return when (this) {
        MaintenanceStatus.OVERDUE -> StatusVisualSpec(
            color = statusColors.overdue,
            label = stringResource(R.string.home_status_overdue),
            iconRes = R.drawable.ic_status_overdue,
        )

        MaintenanceStatus.SOON -> StatusVisualSpec(
            color = statusColors.soon,
            label = stringResource(R.string.home_status_soon),
            iconRes = R.drawable.ic_status_soon,
        )

        MaintenanceStatus.OK -> StatusVisualSpec(
            color = statusColors.ok,
            label = stringResource(R.string.home_status_ok),
            iconRes = R.drawable.ic_status_ok,
        )
    }
}

@ServisKuPreviews
@Composable
private fun HomeScreenContentPreview() {
    ServisKuTheme(accent = VehicleAccent.BIRU_OLI) {
        HomeScreen(
            state = sampleLoadedState(),
            onSelectVehicle = {},
            onUpdateOdometerClick = {},
            onAddVehicleClick = {},
            onRetryClick = {},
        )
    }
}

@ServisKuPreviews
@Composable
private fun HomeScreenEmptyMaintenancePreview() {
    ServisKuTheme(accent = VehicleAccent.TEAL) {
        HomeScreen(
            state = sampleLoadedState().copy(
                activeVehicle = sampleLoadedState().activeVehicle.copy(accent = VehicleAccent.TEAL),
                upcomingMaintenance = emptyList(),
                recentServices = emptyList(),
            ),
            onSelectVehicle = {},
            onUpdateOdometerClick = {},
            onAddVehicleClick = {},
            onRetryClick = {},
        )
    }
}

@ServisKuPreviews
@Composable
private fun HomeScreenEmptyPreview() {
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

@ServisKuPreviews
@Composable
private fun HomeScreenErrorPreview() {
    ServisKuTheme {
        HomeScreen(
            state = HomeUiState.Error(
                messageRes = R.string.home_error_load_title,
                actionHintRes = R.string.home_error_load_action,
            ),
            onSelectVehicle = {},
            onUpdateOdometerClick = {},
            onAddVehicleClick = {},
            onRetryClick = {},
        )
    }
}

private fun sampleLoadedState(): HomeUiState.Content = HomeUiState.Content(
    vehicles = listOf(
        VehicleSummaryUi(
            id = 1L,
            name = "Vario 160 ABS",
            plateNumber = "B 4123 XYZ",
            accent = VehicleAccent.BIRU_OLI,
        ),
        VehicleSummaryUi(
            id = 2L,
            name = "Supra X 125 FI",
            plateNumber = "AB 5381 KM",
            accent = VehicleAccent.TEAL,
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
            partName = "Oli mesin",
            category = MaintenanceCategory.ENGINE_OIL,
            remainingKm = -140,
            remainingDays = -2L,
            status = MaintenanceStatus.OVERDUE,
        ),
        UpcomingMaintenanceUi(
            id = 2L,
            partName = "Kampas rem depan",
            category = MaintenanceCategory.BRAKES,
            remainingKm = 320,
            remainingDays = 12L,
            status = MaintenanceStatus.SOON,
        ),
        UpcomingMaintenanceUi(
            id = 3L,
            partName = "V-belt dan roller",
            category = MaintenanceCategory.DRIVE_BELT,
            remainingKm = 1_450,
            remainingDays = 34L,
            status = MaintenanceStatus.OK,
        ),
    ),
    recentServices = listOf(
        ServiceTimelineItemUi(
            id = 10L,
            formattedDate = "14 Nov 2026",
            formattedOdometer = "12.450 km",
            formattedTotalCost = "Rp 158.500",
            workshop = "Bengkel Maju Jaya",
            itemsSummary = "Oli mesin MPX2, Filter udara",
        ),
        ServiceTimelineItemUi(
            id = 9L,
            formattedDate = "28 Agu 2026",
            formattedOdometer = "10.310 km",
            formattedTotalCost = "Rp 85.000",
            workshop = "AHASS Condongcatur",
            itemsSummary = "Kampas rem belakang",
        ),
    ),
)
