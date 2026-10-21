package com.example.tilecount.data.model

import java.math.BigDecimal
import java.math.BigInteger

data class TileCountResult(
    val surfaceType: SurfaceType,
    val surfaceLength: BigDecimal,
    val surfaceWidth: BigDecimal,
    val tileLength: BigDecimal,
    val tileWidth: BigDecimal,
    val surfaceArea: BigDecimal,
    val tileArea: BigDecimal,
    val wastePercent: BigDecimal,
    val coverageWithWaste: BigDecimal,
    val basicTileCount: BigDecimal,
    val tilesRequired: BigInteger,
    val totalTileCoverage: BigDecimal,
    val extraCoverage: BigDecimal,
    val tilesPerBox: BigInteger?,
    val boxesRequired: BigInteger?,
    val totalPurchasedTiles: BigInteger?,
    val surplusTiles: BigInteger?,
    val calculationSteps: List<CalculationStep>,
    val calculatedAt: Long
)
