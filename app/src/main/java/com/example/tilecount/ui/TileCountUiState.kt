package com.example.tilecount.ui

import com.example.tilecount.data.model.AppSettings
import com.example.tilecount.data.model.AreaUnit
import com.example.tilecount.data.model.LengthUnit
import com.example.tilecount.data.model.SurfaceType
import com.example.tilecount.data.model.TileCountHistory
import com.example.tilecount.data.model.TileCountResult

data class TileCountUiState(
    val surfaceType: SurfaceType = SurfaceType.FLOOR,

    val surfaceLengthInput: String = "5",
    val surfaceWidthInput: String = "4",

    val tileLengthInput: String = "60",
    val tileWidthInput: String = "60",

    val surfaceLengthUnit: LengthUnit = LengthUnit.METER,
    val surfaceWidthUnit: LengthUnit = LengthUnit.METER,

    val tileLengthUnit: LengthUnit = LengthUnit.CENTIMETER,
    val tileWidthUnit: LengthUnit = LengthUnit.CENTIMETER,

    val resultAreaUnit: AreaUnit = AreaUnit.SQUARE_METER,

    val wastePercentInput: String = "10",
    val tilesPerBoxInput: String = "",

    val result: TileCountResult? = null,

    val history: List<TileCountHistory> = emptyList(),

    val errorMessage: String? = null,

    val isCalculating: Boolean = false,

    val settings: AppSettings = AppSettings(),

    val snackbarMessage: String? = null
)
