package com.example.tilecount.domain.math

import com.example.tilecount.data.model.AreaUnit
import com.example.tilecount.data.model.LengthUnit
import com.example.tilecount.data.model.SurfaceType
import com.example.tilecount.data.model.TileCountResult
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object TileCountFormatter {

    fun formatNumber(
        value: BigDecimal,
        decimalPlaces: Int = 6,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val rounded = if (decimalPlaces >= 0) {
            value.setScale(decimalPlaces, roundingMode)
        } else {
            value
        }
        val stripped = rounded.stripTrailingZeros()
        // If stripped scale is negative (e.g. 100 -> scale -2), setScale to 0
        val normalized = if (stripped.scale() < 0) stripped.setScale(0) else stripped
        return normalized.toPlainString()
    }

    fun formatWholeNumber(value: BigInteger): String {
        val symbols = DecimalFormatSymbols(Locale.US)
        val df = DecimalFormat("#,##0", symbols)
        return df.format(value)
    }

    fun formatArea(
        areaInSquareMeters: BigDecimal,
        targetUnit: AreaUnit,
        decimalPlaces: Int = 6,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val inTargetUnit = AreaUnitConverter.fromSquareMeters(areaInSquareMeters, targetUnit)
        val formatted = formatNumber(inTargetUnit, decimalPlaces, roundingMode)
        return "$formatted ${targetUnit.symbol}"
    }

    fun formatLength(
        value: BigDecimal,
        unit: LengthUnit,
        decimalPlaces: Int = 6,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val formatted = formatNumber(value, decimalPlaces, roundingMode)
        return "$formatted ${unit.symbol}"
    }

    fun formatSummaryForCopy(
        result: TileCountResult,
        surfaceLengthUnit: LengthUnit,
        surfaceWidthUnit: LengthUnit,
        tileLengthUnit: LengthUnit,
        tileWidthUnit: LengthUnit,
        areaUnit: AreaUnit,
        decimalPlaces: Int = 6,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val sb = StringBuilder()
        sb.append("TileCount\n\n")

        val surfName = if (result.surfaceType == SurfaceType.FLOOR) "Floor" else "Wall"
        sb.append("Surface: $surfName\n")

        val surfLenStr = formatLength(result.surfaceLength, surfaceLengthUnit, decimalPlaces, roundingMode)
        val surfWidStr = formatLength(result.surfaceWidth, surfaceWidthUnit, decimalPlaces, roundingMode)
        sb.append("Size: $surfLenStr × $surfWidStr\n\n")

        val tileLenStr = formatLength(result.tileLength, tileLengthUnit, decimalPlaces, roundingMode)
        val tileWidStr = formatLength(result.tileWidth, tileWidthUnit, decimalPlaces, roundingMode)
        sb.append("Tile: $tileLenStr × $tileWidStr\n\n")

        val surfAreaStr = formatArea(result.surfaceArea, areaUnit, decimalPlaces, roundingMode)
        sb.append("Surface Area: $surfAreaStr\n")

        val wasteStr = formatNumber(result.wastePercent, 2, roundingMode)
        sb.append("Waste: $wasteStr%\n")
        sb.append("Tiles Required: ${formatWholeNumber(result.tilesRequired)}\n")

        if (result.tilesPerBox != null && result.boxesRequired != null) {
            sb.append("\nTiles Per Box: ${result.tilesPerBox}\n")
            sb.append("Boxes Required: ${result.boxesRequired}\n")
            if (result.totalPurchasedTiles != null) {
                sb.append("Total Purchased: ${result.totalPurchasedTiles} tiles\n")
            }
            if (result.surplusTiles != null) {
                sb.append("Surplus: ${result.surplusTiles} tiles\n")
            }
        }

        return sb.toString()
    }
}
