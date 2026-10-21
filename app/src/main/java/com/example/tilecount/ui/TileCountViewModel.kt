package com.example.tilecount.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tilecount.data.model.AppSettings
import com.example.tilecount.data.model.AreaUnit
import com.example.tilecount.data.model.LengthUnit
import com.example.tilecount.data.model.SurfaceType
import com.example.tilecount.data.model.TileCountHistory
import com.example.tilecount.data.repository.HistoryRepository
import com.example.tilecount.data.repository.SettingsRepository
import com.example.tilecount.domain.math.TileCountCalculator
import com.example.tilecount.domain.math.TileCountFormatter
import com.example.tilecount.domain.math.TileCountParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class TileCountViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application)
    private val historyRepository = HistoryRepository(application)

    private val _uiState = MutableStateFlow(
        TileCountUiState(
            surfaceType = settingsRepository.getLastSurfaceType(),
            surfaceLengthInput = settingsRepository.getLastSurfaceLength(),
            surfaceWidthInput = settingsRepository.getLastSurfaceWidth(),
            surfaceLengthUnit = settingsRepository.getLastSurfaceLengthUnit(),
            surfaceWidthUnit = settingsRepository.getLastSurfaceWidthUnit(),
            tileLengthInput = settingsRepository.getLastTileLength(),
            tileWidthInput = settingsRepository.getLastTileWidth(),
            tileLengthUnit = settingsRepository.getLastTileLengthUnit(),
            tileWidthUnit = settingsRepository.getLastTileWidthUnit(),
            resultAreaUnit = settingsRepository.getLastResultAreaUnit(),
            wastePercentInput = settingsRepository.getLastWastePercent(),
            tilesPerBoxInput = settingsRepository.getLastTilesPerBox(),
            settings = settingsRepository.getSettings(),
            history = historyRepository.getHistory()
        )
    )
    val uiState: StateFlow<TileCountUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                _uiState.update { it.copy(settings = settings) }
            }
        }
        viewModelScope.launch {
            historyRepository.historyFlow.collect { history ->
                _uiState.update { it.copy(history = history) }
            }
        }
        // Run initial calculation
        calculate(showErrors = false)
    }

    fun onSurfaceTypeChange(type: SurfaceType) {
        _uiState.update { it.copy(surfaceType = type, errorMessage = null) }
        calculate(showErrors = false)
        persistCurrentInputs()
    }

    fun onSurfaceLengthChange(input: String) {
        _uiState.update { it.copy(surfaceLengthInput = input, errorMessage = null) }
        persistCurrentInputs()
    }

    fun onSurfaceWidthChange(input: String) {
        _uiState.update { it.copy(surfaceWidthInput = input, errorMessage = null) }
        persistCurrentInputs()
    }

    fun onTileLengthChange(input: String) {
        _uiState.update { it.copy(tileLengthInput = input, errorMessage = null) }
        persistCurrentInputs()
    }

    fun onTileWidthChange(input: String) {
        _uiState.update { it.copy(tileWidthInput = input, errorMessage = null) }
        persistCurrentInputs()
    }

    fun onWastePercentChange(input: String) {
        _uiState.update { it.copy(wastePercentInput = input, errorMessage = null) }
        persistCurrentInputs()
    }

    fun onTilesPerBoxChange(input: String) {
        _uiState.update { it.copy(tilesPerBoxInput = input, errorMessage = null) }
        persistCurrentInputs()
    }

    fun onSurfaceLengthUnitChange(unit: LengthUnit) {
        _uiState.update { it.copy(surfaceLengthUnit = unit) }
        calculate(showErrors = false)
        persistCurrentInputs()
    }

    fun onSurfaceWidthUnitChange(unit: LengthUnit) {
        _uiState.update { it.copy(surfaceWidthUnit = unit) }
        calculate(showErrors = false)
        persistCurrentInputs()
    }

    fun onTileLengthUnitChange(unit: LengthUnit) {
        _uiState.update { it.copy(tileLengthUnit = unit) }
        calculate(showErrors = false)
        persistCurrentInputs()
    }

    fun onTileWidthUnitChange(unit: LengthUnit) {
        _uiState.update { it.copy(tileWidthUnit = unit) }
        calculate(showErrors = false)
        persistCurrentInputs()
    }

    fun onResultAreaUnitChange(unit: AreaUnit) {
        _uiState.update { it.copy(resultAreaUnit = unit) }
        persistCurrentInputs()
    }

    fun onCalculateClicked() {
        calculate(showErrors = true)
    }

    fun calculate(showErrors: Boolean = true) {
        val state = _uiState.value
        _uiState.update { it.copy(isCalculating = true) }

        viewModelScope.launch(Dispatchers.Default) {
            val surfLenRes = TileCountParser.parseNumber(state.surfaceLengthInput)
            val surfWidRes = TileCountParser.parseNumber(state.surfaceWidthInput)
            val tileLenRes = TileCountParser.parseNumber(state.tileLengthInput)
            val tileWidRes = TileCountParser.parseNumber(state.tileWidthInput)
            val wasteRes = TileCountParser.parseNumber(state.wastePercentInput)

            val boxRes = if (state.tilesPerBoxInput.isBlank()) {
                Result.success(null)
            } else {
                TileCountParser.parsePositiveInteger(state.tilesPerBoxInput)
            }

            val parseError = when {
                surfLenRes.isFailure -> surfLenRes.exceptionOrNull()?.message
                surfWidRes.isFailure -> surfWidRes.exceptionOrNull()?.message
                tileLenRes.isFailure -> tileLenRes.exceptionOrNull()?.message
                tileWidRes.isFailure -> tileWidRes.exceptionOrNull()?.message
                wasteRes.isFailure -> wasteRes.exceptionOrNull()?.message
                boxRes.isFailure -> boxRes.exceptionOrNull()?.message
                else -> null
            }

            if (parseError != null) {
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            isCalculating = false,
                            errorMessage = if (showErrors) parseError else null
                        )
                    }
                }
                return@launch
            }

            val surfLen = surfLenRes.getOrThrow()
            val surfWid = surfWidRes.getOrThrow()
            val tileLen = tileLenRes.getOrThrow()
            val tileWid = tileWidRes.getOrThrow()
            val waste = wasteRes.getOrThrow()
            val box = boxRes.getOrThrow()

            val calcResult = TileCountCalculator.calculate(
                surfaceType = state.surfaceType,
                surfaceLength = surfLen,
                surfaceWidth = surfWid,
                surfaceLengthUnit = state.surfaceLengthUnit,
                surfaceWidthUnit = state.surfaceWidthUnit,
                tileLength = tileLen,
                tileWidth = tileWid,
                tileLengthUnit = state.tileLengthUnit,
                tileWidthUnit = state.tileWidthUnit,
                wastePercent = waste,
                tilesPerBox = box,
                resultAreaUnit = state.resultAreaUnit
            )

            withContext(Dispatchers.Main) {
                if (calcResult.isSuccess) {
                    _uiState.update {
                        it.copy(
                            result = calcResult.getOrThrow(),
                            errorMessage = null,
                            isCalculating = false
                        )
                    }
                } else {
                    val msg = calcResult.exceptionOrNull()?.message ?: "Calculation error"
                    _uiState.update {
                        it.copy(
                            errorMessage = if (showErrors) msg else null,
                            isCalculating = false
                        )
                    }
                }
            }
        }
    }

    fun onSaveCalculation(): Boolean {
        val state = _uiState.value
        val historyItem = TileCountHistory(
            id = UUID.randomUUID().toString(),
            surfaceType = state.surfaceType,
            surfaceLength = state.surfaceLengthInput.trim(),
            surfaceWidth = state.surfaceWidthInput.trim(),
            tileLength = state.tileLengthInput.trim(),
            tileWidth = state.tileWidthInput.trim(),
            surfaceLengthUnit = state.surfaceLengthUnit,
            surfaceWidthUnit = state.surfaceWidthUnit,
            tileLengthUnit = state.tileLengthUnit,
            tileWidthUnit = state.tileWidthUnit,
            resultAreaUnit = state.resultAreaUnit,
            wastePercent = state.wastePercentInput.trim(),
            tilesPerBox = state.tilesPerBoxInput.trim().ifEmpty { null },
            savedAt = System.currentTimeMillis()
        )
        val saved = historyRepository.saveCalculation(historyItem)
        if (saved) {
            _uiState.update { it.copy(snackbarMessage = "Calculation saved to history") }
        } else {
            _uiState.update { it.copy(snackbarMessage = "Calculation already saved") }
        }
        return saved
    }

    fun onRestoreHistory(history: TileCountHistory) {
        _uiState.update {
            it.copy(
                surfaceType = history.surfaceType,
                surfaceLengthInput = history.surfaceLength,
                surfaceWidthInput = history.surfaceWidth,
                tileLengthInput = history.tileLength,
                tileWidthInput = history.tileWidth,
                surfaceLengthUnit = history.surfaceLengthUnit,
                surfaceWidthUnit = history.surfaceWidthUnit,
                tileLengthUnit = history.tileLengthUnit,
                tileWidthUnit = history.tileWidthUnit,
                resultAreaUnit = history.resultAreaUnit,
                wastePercentInput = history.wastePercent,
                tilesPerBoxInput = history.tilesPerBox ?: "",
                errorMessage = null
            )
        }
        calculate(showErrors = false)
        persistCurrentInputs()
        _uiState.update { it.copy(snackbarMessage = "Calculation restored") }
    }

    fun onDeleteHistoryItem(id: String) {
        historyRepository.deleteItem(id)
    }

    fun onClearHistory() {
        historyRepository.clearHistory()
        _uiState.update { it.copy(snackbarMessage = "History cleared") }
    }

    fun onReset() {
        _uiState.update {
            it.copy(
                surfaceType = SurfaceType.FLOOR,
                surfaceLengthInput = "5",
                surfaceWidthInput = "4",
                tileLengthInput = "60",
                tileWidthInput = "60",
                surfaceLengthUnit = LengthUnit.METER,
                surfaceWidthUnit = LengthUnit.METER,
                tileLengthUnit = LengthUnit.CENTIMETER,
                tileWidthUnit = LengthUnit.CENTIMETER,
                resultAreaUnit = AreaUnit.SQUARE_METER,
                wastePercentInput = "10",
                tilesPerBoxInput = "",
                errorMessage = null,
                snackbarMessage = "Values reset to defaults"
            )
        }
        calculate(showErrors = false)
        persistCurrentInputs()
    }

    fun onUpdateSettings(settings: AppSettings) {
        settingsRepository.updateSettings(settings)
    }

    fun onResetSettings() {
        val default = settingsRepository.resetSettings()
        _uiState.update { it.copy(settings = default, snackbarMessage = "Settings reset to defaults") }
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun getShareText(): String? {
        val state = _uiState.value
        val res = state.result ?: return null
        return TileCountFormatter.formatSummaryForCopy(
            result = res,
            surfaceLengthUnit = state.surfaceLengthUnit,
            surfaceWidthUnit = state.surfaceWidthUnit,
            tileLengthUnit = state.tileLengthUnit,
            tileWidthUnit = state.tileWidthUnit,
            areaUnit = state.resultAreaUnit,
            decimalPlaces = state.settings.decimalPlaces,
            roundingMode = state.settings.roundingMode
        )
    }

    private fun persistCurrentInputs() {
        val state = _uiState.value
        settingsRepository.saveLastInputs(
            surfaceType = state.surfaceType,
            surfaceLength = state.surfaceLengthInput,
            surfaceWidth = state.surfaceWidthInput,
            surfaceLengthUnit = state.surfaceLengthUnit,
            surfaceWidthUnit = state.surfaceWidthUnit,
            tileLength = state.tileLengthInput,
            tileWidth = state.tileWidthInput,
            tileLengthUnit = state.tileLengthUnit,
            tileWidthUnit = state.tileWidthUnit,
            resultAreaUnit = state.resultAreaUnit,
            wastePercent = state.wastePercentInput,
            tilesPerBox = state.tilesPerBoxInput
        )
    }
}
