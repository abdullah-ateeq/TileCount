package com.example.tilecount.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tilecount.data.model.AreaUnit
import com.example.tilecount.data.model.LengthUnit
import com.example.tilecount.data.model.SurfaceType
import com.example.tilecount.domain.math.TileCountFormatter
import com.example.tilecount.ui.TileCountUiState
import com.example.tilecount.ui.TileCountViewModel
import com.example.tilecount.ui.components.AreaUnitSelectionSheet
import com.example.tilecount.ui.components.CalculationStepsCard
import com.example.tilecount.ui.components.FormulaCard
import com.example.tilecount.ui.components.LengthUnitSelectionSheet
import com.example.tilecount.ui.components.TileDiagram
import com.example.tilecount.util.HapticFeedbackHelper
import com.example.tilecount.util.ShareUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: TileCountViewModel,
    uiState: TileCountUiState,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showSurfaceLengthUnitSheet by remember { mutableStateOf(false) }
    var showSurfaceWidthUnitSheet by remember { mutableStateOf(false) }
    var showTileLengthUnitSheet by remember { mutableStateOf(false) }
    var showTileWidthUnitSheet by remember { mutableStateOf(false) }
    var showAreaUnitSheet by remember { mutableStateOf(false) }

    var isBoxSectionExpanded by remember { mutableStateOf(uiState.tilesPerBoxInput.isNotEmpty()) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbarMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .animateContentSize(
                    animationSpec = if (uiState.settings.animations) tween(250) else tween(0)
                )
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TileCount",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Calculate tiles required for a floor or wall.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    IconButton(
                        onClick = {
                            HapticFeedbackHelper.performLightHaptic(context, uiState.settings.hapticFeedback)
                            viewModel.onReset()
                        },
                        modifier = Modifier.semantics { contentDescription = "Reset values" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset values",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = {
                            HapticFeedbackHelper.performLightHaptic(context, uiState.settings.hapticFeedback)
                            viewModel.onSaveCalculation()
                        },
                        modifier = Modifier.semantics { contentDescription = "Save tile calculation" }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.BookmarkBorder,
                            contentDescription = "Save tile calculation",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Surface Selector Section
            Text(
                text = "SURFACE TYPE",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SurfaceCard(
                    title = "FLOOR",
                    description = "Calculate tiles for a floor",
                    isSelected = uiState.surfaceType == SurfaceType.FLOOR,
                    accessibilityLabel = "Calculate tiles for a floor.",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        HapticFeedbackHelper.performLightHaptic(context, uiState.settings.hapticFeedback)
                        viewModel.onSurfaceTypeChange(SurfaceType.FLOOR)
                    }
                )

                SurfaceCard(
                    title = "WALL",
                    description = "Calculate tiles for a wall",
                    isSelected = uiState.surfaceType == SurfaceType.WALL,
                    accessibilityLabel = "Calculate tiles for a wall.",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        HapticFeedbackHelper.performLightHaptic(context, uiState.settings.hapticFeedback)
                        viewModel.onSurfaceTypeChange(SurfaceType.WALL)
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Surface Dimensions Section
            Text(
                text = "SURFACE DIMENSIONS",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            val surfLenLabel = if (uiState.surfaceType == SurfaceType.FLOOR) "Floor Length" else "Wall Width"
            val surfWidLabel = if (uiState.surfaceType == SurfaceType.FLOOR) "Floor Width" else "Wall Height"

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DimensionInputField(
                    label = surfLenLabel,
                    value = uiState.surfaceLengthInput,
                    unitSymbol = uiState.surfaceLengthUnit.symbol,
                    accessibilityLabel = "Surface length value.",
                    modifier = Modifier.weight(1f),
                    onValueChange = { viewModel.onSurfaceLengthChange(it) },
                    onUnitClick = {
                        HapticFeedbackHelper.performLightHaptic(context, uiState.settings.hapticFeedback)
                        showSurfaceLengthUnitSheet = true
                    }
                )

                DimensionInputField(
                    label = surfWidLabel,
                    value = uiState.surfaceWidthInput,
                    unitSymbol = uiState.surfaceWidthUnit.symbol,
                    accessibilityLabel = "Surface width value.",
                    modifier = Modifier.weight(1f),
                    onValueChange = { viewModel.onSurfaceWidthChange(it) },
                    onUnitClick = {
                        HapticFeedbackHelper.performLightHaptic(context, uiState.settings.hapticFeedback)
                        showSurfaceWidthUnitSheet = true
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tile Dimensions Section
            Text(
                text = "TILE DIMENSIONS",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DimensionInputField(
                    label = "Tile Length",
                    value = uiState.tileLengthInput,
                    unitSymbol = uiState.tileLengthUnit.symbol,
                    accessibilityLabel = "Tile length value.",
                    modifier = Modifier.weight(1f),
                    onValueChange = { viewModel.onTileLengthChange(it) },
                    onUnitClick = {
                        HapticFeedbackHelper.performLightHaptic(context, uiState.settings.hapticFeedback)
                        showTileLengthUnitSheet = true
                    }
                )

                DimensionInputField(
                    label = "Tile Width",
                    value = uiState.tileWidthInput,
                    unitSymbol = uiState.tileWidthUnit.symbol,
                    accessibilityLabel = "Tile width value.",
                    modifier = Modifier.weight(1f),
                    onValueChange = { viewModel.onTileWidthChange(it) },
                    onUnitClick = {
                        HapticFeedbackHelper.performLightHaptic(context, uiState.settings.hapticFeedback)
                        showTileWidthUnitSheet = true
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Waste Allowance Section
            Text(
                text = "WASTE ALLOWANCE",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            val presets = listOf("0", "5", "10", "15")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { preset ->
                    val isSelected = uiState.wastePercentInput.trim() == preset
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            HapticFeedbackHelper.performLightHaptic(context, uiState.settings.hapticFeedback)
                            viewModel.onWastePercentChange(preset)
                        },
                        label = { Text("$preset%") },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }

                // Custom Waste Input
                OutlinedTextField(
                    value = uiState.wastePercentInput,
                    onValueChange = { viewModel.onWastePercentChange(it) },
                    label = { Text("Waste %") },
                    modifier = Modifier
                        .width(110.dp)
                        .height(56.dp)
                        .semantics { contentDescription = "Waste allowance percentage." },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Box Details (Optional Expandable Section)
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isBoxSectionExpanded = !isBoxSectionExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "BOX DETAILS (OPTIONAL)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (uiState.tilesPerBoxInput.isNotBlank()) {
                                    "${uiState.tilesPerBoxInput} tiles per box"
                                } else {
                                    "Enter tiles per box to calculate boxes required."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = if (isBoxSectionExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isBoxSectionExpanded) "Collapse box details" else "Expand box details",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    AnimatedVisibility(visible = isBoxSectionExpanded) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            OutlinedTextField(
                                value = uiState.tilesPerBoxInput,
                                onValueChange = { viewModel.onTilesPerBoxChange(it) },
                                label = { Text("Tiles per Box") },
                                placeholder = { Text("e.g. 4") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .semantics { contentDescription = "Tiles per box" },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action: CALCULATE Button
            Button(
                onClick = {
                    HapticFeedbackHelper.performLightHaptic(context, uiState.settings.hapticFeedback)
                    viewModel.onCalculateClicked()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "CALCULATE",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }

            // Error banner if any
            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // RESULT SECTION
            val result = uiState.result
            if (result != null) {
                // Primary Result Card: TILES REQUIRED
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Estimated number of tiles required." },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TILES REQUIRED",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            IconButton(
                                onClick = {
                                    HapticFeedbackHelper.performLightHaptic(context, uiState.settings.hapticFeedback)
                                    ShareUtils.copyToClipboard(
                                        context,
                                        "Tiles Required",
                                        result.tilesRequired.toString()
                                    )
                                    viewModel.clearSnackbarMessage()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy tiles required",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = TileCountFormatter.formatWholeNumber(result.tilesRequired),
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 48.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            text = "Estimated Tile Quantity",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        val wasteFormatted = TileCountFormatter.formatNumber(result.wastePercent, 2)
                        Text(
                            text = "Includes $wasteFormatted% waste allowance",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Secondary Result Cards: Surface Area, Tile Area, Total Coverage, Extra Coverage
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ResultMetricCard(
                        title = "SURFACE AREA",
                        value = TileCountFormatter.formatArea(
                            result.surfaceArea,
                            uiState.resultAreaUnit,
                            uiState.settings.decimalPlaces,
                            uiState.settings.roundingMode
                        ),
                        modifier = Modifier.weight(1f),
                        onCopy = {
                            HapticFeedbackHelper.performLightHaptic(context, uiState.settings.hapticFeedback)
                            val areaStr = TileCountFormatter.formatArea(
                                result.surfaceArea,
                                uiState.resultAreaUnit,
                                uiState.settings.decimalPlaces,
                                uiState.settings.roundingMode
                            )
                            ShareUtils.copyToClipboard(context, "Surface Area", areaStr)
                        }
                    )

                    ResultMetricCard(
                        title = "TILE AREA",
                        value = TileCountFormatter.formatArea(
                            result.tileArea,
                            uiState.resultAreaUnit,
                            uiState.settings.decimalPlaces,
                            uiState.settings.roundingMode
                        ),
                        modifier = Modifier.weight(1f),
                        onCopy = {
                            HapticFeedbackHelper.performLightHaptic(context, uiState.settings.hapticFeedback)
                            val tileAreaStr = TileCountFormatter.formatArea(
                                result.tileArea,
                                uiState.resultAreaUnit,
                                uiState.settings.decimalPlaces,
                                uiState.settings.roundingMode
                            )
                            ShareUtils.copyToClipboard(context, "Tile Area", tileAreaStr)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ResultMetricCard(
                        title = "TOTAL COVERAGE",
                        value = TileCountFormatter.formatArea(
                            result.totalTileCoverage,
                            uiState.resultAreaUnit,
                            uiState.settings.decimalPlaces,
                            uiState.settings.roundingMode
                        ),
                        subtitle = "Nominal tiles area",
                        modifier = Modifier.weight(1f)
                    )

                    ResultMetricCard(
                        title = "EXTRA COVERAGE",
                        value = TileCountFormatter.formatArea(
                            result.extraCoverage,
                            uiState.resultAreaUnit,
                            uiState.settings.decimalPlaces,
                            uiState.settings.roundingMode
                        ),
                        subtitle = "Surplus area",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Result Area Unit Selection Button
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = {
                            HapticFeedbackHelper.performLightHaptic(context, uiState.settings.hapticFeedback)
                            showAreaUnitSheet = true
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SquareFoot,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Area Unit: ${uiState.resultAreaUnit.symbol}")
                    }
                }

                // Box Results (if tiles per box provided)
                if (uiState.settings.showPurchaseDetails) {
                    Spacer(modifier = Modifier.height(16.dp))
                    if (result.tilesPerBox != null && result.boxesRequired != null) {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "BOX & PURCHASE DETAILS",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    IconButton(
                                        onClick = {
                                            HapticFeedbackHelper.performLightHaptic(context, uiState.settings.hapticFeedback)
                                            ShareUtils.copyToClipboard(
                                                context,
                                                "Boxes Required",
                                                "${result.boxesRequired} boxes"
                                            )
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy boxes required",
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "BOXES REQUIRED",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                        )
                                        Text(
                                            text = "${result.boxesRequired} boxes",
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = "TOTAL PURCHASED",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                        )
                                        Text(
                                            text = "${result.totalPurchasedTiles} tiles",
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = "SURPLUS TILES",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                        )
                                        Text(
                                            text = "${result.surplusTiles} tiles",
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Note when box details not provided
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Enter tiles per box to calculate boxes required.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Tile Diagram Component (if enabled)
                if (uiState.settings.showTileDiagram) {
                    Spacer(modifier = Modifier.height(20.dp))
                    TileDiagram(
                        surfaceType = uiState.surfaceType,
                        surfaceLength = result.surfaceLength,
                        surfaceWidth = result.surfaceWidth,
                        surfaceLengthUnit = uiState.surfaceLengthUnit,
                        surfaceWidthUnit = uiState.surfaceWidthUnit,
                        tileLength = result.tileLength,
                        tileWidth = result.tileWidth,
                        tileLengthUnit = uiState.tileLengthUnit,
                        tileWidthUnit = uiState.tileWidthUnit,
                        animationsEnabled = uiState.settings.animations
                    )
                }

                // Disclaimers & Notes
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "• Actual usage may vary because of cuts, layout, breakage, and installation conditions.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Openings are not deducted.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Copy & Share Action Buttons
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            HapticFeedbackHelper.performLightHaptic(context, uiState.settings.hapticFeedback)
                            viewModel.getShareText()?.let { text ->
                                ShareUtils.copyToClipboard(context, "TileCount Calculation", text)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .semantics { contentDescription = "Copy tile calculation." },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("COPY")
                    }

                    Button(
                        onClick = {
                            HapticFeedbackHelper.performLightHaptic(context, uiState.settings.hapticFeedback)
                            viewModel.getShareText()?.let { text ->
                                ShareUtils.shareText(context, text)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .semantics { contentDescription = "Share tile calculation." },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SHARE")
                    }
                }

                // Calculation Steps Card (if enabled)
                if (uiState.settings.showCalculationSteps && result.calculationSteps.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    CalculationStepsCard(steps = result.calculationSteps)
                }

                // Formula Card (if enabled)
                if (uiState.settings.showFormula) {
                    Spacer(modifier = Modifier.height(16.dp))
                    FormulaCard()
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Unit Selection Sheets
        if (showSurfaceLengthUnitSheet) {
            LengthUnitSelectionSheet(
                title = if (uiState.surfaceType == SurfaceType.FLOOR) "Floor Length Unit" else "Wall Width Unit",
                selectedUnit = uiState.surfaceLengthUnit,
                onUnitSelected = { viewModel.onSurfaceLengthUnitChange(it) },
                onDismissRequest = { showSurfaceLengthUnitSheet = false }
            )
        }

        if (showSurfaceWidthUnitSheet) {
            LengthUnitSelectionSheet(
                title = if (uiState.surfaceType == SurfaceType.FLOOR) "Floor Width Unit" else "Wall Height Unit",
                selectedUnit = uiState.surfaceWidthUnit,
                onUnitSelected = { viewModel.onSurfaceWidthUnitChange(it) },
                onDismissRequest = { showSurfaceWidthUnitSheet = false }
            )
        }

        if (showTileLengthUnitSheet) {
            LengthUnitSelectionSheet(
                title = "Tile Length Unit",
                selectedUnit = uiState.tileLengthUnit,
                onUnitSelected = { viewModel.onTileLengthUnitChange(it) },
                onDismissRequest = { showTileLengthUnitSheet = false }
            )
        }

        if (showTileWidthUnitSheet) {
            LengthUnitSelectionSheet(
                title = "Tile Width Unit",
                selectedUnit = uiState.tileWidthUnit,
                onUnitSelected = { viewModel.onTileWidthUnitChange(it) },
                onDismissRequest = { showTileWidthUnitSheet = false }
            )
        }

        if (showAreaUnitSheet) {
            AreaUnitSelectionSheet(
                title = "Result Area Unit",
                selectedUnit = uiState.resultAreaUnit,
                onUnitSelected = { viewModel.onResultAreaUnitChange(it) },
                onDismissRequest = { showAreaUnitSheet = false }
            )
        }
    }
}

@Composable
private fun SurfaceCard(
    title: String,
    description: String,
    isSelected: Boolean,
    accessibilityLabel: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .semantics { contentDescription = accessibilityLabel },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Composable
private fun DimensionInputField(
    label: String,
    value: String,
    unitSymbol: String,
    accessibilityLabel: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    onUnitClick: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        trailingIcon = {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                modifier = Modifier
                    .padding(end = 6.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onUnitClick() }
            ) {
                Text(
                    text = unitSymbol,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        modifier = modifier.semantics { contentDescription = accessibilityLabel },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}

@Composable
private fun ResultMetricCard(
    title: String,
    value: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    onCopy: (() -> Unit)? = null
) {
    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                if (onCopy != null) {
                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy $title",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
