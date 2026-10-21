package com.example.tilecount.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.tilecount.data.model.AppSettings
import com.example.tilecount.data.model.AppTheme
import com.example.tilecount.data.model.AreaUnit
import com.example.tilecount.data.model.LengthUnit
import com.example.tilecount.data.model.SurfaceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.RoundingMode

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<AppSettings> = _settingsFlow.asStateFlow()

    fun getSettings(): AppSettings = _settingsFlow.value

    fun updateSettings(settings: AppSettings) {
        prefs.edit().apply {
            putString(KEY_THEME, settings.theme.name)
            putBoolean(KEY_DYNAMIC_COLOR, settings.dynamicColor)
            putBoolean(KEY_HAPTICS, settings.hapticFeedback)
            putBoolean(KEY_ANIMATIONS, settings.animations)
            putInt(KEY_DECIMAL_PLACES, settings.decimalPlaces)
            putString(KEY_ROUNDING_MODE, settings.roundingMode.name)
            putBoolean(KEY_SHOW_FORMULA, settings.showFormula)
            putBoolean(KEY_SHOW_CALC_STEPS, settings.showCalculationSteps)
            putBoolean(KEY_SHOW_DIAGRAM, settings.showTileDiagram)
            putBoolean(KEY_SHOW_PURCHASE, settings.showPurchaseDetails)
            putBoolean(KEY_CONFIRM_DELETE, settings.confirmDelete)
            apply()
        }
        _settingsFlow.value = settings
    }

    fun resetSettings(): AppSettings {
        val default = AppSettings()
        updateSettings(default)
        return default
    }

    private fun loadSettings(): AppSettings {
        val themeStr = prefs.getString(KEY_THEME, AppTheme.SYSTEM.name) ?: AppTheme.SYSTEM.name
        val theme = runCatching { AppTheme.valueOf(themeStr) }.getOrDefault(AppTheme.SYSTEM)

        val roundingStr = prefs.getString(KEY_ROUNDING_MODE, RoundingMode.HALF_UP.name) ?: RoundingMode.HALF_UP.name
        val rounding = runCatching { RoundingMode.valueOf(roundingStr) }.getOrDefault(RoundingMode.HALF_UP)

        return AppSettings(
            theme = theme,
            dynamicColor = prefs.getBoolean(KEY_DYNAMIC_COLOR, true),
            hapticFeedback = prefs.getBoolean(KEY_HAPTICS, true),
            animations = prefs.getBoolean(KEY_ANIMATIONS, true),
            decimalPlaces = prefs.getInt(KEY_DECIMAL_PLACES, 6),
            roundingMode = rounding,
            showFormula = prefs.getBoolean(KEY_SHOW_FORMULA, true),
            showCalculationSteps = prefs.getBoolean(KEY_SHOW_CALC_STEPS, true),
            showTileDiagram = prefs.getBoolean(KEY_SHOW_DIAGRAM, true),
            showPurchaseDetails = prefs.getBoolean(KEY_SHOW_PURCHASE, true),
            confirmDelete = prefs.getBoolean(KEY_CONFIRM_DELETE, true)
        )
    }

    // Persist last inputs
    fun saveLastInputs(
        surfaceType: SurfaceType,
        surfaceLength: String,
        surfaceWidth: String,
        surfaceLengthUnit: LengthUnit,
        surfaceWidthUnit: LengthUnit,
        tileLength: String,
        tileWidth: String,
        tileLengthUnit: LengthUnit,
        tileWidthUnit: LengthUnit,
        resultAreaUnit: AreaUnit,
        wastePercent: String,
        tilesPerBox: String
    ) {
        prefs.edit().apply {
            putString(KEY_LAST_SURFACE_TYPE, surfaceType.name)
            putString(KEY_LAST_SURFACE_LEN, surfaceLength)
            putString(KEY_LAST_SURFACE_WID, surfaceWidth)
            putString(KEY_LAST_SURF_LEN_UNIT, surfaceLengthUnit.name)
            putString(KEY_LAST_SURF_WID_UNIT, surfaceWidthUnit.name)
            putString(KEY_LAST_TILE_LEN, tileLength)
            putString(KEY_LAST_TILE_WID, tileWidth)
            putString(KEY_LAST_TILE_LEN_UNIT, tileLengthUnit.name)
            putString(KEY_LAST_TILE_WID_UNIT, tileWidthUnit.name)
            putString(KEY_LAST_RESULT_AREA_UNIT, resultAreaUnit.name)
            putString(KEY_LAST_WASTE_PCT, wastePercent)
            putString(KEY_LAST_TILES_PER_BOX, tilesPerBox)
            apply()
        }
    }

    fun getLastSurfaceType(): SurfaceType {
        val name = prefs.getString(KEY_LAST_SURFACE_TYPE, SurfaceType.FLOOR.name) ?: SurfaceType.FLOOR.name
        return runCatching { SurfaceType.valueOf(name) }.getOrDefault(SurfaceType.FLOOR)
    }

    fun getLastSurfaceLength(): String = prefs.getString(KEY_LAST_SURFACE_LEN, "5") ?: "5"
    fun getLastSurfaceWidth(): String = prefs.getString(KEY_LAST_SURFACE_WID, "4") ?: "4"
    fun getLastSurfaceLengthUnit(): LengthUnit =
        runCatching { LengthUnit.valueOf(prefs.getString(KEY_LAST_SURF_LEN_UNIT, LengthUnit.METER.name) ?: LengthUnit.METER.name) }
            .getOrDefault(LengthUnit.METER)
    fun getLastSurfaceWidthUnit(): LengthUnit =
        runCatching { LengthUnit.valueOf(prefs.getString(KEY_LAST_SURF_WID_UNIT, LengthUnit.METER.name) ?: LengthUnit.METER.name) }
            .getOrDefault(LengthUnit.METER)

    fun getLastTileLength(): String = prefs.getString(KEY_LAST_TILE_LEN, "60") ?: "60"
    fun getLastTileWidth(): String = prefs.getString(KEY_LAST_TILE_WID, "60") ?: "60"
    fun getLastTileLengthUnit(): LengthUnit =
        runCatching { LengthUnit.valueOf(prefs.getString(KEY_LAST_TILE_LEN_UNIT, LengthUnit.CENTIMETER.name) ?: LengthUnit.CENTIMETER.name) }
            .getOrDefault(LengthUnit.CENTIMETER)
    fun getLastTileWidthUnit(): LengthUnit =
        runCatching { LengthUnit.valueOf(prefs.getString(KEY_LAST_TILE_WID_UNIT, LengthUnit.CENTIMETER.name) ?: LengthUnit.CENTIMETER.name) }
            .getOrDefault(LengthUnit.CENTIMETER)

    fun getLastResultAreaUnit(): AreaUnit =
        runCatching { AreaUnit.valueOf(prefs.getString(KEY_LAST_RESULT_AREA_UNIT, AreaUnit.SQUARE_METER.name) ?: AreaUnit.SQUARE_METER.name) }
            .getOrDefault(AreaUnit.SQUARE_METER)

    fun getLastWastePercent(): String = prefs.getString(KEY_LAST_WASTE_PCT, "10") ?: "10"
    fun getLastTilesPerBox(): String = prefs.getString(KEY_LAST_TILES_PER_BOX, "") ?: ""

    companion object {
        private const val PREFS_NAME = "tilecount_preferences"

        private const val KEY_THEME = "theme"
        private const val KEY_DYNAMIC_COLOR = "dynamic_color"
        private const val KEY_HAPTICS = "haptics"
        private const val KEY_ANIMATIONS = "animations"
        private const val KEY_DECIMAL_PLACES = "decimal_places"
        private const val KEY_ROUNDING_MODE = "rounding_mode"
        private const val KEY_SHOW_FORMULA = "show_formula"
        private const val KEY_SHOW_CALC_STEPS = "show_calc_steps"
        private const val KEY_SHOW_DIAGRAM = "show_diagram"
        private const val KEY_SHOW_PURCHASE = "show_purchase"
        private const val KEY_CONFIRM_DELETE = "confirm_delete"

        private const val KEY_LAST_SURFACE_TYPE = "last_surface_type"
        private const val KEY_LAST_SURFACE_LEN = "last_surface_len"
        private const val KEY_LAST_SURFACE_WID = "last_surface_wid"
        private const val KEY_LAST_SURF_LEN_UNIT = "last_surf_len_unit"
        private const val KEY_LAST_SURF_WID_UNIT = "last_surf_wid_unit"
        private const val KEY_LAST_TILE_LEN = "last_tile_len"
        private const val KEY_LAST_TILE_WID = "last_tile_wid"
        private const val KEY_LAST_TILE_LEN_UNIT = "last_tile_len_unit"
        private const val KEY_LAST_TILE_WID_UNIT = "last_tile_wid_unit"
        private const val KEY_LAST_RESULT_AREA_UNIT = "last_result_area_unit"
        private const val KEY_LAST_WASTE_PCT = "last_waste_pct"
        private const val KEY_LAST_TILES_PER_BOX = "last_tiles_per_box"
    }
}
