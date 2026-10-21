package com.example.tilecount.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tilecount.data.model.AppSettings
import com.example.tilecount.data.model.AppTheme
import com.example.tilecount.ui.TileCountUiState
import com.example.tilecount.ui.TileCountViewModel
import com.example.tilecount.util.HapticFeedbackHelper
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: TileCountViewModel,
    uiState: TileCountUiState
) {
    val context = LocalContext.current
    val settings = uiState.settings

    var showThemeDialog by remember { mutableStateOf(false) }
    var showDecimalDialog by remember { mutableStateOf(false) }
    var showRoundingDialog by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showResetSettingsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // APPEARANCE
            SettingsSectionHeader(title = "APPEARANCE")
            SettingsCard {
                SettingsClickableRow(
                    title = "Theme",
                    subtitle = settings.theme.displayName,
                    onClick = {
                        HapticFeedbackHelper.performLightHaptic(context, settings.hapticFeedback)
                        showThemeDialog = true
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsSwitchRow(
                    title = "Dynamic Color",
                    subtitle = "Use colors from your wallpaper (Android 12+)",
                    checked = settings.dynamicColor,
                    onCheckedChange = {
                        HapticFeedbackHelper.performLightHaptic(context, settings.hapticFeedback)
                        viewModel.onUpdateSettings(settings.copy(dynamicColor = it))
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BEHAVIOR
            SettingsSectionHeader(title = "BEHAVIOR")
            SettingsCard {
                SettingsSwitchRow(
                    title = "Haptic Feedback",
                    subtitle = "Subtle vibration on interactions",
                    checked = settings.hapticFeedback,
                    onCheckedChange = {
                        viewModel.onUpdateSettings(settings.copy(hapticFeedback = it))
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsSwitchRow(
                    title = "Animations",
                    subtitle = "Smooth UI transitions and tile diagram animation",
                    checked = settings.animations,
                    onCheckedChange = {
                        HapticFeedbackHelper.performLightHaptic(context, settings.hapticFeedback)
                        viewModel.onUpdateSettings(settings.copy(animations = it))
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // NUMBER DISPLAY
            SettingsSectionHeader(title = "NUMBER DISPLAY")
            SettingsCard {
                SettingsClickableRow(
                    title = "Decimal Places",
                    subtitle = "${settings.decimalPlaces} places (default: 6)",
                    onClick = {
                        HapticFeedbackHelper.performLightHaptic(context, settings.hapticFeedback)
                        showDecimalDialog = true
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsClickableRow(
                    title = "Rounding Mode",
                    subtitle = "${formatRoundingModeName(settings.roundingMode)} (affects area & measurement display only)",
                    onClick = {
                        HapticFeedbackHelper.performLightHaptic(context, settings.hapticFeedback)
                        showRoundingDialog = true
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // DISPLAY
            SettingsSectionHeader(title = "DISPLAY")
            SettingsCard {
                SettingsSwitchRow(
                    title = "Show Tile Diagram",
                    subtitle = "Visual representation of surface and tile layout",
                    checked = settings.showTileDiagram,
                    onCheckedChange = {
                        HapticFeedbackHelper.performLightHaptic(context, settings.hapticFeedback)
                        viewModel.onUpdateSettings(settings.copy(showTileDiagram = it))
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsSwitchRow(
                    title = "Show Purchase Details",
                    subtitle = "Show box quantities and surplus tiles",
                    checked = settings.showPurchaseDetails,
                    onCheckedChange = {
                        HapticFeedbackHelper.performLightHaptic(context, settings.hapticFeedback)
                        viewModel.onUpdateSettings(settings.copy(showPurchaseDetails = it))
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsSwitchRow(
                    title = "Show Calculation Steps",
                    subtitle = "Step-by-step breakdown card",
                    checked = settings.showCalculationSteps,
                    onCheckedChange = {
                        HapticFeedbackHelper.performLightHaptic(context, settings.hapticFeedback)
                        viewModel.onUpdateSettings(settings.copy(showCalculationSteps = it))
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsSwitchRow(
                    title = "Show Tile Formulas",
                    subtitle = "Display math formula explanations",
                    checked = settings.showFormula,
                    onCheckedChange = {
                        HapticFeedbackHelper.performLightHaptic(context, settings.hapticFeedback)
                        viewModel.onUpdateSettings(settings.copy(showFormula = it))
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // HISTORY
            SettingsSectionHeader(title = "HISTORY")
            SettingsCard {
                SettingsSwitchRow(
                    title = "Confirm Delete",
                    subtitle = "Ask before deleting saved calculations",
                    checked = settings.confirmDelete,
                    onCheckedChange = {
                        HapticFeedbackHelper.performLightHaptic(context, settings.hapticFeedback)
                        viewModel.onUpdateSettings(settings.copy(confirmDelete = it))
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                SettingsClickableRow(
                    title = "Clear History",
                    subtitle = "Remove all saved calculations",
                    onClick = {
                        HapticFeedbackHelper.performLightHaptic(context, settings.hapticFeedback)
                        showClearHistoryDialog = true
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // RESET SETTINGS BUTTON
            OutlinedButton(
                onClick = {
                    HapticFeedbackHelper.performLightHaptic(context, settings.hapticFeedback)
                    showResetSettingsDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Reset All Settings")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Theme Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Choose Theme") },
            text = {
                Column {
                    AppTheme.entries.forEach { theme ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.onUpdateSettings(settings.copy(theme = theme))
                                    showThemeDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.theme == theme,
                                onClick = {
                                    viewModel.onUpdateSettings(settings.copy(theme = theme))
                                    showThemeDialog = false
                                }
                            )
                            Text(
                                text = theme.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Decimal Places Dialog (0, 1, 2, 3, 4, 6, 8, 10, 12)
    if (showDecimalDialog) {
        val options = listOf(0, 1, 2, 3, 4, 6, 8, 10, 12)
        AlertDialog(
            onDismissRequest = { showDecimalDialog = false },
            title = { Text("Decimal Places") },
            text = {
                Column {
                    options.forEach { count ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.onUpdateSettings(settings.copy(decimalPlaces = count))
                                    showDecimalDialog = false
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.decimalPlaces == count,
                                onClick = {
                                    viewModel.onUpdateSettings(settings.copy(decimalPlaces = count))
                                    showDecimalDialog = false
                                }
                            )
                            Text(
                                text = "$count decimal places",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDecimalDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Rounding Mode Dialog
    if (showRoundingDialog) {
        val roundingModes = listOf(
            RoundingMode.HALF_UP,
            RoundingMode.HALF_DOWN,
            RoundingMode.HALF_EVEN,
            RoundingMode.UP,
            RoundingMode.DOWN,
            RoundingMode.CEILING,
            RoundingMode.FLOOR
        )
        AlertDialog(
            onDismissRequest = { showRoundingDialog = false },
            title = { Text("Display Rounding Mode") },
            text = {
                Column {
                    Text(
                        text = "Applies to area and measurement display. Tile and box quantities always use ceiling.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    roundingModes.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.onUpdateSettings(settings.copy(roundingMode = mode))
                                    showRoundingDialog = false
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.roundingMode == mode,
                                onClick = {
                                    viewModel.onUpdateSettings(settings.copy(roundingMode = mode))
                                    showRoundingDialog = false
                                }
                            )
                            Text(
                                text = formatRoundingModeName(mode),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRoundingDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Clear History Dialog
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear History") },
            text = { Text("Clear all saved tile calculations?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onClearHistory()
                        showClearHistoryDialog = false
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reset Settings Dialog
    if (showResetSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showResetSettingsDialog = false },
            title = { Text("Reset Settings") },
            text = { Text("Restore all settings to their default values?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onResetSettings()
                        showResetSettingsDialog = false
                    }
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetSettingsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            content()
        }
    }
}

@Composable
private fun SettingsClickableRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

private fun formatRoundingModeName(mode: RoundingMode): String {
    return when (mode) {
        RoundingMode.HALF_UP -> "Half Up"
        RoundingMode.HALF_DOWN -> "Half Down"
        RoundingMode.HALF_EVEN -> "Half Even"
        RoundingMode.UP -> "Up"
        RoundingMode.DOWN -> "Down"
        RoundingMode.CEILING -> "Ceiling"
        RoundingMode.FLOOR -> "Floor"
        else -> mode.name
    }
}
