package com.example.tilecount.data.model

data class TileCountHistory(
    val id: String,
    val surfaceType: SurfaceType,
    val surfaceLength: String,
    val surfaceWidth: String,
    val tileLength: String,
    val tileWidth: String,
    val surfaceLengthUnit: LengthUnit,
    val surfaceWidthUnit: LengthUnit,
    val tileLengthUnit: LengthUnit,
    val tileWidthUnit: LengthUnit,
    val resultAreaUnit: AreaUnit,
    val wastePercent: String,
    val tilesPerBox: String?,
    val savedAt: Long
)
