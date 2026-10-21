package com.example.tilecount.domain.math

import com.example.tilecount.data.model.AreaUnit
import com.example.tilecount.data.model.CalculationStep
import com.example.tilecount.data.model.LengthUnit
import com.example.tilecount.data.model.SurfaceType
import com.example.tilecount.data.model.TileCountResult
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode

object TileCountCalculator {
    val DEFAULT_MATH_CONTEXT: MathContext = MathContext(50, RoundingMode.HALF_UP)
    private val HUNDRED = BigDecimal("100")

    fun calculateSurfaceArea(
        length: BigDecimal,
        width: BigDecimal,
        mathContext: MathContext = DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        return length.multiply(width, mathContext)
    }

    fun calculateTileArea(
        tileLength: BigDecimal,
        tileWidth: BigDecimal,
        mathContext: MathContext = DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        return tileLength.multiply(tileWidth, mathContext)
    }

    fun calculateCoverageWithWaste(
        surfaceArea: BigDecimal,
        wastePercent: BigDecimal,
        mathContext: MathContext = DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        val wasteFactor = BigDecimal.ONE.add(wastePercent.divide(HUNDRED, mathContext))
        return surfaceArea.multiply(wasteFactor, mathContext)
    }

    fun calculateTileCount(
        coverageWithWaste: BigDecimal,
        tileArea: BigDecimal
    ): BigInteger {
        require(tileArea > BigDecimal.ZERO) { "Tile area must be positive" }
        return coverageWithWaste.divide(tileArea, 0, RoundingMode.CEILING).toBigIntegerExact()
    }

    fun calculateBoxesRequired(
        tilesRequired: BigInteger,
        tilesPerBox: BigInteger
    ): BigInteger {
        require(tilesPerBox > BigInteger.ZERO) { "Tiles per box must be positive" }
        val (quotient, remainder) = tilesRequired.divideAndRemainder(tilesPerBox)
        return if (remainder == BigInteger.ZERO) quotient else quotient + BigInteger.ONE
    }

    fun calculateTotalPurchasedTiles(
        boxesRequired: BigInteger,
        tilesPerBox: BigInteger
    ): BigInteger {
        return boxesRequired.multiply(tilesPerBox)
    }

    fun calculateSurplusTiles(
        totalPurchasedTiles: BigInteger,
        tilesRequired: BigInteger
    ): BigInteger {
        return totalPurchasedTiles.subtract(tilesRequired)
    }

    fun calculate(
        surfaceType: SurfaceType,
        surfaceLength: BigDecimal,
        surfaceWidth: BigDecimal,
        surfaceLengthUnit: LengthUnit,
        surfaceWidthUnit: LengthUnit,
        tileLength: BigDecimal,
        tileWidth: BigDecimal,
        tileLengthUnit: LengthUnit,
        tileWidthUnit: LengthUnit,
        wastePercent: BigDecimal,
        tilesPerBox: BigInteger?,
        resultAreaUnit: AreaUnit = AreaUnit.SQUARE_METER,
        mathContext: MathContext = DEFAULT_MATH_CONTEXT
    ): Result<TileCountResult> {
        return runCatching {
            TileCountValidator.validateSurfaceDimension(surfaceLength, isLength = true, isFloor = surfaceType == SurfaceType.FLOOR).getOrThrow()
            TileCountValidator.validateSurfaceDimension(surfaceWidth, isLength = false, isFloor = surfaceType == SurfaceType.FLOOR).getOrThrow()
            TileCountValidator.validateTileDimension(tileLength).getOrThrow()
            TileCountValidator.validateTileDimension(tileWidth).getOrThrow()
            TileCountValidator.validateWastePercent(wastePercent).getOrThrow()
            TileCountValidator.validateTilesPerBox(tilesPerBox).getOrThrow()

            val surfLenM = LengthUnitConverter.toMeters(surfaceLength, surfaceLengthUnit, mathContext)
            val surfWidM = LengthUnitConverter.toMeters(surfaceWidth, surfaceWidthUnit, mathContext)
            val tileLenM = LengthUnitConverter.toMeters(tileLength, tileLengthUnit, mathContext)
            val tileWidM = LengthUnitConverter.toMeters(tileWidth, tileWidthUnit, mathContext)

            val surfaceArea = calculateSurfaceArea(surfLenM, surfWidM, mathContext)
            val tileArea = calculateTileArea(tileLenM, tileWidM, mathContext)
            val coverageWithWaste = calculateCoverageWithWaste(surfaceArea, wastePercent, mathContext)

            val basicTileCount = surfaceArea.divide(tileArea, mathContext)
            val tilesRequired = calculateTileCount(coverageWithWaste, tileArea)
            TileCountValidator.validateQuantityLimit(tilesRequired).getOrThrow()

            val totalTileCoverage = BigDecimal(tilesRequired).multiply(tileArea, mathContext)
            val extraCoverage = totalTileCoverage.subtract(surfaceArea, mathContext)

            val boxesRequired = tilesPerBox?.let { calculateBoxesRequired(tilesRequired, it) }
            val totalPurchasedTiles = if (boxesRequired != null) {
                calculateTotalPurchasedTiles(boxesRequired, tilesPerBox)
            } else null
            val surplusTiles = if (totalPurchasedTiles != null) {
                calculateSurplusTiles(totalPurchasedTiles, tilesRequired)
            } else null

            val steps = generateCalculationSteps(
                surfaceType = surfaceType,
                surfLenM = surfLenM,
                surfWidM = surfWidM,
                surfaceArea = surfaceArea,
                tileLenM = tileLenM,
                tileWidM = tileWidM,
                tileArea = tileArea,
                wastePercent = wastePercent,
                coverageWithWaste = coverageWithWaste,
                basicTileCount = basicTileCount,
                tilesRequired = tilesRequired,
                tilesPerBox = tilesPerBox,
                boxesRequired = boxesRequired,
                totalPurchasedTiles = totalPurchasedTiles,
                surplusTiles = surplusTiles,
                mathContext = mathContext
            )

            TileCountResult(
                surfaceType = surfaceType,
                surfaceLength = surfaceLength,
                surfaceWidth = surfaceWidth,
                tileLength = tileLength,
                tileWidth = tileWidth,
                surfaceArea = surfaceArea,
                tileArea = tileArea,
                wastePercent = wastePercent,
                coverageWithWaste = coverageWithWaste,
                basicTileCount = basicTileCount,
                tilesRequired = tilesRequired,
                totalTileCoverage = totalTileCoverage,
                extraCoverage = extraCoverage,
                tilesPerBox = tilesPerBox,
                boxesRequired = boxesRequired,
                totalPurchasedTiles = totalPurchasedTiles,
                surplusTiles = surplusTiles,
                calculationSteps = steps,
                calculatedAt = System.currentTimeMillis()
            )
        }
    }

    private fun generateCalculationSteps(
        surfaceType: SurfaceType,
        surfLenM: BigDecimal,
        surfWidM: BigDecimal,
        surfaceArea: BigDecimal,
        tileLenM: BigDecimal,
        tileWidM: BigDecimal,
        tileArea: BigDecimal,
        wastePercent: BigDecimal,
        coverageWithWaste: BigDecimal,
        basicTileCount: BigDecimal,
        tilesRequired: BigInteger,
        tilesPerBox: BigInteger?,
        boxesRequired: BigInteger?,
        totalPurchasedTiles: BigInteger?,
        surplusTiles: BigInteger?,
        mathContext: MathContext
    ): List<CalculationStep> {
        val steps = mutableListOf<CalculationStep>()

        val surfLabel = if (surfaceType == SurfaceType.FLOOR) "Floor Surface Area" else "Wall Surface Area"
        val surfFormula = if (surfaceType == SurfaceType.FLOOR) "A = Length × Width" else "A = Width × Height"
        steps.add(
            CalculationStep(
                label = surfLabel,
                expression = "$surfFormula\nA = ${TileCountFormatter.formatNumber(surfLenM, 6)} m × ${TileCountFormatter.formatNumber(surfWidM, 6)} m",
                result = "A = ${TileCountFormatter.formatNumber(surfaceArea, 6)} m²"
            )
        )

        steps.add(
            CalculationStep(
                label = "Tile Area",
                expression = "T = Tile Length × Tile Width\nT = ${TileCountFormatter.formatNumber(tileLenM, 6)} m × ${TileCountFormatter.formatNumber(tileWidM, 6)} m",
                result = "T = ${TileCountFormatter.formatNumber(tileArea, 6)} m²"
            )
        )

        val wasteFactorStr = BigDecimal.ONE.add(wastePercent.divide(HUNDRED, mathContext)).stripTrailingZeros().toPlainString()
        steps.add(
            CalculationStep(
                label = "Coverage With Waste (${TileCountFormatter.formatNumber(wastePercent, 2)}%)",
                expression = "C = Surface Area × (1 + Waste% / 100)\nC = ${TileCountFormatter.formatNumber(surfaceArea, 6)} m² × $wasteFactorStr",
                result = "C = ${TileCountFormatter.formatNumber(coverageWithWaste, 6)} m²"
            )
        )

        val rawTileCountStr = TileCountFormatter.formatNumber(coverageWithWaste.divide(tileArea, mathContext), 4)
        steps.add(
            CalculationStep(
                label = "Tiles Required",
                expression = "N = Coverage With Waste / Tile Area\nN = ${TileCountFormatter.formatNumber(coverageWithWaste, 6)} / ${TileCountFormatter.formatNumber(tileArea, 6)} = $rawTileCountStr",
                result = "CEILING(N) = $tilesRequired tiles"
            )
        )

        if (tilesPerBox != null && boxesRequired != null) {
            val rawBoxCount = BigDecimal(tilesRequired).divide(BigDecimal(tilesPerBox), mathContext)
            val rawBoxStr = TileCountFormatter.formatNumber(rawBoxCount, 4)
            steps.add(
                CalculationStep(
                    label = "Boxes Required",
                    expression = "B = Tiles Required / Tiles Per Box\nB = $tilesRequired / $tilesPerBox = $rawBoxStr",
                    result = "CEILING(B) = $boxesRequired boxes"
                )
            )

            if (totalPurchasedTiles != null && surplusTiles != null) {
                steps.add(
                    CalculationStep(
                        label = "Box Purchase & Surplus",
                        expression = "Purchased = $boxesRequired boxes × $tilesPerBox tiles/box = $totalPurchasedTiles tiles\nSurplus = $totalPurchasedTiles - $tilesRequired",
                        result = "$surplusTiles surplus tiles"
                    )
                )
            }
        }

        return steps
    }
}
