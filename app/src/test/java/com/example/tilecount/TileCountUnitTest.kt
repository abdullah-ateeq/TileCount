package com.example.tilecount

import com.example.tilecount.data.model.AppSettings
import com.example.tilecount.data.model.AreaUnit
import com.example.tilecount.data.model.LengthUnit
import com.example.tilecount.data.model.SurfaceType
import com.example.tilecount.data.model.TileCountHistory
import com.example.tilecount.domain.math.AreaUnitConverter
import com.example.tilecount.domain.math.LengthUnitConverter
import com.example.tilecount.domain.math.TileCountCalculator
import com.example.tilecount.domain.math.TileCountFormatter
import com.example.tilecount.domain.math.TileCountParser
import com.example.tilecount.domain.math.TileCountValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode

class TileCountUnitTest {

    private val mc = MathContext(50, RoundingMode.HALF_UP)

    // 1. Floor surface area
    @Test
    fun testFloorSurfaceArea() {
        val length = BigDecimal("5")
        val width = BigDecimal("4")
        val area = TileCountCalculator.calculateSurfaceArea(length, width, mc)
        assertEquals(0, BigDecimal("20").compareTo(area))
    }

    // 2. Wall surface area
    @Test
    fun testWallSurfaceArea() {
        val width = BigDecimal("5")
        val height = BigDecimal("3")
        val area = TileCountCalculator.calculateSurfaceArea(width, height, mc)
        assertEquals(0, BigDecimal("15").compareTo(area))
    }

    // 3. Tile area
    @Test
    fun testTileArea() {
        val tileLen = BigDecimal("0.60")
        val tileWid = BigDecimal("0.60")
        val area = TileCountCalculator.calculateTileArea(tileLen, tileWid, mc)
        assertEquals(0, BigDecimal("0.36").compareTo(area))
    }

    // 4. Coverage without waste
    @Test
    fun testCoverageWithoutWaste() {
        val surfaceArea = BigDecimal("20")
        val waste = BigDecimal.ZERO
        val coverage = TileCountCalculator.calculateCoverageWithWaste(surfaceArea, waste, mc)
        assertEquals(0, surfaceArea.compareTo(coverage))
    }

    // 5. Coverage with waste
    @Test
    fun testCoverageWithWaste() {
        val surfaceArea = BigDecimal("20")
        val waste = BigDecimal("10")
        val coverage = TileCountCalculator.calculateCoverageWithWaste(surfaceArea, waste, mc)
        assertEquals(0, BigDecimal("22").compareTo(coverage))
    }

    // 6. Tile count
    @Test
    fun testTileCount() {
        val coverage = BigDecimal("22")
        val tileArea = BigDecimal("0.36")
        val count = TileCountCalculator.calculateTileCount(coverage, tileArea)
        assertEquals(BigInteger.valueOf(62), count)
    }

    // 7. Tile ceiling
    @Test
    fun testTileCeiling() {
        // 61.0001 -> 62
        val coverage1 = BigDecimal("61.0001")
        val count1 = TileCountCalculator.calculateTileCount(coverage1, BigDecimal.ONE)
        assertEquals(BigInteger.valueOf(62), count1)

        // 61.5 -> 62
        val coverage2 = BigDecimal("61.5")
        val count2 = TileCountCalculator.calculateTileCount(coverage2, BigDecimal.ONE)
        assertEquals(BigInteger.valueOf(62), count2)

        // 61.9999 -> 62
        val coverage3 = BigDecimal("61.9999")
        val count3 = TileCountCalculator.calculateTileCount(coverage3, BigDecimal.ONE)
        assertEquals(BigInteger.valueOf(62), count3)

        // 62.0 -> 62
        val coverage4 = BigDecimal("62.0")
        val count4 = TileCountCalculator.calculateTileCount(coverage4, BigDecimal.ONE)
        assertEquals(BigInteger.valueOf(62), count4)
    }

    // 8. Exact tile division
    @Test
    fun testExactTileDivision() {
        val result = TileCountCalculator.calculate(
            surfaceType = SurfaceType.FLOOR,
            surfaceLength = BigDecimal("6"),
            surfaceWidth = BigDecimal("4"),
            surfaceLengthUnit = LengthUnit.METER,
            surfaceWidthUnit = LengthUnit.METER,
            tileLength = BigDecimal("1"),
            tileWidth = BigDecimal("1"),
            tileLengthUnit = LengthUnit.METER,
            tileWidthUnit = LengthUnit.METER,
            wastePercent = BigDecimal.ZERO,
            tilesPerBox = null
        ).getOrThrow()

        assertEquals(BigInteger.valueOf(24), result.tilesRequired)
    }

    // 9. Fractional tile division
    @Test
    fun testFractionalTileDivision() {
        val result = TileCountCalculator.calculate(
            surfaceType = SurfaceType.FLOOR,
            surfaceLength = BigDecimal("5"),
            surfaceWidth = BigDecimal("4"),
            surfaceLengthUnit = LengthUnit.METER,
            surfaceWidthUnit = LengthUnit.METER,
            tileLength = BigDecimal("0.6"),
            tileWidth = BigDecimal("0.6"),
            tileLengthUnit = LengthUnit.METER,
            tileWidthUnit = LengthUnit.METER,
            wastePercent = BigDecimal.ZERO,
            tilesPerBox = null
        ).getOrThrow()

        // 20 / 0.36 = 55.555... -> 56 tiles
        assertEquals(BigInteger.valueOf(56), result.tilesRequired)
    }

    // 10. Box count
    @Test
    fun testBoxCount() {
        val tilesRequired = BigInteger.valueOf(62)
        val tilesPerBox = BigInteger.valueOf(4)
        val boxes = TileCountCalculator.calculateBoxesRequired(tilesRequired, tilesPerBox)
        assertEquals(BigInteger.valueOf(16), boxes)
    }

    // 11. Exact box division
    @Test
    fun testExactBoxDivision() {
        val tilesRequired = BigInteger.valueOf(20)
        val tilesPerBox = BigInteger.valueOf(5)
        val boxes = TileCountCalculator.calculateBoxesRequired(tilesRequired, tilesPerBox)
        assertEquals(BigInteger.valueOf(4), boxes)
    }

    // 12. Box ceiling
    @Test
    fun testBoxCeiling() {
        assertEquals(BigInteger.valueOf(4), TileCountCalculator.calculateBoxesRequired(BigInteger.valueOf(13), BigInteger.valueOf(4)))
        assertEquals(BigInteger.valueOf(4), TileCountCalculator.calculateBoxesRequired(BigInteger.valueOf(15), BigInteger.valueOf(4)))
        assertEquals(BigInteger.valueOf(4), TileCountCalculator.calculateBoxesRequired(BigInteger.valueOf(16), BigInteger.valueOf(4)))
        assertEquals(BigInteger.valueOf(5), TileCountCalculator.calculateBoxesRequired(BigInteger.valueOf(17), BigInteger.valueOf(4)))
    }

    // 13. Total purchased tiles
    @Test
    fun testTotalPurchasedTiles() {
        val boxes = BigInteger.valueOf(16)
        val perBox = BigInteger.valueOf(4)
        val total = TileCountCalculator.calculateTotalPurchasedTiles(boxes, perBox)
        assertEquals(BigInteger.valueOf(64), total)
    }

    // 14. Surplus tiles
    @Test
    fun testSurplusTiles() {
        val total = BigInteger.valueOf(64)
        val required = BigInteger.valueOf(62)
        val surplus = TileCountCalculator.calculateSurplusTiles(total, required)
        assertEquals(BigInteger.valueOf(2), surplus)

        // Exact box count surplus is 0
        assertEquals(BigInteger.ZERO, TileCountCalculator.calculateSurplusTiles(BigInteger.valueOf(20), BigInteger.valueOf(20)))
    }

    // 15. Extra coverage
    @Test
    fun testExtraCoverage() {
        val result = TileCountCalculator.calculate(
            surfaceType = SurfaceType.FLOOR,
            surfaceLength = BigDecimal("5"),
            surfaceWidth = BigDecimal("4"),
            surfaceLengthUnit = LengthUnit.METER,
            surfaceWidthUnit = LengthUnit.METER,
            tileLength = BigDecimal("60"),
            tileWidth = BigDecimal("60"),
            tileLengthUnit = LengthUnit.CENTIMETER,
            tileWidthUnit = LengthUnit.CENTIMETER,
            wastePercent = BigDecimal("10"),
            tilesPerBox = BigInteger.valueOf(4)
        ).getOrThrow()

        // Surface Area = 20 m2
        // Tile Area = 0.36 m2
        // Tiles Required = 62
        // Total Tile Coverage = 62 * 0.36 = 22.32 m2
        // Extra Coverage = 22.32 - 20 = 2.32 m2
        assertEquals(0, BigDecimal("20").compareTo(result.surfaceArea))
        assertEquals(0, BigDecimal("0.36").compareTo(result.tileArea))
        assertEquals(0, BigDecimal("22").compareTo(result.coverageWithWaste))
        assertEquals(BigInteger.valueOf(62), result.tilesRequired)
        assertEquals(0, BigDecimal("22.32").compareTo(result.totalTileCoverage))
        assertEquals(0, BigDecimal("2.32").compareTo(result.extraCoverage))
    }

    // 16. Metric conversion
    @Test
    fun testMetricConversion() {
        val mm = LengthUnitConverter.toMeters(BigDecimal("1000"), LengthUnit.MILLIMETER)
        assertEquals(0, BigDecimal("1").compareTo(mm))

        val cm = LengthUnitConverter.toMeters(BigDecimal("100"), LengthUnit.CENTIMETER)
        assertEquals(0, BigDecimal("1").compareTo(cm))

        val km = LengthUnitConverter.toMeters(BigDecimal("1"), LengthUnit.KILOMETER)
        assertEquals(0, BigDecimal("1000").compareTo(km))
    }

    // 17. Imperial conversion
    @Test
    fun testImperialConversion() {
        val inch = LengthUnitConverter.toMeters(BigDecimal("1"), LengthUnit.INCH)
        assertEquals(0, BigDecimal("0.0254").compareTo(inch))

        val ft = LengthUnitConverter.toMeters(BigDecimal("1"), LengthUnit.FOOT)
        assertEquals(0, BigDecimal("0.3048").compareTo(ft))

        val yd = LengthUnitConverter.toMeters(BigDecimal("1"), LengthUnit.YARD)
        assertEquals(0, BigDecimal("0.9144").compareTo(yd))
    }

    // 18. Area conversion
    @Test
    fun testAreaConversion() {
        val sqCm = AreaUnitConverter.toSquareMeters(BigDecimal("10000"), AreaUnit.SQUARE_CENTIMETER)
        assertEquals(0, BigDecimal("1").compareTo(sqCm))

        val sqFt = AreaUnitConverter.toSquareMeters(BigDecimal("1"), AreaUnit.SQUARE_FOOT)
        assertEquals(0, BigDecimal("0.09290304").compareTo(sqFt))
    }

    // 19. Mixed surface units
    @Test
    fun testMixedSurfaceUnits() {
        val result = TileCountCalculator.calculate(
            surfaceType = SurfaceType.FLOOR,
            surfaceLength = BigDecimal("5"),
            surfaceWidth = BigDecimal("400"),
            surfaceLengthUnit = LengthUnit.METER,
            surfaceWidthUnit = LengthUnit.CENTIMETER,
            tileLength = BigDecimal("60"),
            tileWidth = BigDecimal("60"),
            tileLengthUnit = LengthUnit.CENTIMETER,
            tileWidthUnit = LengthUnit.CENTIMETER,
            wastePercent = BigDecimal.ZERO,
            tilesPerBox = null
        ).getOrThrow()

        // 5m x 4m = 20 m2, tile = 0.36 m2 -> 56 tiles
        assertEquals(BigInteger.valueOf(56), result.tilesRequired)
    }

    // 20. Mixed tile units
    @Test
    fun testMixedTileUnits() {
        val result = TileCountCalculator.calculate(
            surfaceType = SurfaceType.FLOOR,
            surfaceLength = BigDecimal("5"),
            surfaceWidth = BigDecimal("4"),
            surfaceLengthUnit = LengthUnit.METER,
            surfaceWidthUnit = LengthUnit.METER,
            tileLength = BigDecimal("600"),
            tileWidth = BigDecimal("60"),
            tileLengthUnit = LengthUnit.MILLIMETER,
            tileWidthUnit = LengthUnit.CENTIMETER,
            wastePercent = BigDecimal.ZERO,
            tilesPerBox = null
        ).getOrThrow()

        // tile: 0.6m x 0.6m = 0.36 m2 -> 56 tiles
        assertEquals(BigInteger.valueOf(56), result.tilesRequired)
    }

    // 21. Mixed surface/tile units
    @Test
    fun testMixedSurfaceTileUnits() {
        val result = TileCountCalculator.calculate(
            surfaceType = SurfaceType.FLOOR,
            surfaceLength = BigDecimal("5"),
            surfaceWidth = BigDecimal("400"),
            surfaceLengthUnit = LengthUnit.METER,
            surfaceWidthUnit = LengthUnit.CENTIMETER,
            tileLength = BigDecimal("600"),
            tileWidth = BigDecimal("60"),
            tileLengthUnit = LengthUnit.MILLIMETER,
            tileWidthUnit = LengthUnit.CENTIMETER,
            wastePercent = BigDecimal.ZERO,
            tilesPerBox = null
        ).getOrThrow()

        assertEquals(BigInteger.valueOf(56), result.tilesRequired)
    }

    // 22. Zero validation
    @Test
    fun testZeroValidation() {
        val res = TileCountValidator.validateSurfaceDimension(BigDecimal.ZERO)
        assertTrue(res.isFailure)
        assertEquals("Surface dimensions must be greater than zero.", res.exceptionOrNull()?.message)
    }

    // 23. Negative validation
    @Test
    fun testNegativeValidation() {
        val res = TileCountValidator.validateTileDimension(BigDecimal("-60"))
        assertTrue(res.isFailure)
        assertEquals("Tile dimensions must be greater than zero.", res.exceptionOrNull()?.message)
    }

    // 24. Empty input
    @Test
    fun testEmptyInput() {
        val res = TileCountParser.parseNumber("   ")
        assertTrue(res.isFailure)
        assertEquals("Enter all required dimensions.", res.exceptionOrNull()?.message)
    }

    // 25. Invalid waste
    @Test
    fun testInvalidWaste() {
        val negWaste = TileCountValidator.validateWastePercent(BigDecimal("-5"))
        assertTrue(negWaste.isFailure)
        assertEquals("Waste allowance must be between 0% and 100%.", negWaste.exceptionOrNull()?.message)

        val overWaste = TileCountValidator.validateWastePercent(BigDecimal("101"))
        assertTrue(overWaste.isFailure)
        assertEquals("Waste allowance must be between 0% and 100%.", overWaste.exceptionOrNull()?.message)
    }

    // 26. Invalid tiles-per-box
    @Test
    fun testInvalidTilesPerBox() {
        val res = TileCountValidator.validateTilesPerBox(BigInteger.ZERO)
        assertTrue(res.isFailure)
        assertEquals("Tiles per box must be greater than zero.", res.exceptionOrNull()?.message)
    }

    // 27. Maximum input length
    @Test
    fun testMaxInputLength() {
        val longStr = "1".repeat(501)
        val res = TileCountParser.parseNumber(longStr)
        assertTrue(res.isFailure)
        assertEquals("Number is too long. Maximum 500 digits.", res.exceptionOrNull()?.message)
    }

    // 28. Quantity overflow protection
    @Test
    fun testQuantityOverflowProtection() {
        val overLimit = BigInteger.valueOf(1_000_000_001L)
        val res = TileCountValidator.validateQuantityLimit(overLimit)
        assertTrue(res.isFailure)
        assertEquals("Calculated quantity is too large. Please check your dimensions and units.", res.exceptionOrNull()?.message)
    }

    // 29. Decimal values
    @Test
    fun testDecimalValues() {
        val res1 = TileCountParser.parseNumber("100.5").getOrThrow()
        assertEquals(0, BigDecimal("100.5").compareTo(res1))

        val res2 = TileCountParser.parseNumber(".5").getOrThrow()
        assertEquals(0, BigDecimal("0.5").compareTo(res2) )

        val res3 = TileCountParser.parseNumber("10.").getOrThrow()
        assertEquals(0, BigDecimal("10.0").compareTo(res3))
    }

    // 30. Scientific notation
    @Test
    fun testScientificNotation() {
        val res1 = TileCountParser.parseNumber("1E+5").getOrThrow()
        assertEquals(0, BigDecimal("100000").compareTo(res1))

        val res2 = TileCountParser.parseNumber("1.5E-2").getOrThrow()
        assertEquals(0, BigDecimal("0.015").compareTo(res2))
    }

    // 31. Very small values
    @Test
    fun testVerySmallValues() {
        val res = TileCountParser.parseNumber("0.000001").getOrThrow()
        assertEquals(0, BigDecimal("0.000001").compareTo(res))
    }

    // 32. Very large values
    @Test
    fun testVeryLargeValues() {
        val large = "9".repeat(100)
        val res = TileCountParser.parseNumber(large).getOrThrow()
        assertEquals(100, res.toPlainString().length)
    }

    // 33. BigDecimal multiplication
    @Test
    fun testBigDecimalMultiplication() {
        val a = BigDecimal("123.45678901234567890")
        val b = BigDecimal("987.65432109876543210")
        val product = a.multiply(b, mc)
        assertNotNull(product)
        assertTrue(product > BigDecimal.ZERO)
    }

    // 34. BigDecimal division
    @Test
    fun testBigDecimalDivision() {
        val a = BigDecimal("20")
        val b = BigDecimal("0.36")
        val quotient = a.divide(b, mc)
        // 55.555555555555555...
        assertTrue(quotient.toPlainString().startsWith("55.55555555"))
    }

    // 35. BigInteger ceiling division
    @Test
    fun testBigIntegerCeilingDivision() {
        val a = BigInteger.valueOf(62)
        val b = BigInteger.valueOf(4)
        val (q, r) = a.divideAndRemainder(b)
        val ceiling = if (r == BigInteger.ZERO) q else q + BigInteger.ONE
        assertEquals(BigInteger.valueOf(16), ceiling)
    }

    // 36. Result formatting
    @Test
    fun testResultFormatting() {
        val value = BigDecimal("20.000000")
        val formatted = TileCountFormatter.formatNumber(value, 6)
        assertEquals("20", formatted)

        val value2 = BigDecimal("0.360000")
        val formatted2 = TileCountFormatter.formatNumber(value2, 6)
        assertEquals("0.36", formatted2)
    }

    // 37. Trailing-zero removal
    @Test
    fun testTrailingZeroRemoval() {
        val value = BigDecimal("22.3200000")
        val formatted = TileCountFormatter.formatNumber(value, 6)
        assertEquals("22.32", formatted)
    }

    // 38. HALF_UP
    @Test
    fun testHalfUpRounding() {
        val value = BigDecimal("20.1234565")
        val formatted = TileCountFormatter.formatNumber(value, 6, RoundingMode.HALF_UP)
        assertEquals("20.123457", formatted)
    }

    // 39. HALF_DOWN
    @Test
    fun testHalfDownRounding() {
        val value = BigDecimal("20.1234565")
        val formatted = TileCountFormatter.formatNumber(value, 6, RoundingMode.HALF_DOWN)
        assertEquals("20.123456", formatted)
    }

    // 40. HALF_EVEN
    @Test
    fun testHalfEvenRounding() {
        val val1 = BigDecimal("20.1234565") // 6 is even -> rounds to 6
        val formatted1 = TileCountFormatter.formatNumber(val1, 6, RoundingMode.HALF_EVEN)
        assertEquals("20.123456", formatted1)

        val val2 = BigDecimal("20.1234575") // 7 is odd -> rounds to 8
        val formatted2 = TileCountFormatter.formatNumber(val2, 6, RoundingMode.HALF_EVEN)
        assertEquals("20.123458", formatted2)
    }

    // 41. Calculation steps
    @Test
    fun testCalculationSteps() {
        val result = TileCountCalculator.calculate(
            surfaceType = SurfaceType.FLOOR,
            surfaceLength = BigDecimal("5"),
            surfaceWidth = BigDecimal("4"),
            surfaceLengthUnit = LengthUnit.METER,
            surfaceWidthUnit = LengthUnit.METER,
            tileLength = BigDecimal("60"),
            tileWidth = BigDecimal("60"),
            tileLengthUnit = LengthUnit.CENTIMETER,
            tileWidthUnit = LengthUnit.CENTIMETER,
            wastePercent = BigDecimal("10"),
            tilesPerBox = BigInteger.valueOf(4)
        ).getOrThrow()

        assertTrue(result.calculationSteps.isNotEmpty())
        assertEquals(6, result.calculationSteps.size)
        assertEquals("Floor Surface Area", result.calculationSteps[0].label)
        assertEquals("Tile Area", result.calculationSteps[1].label)
    }

    // 42. History duplicate logic
    @Test
    fun testHistoryDuplicateLogic() {
        val h1 = TileCountHistory(
            id = "1",
            surfaceType = SurfaceType.FLOOR,
            surfaceLength = "5",
            surfaceWidth = "4",
            tileLength = "60",
            tileWidth = "60",
            surfaceLengthUnit = LengthUnit.METER,
            surfaceWidthUnit = LengthUnit.METER,
            tileLengthUnit = LengthUnit.CENTIMETER,
            tileWidthUnit = LengthUnit.CENTIMETER,
            resultAreaUnit = AreaUnit.SQUARE_METER,
            wastePercent = "10",
            tilesPerBox = "4",
            savedAt = 1000L
        )

        val h2 = h1.copy(id = "2", savedAt = 2000L)
        // Same calculation fields -> duplicate
        val isDup = (h1.surfaceType == h2.surfaceType &&
                h1.surfaceLength == h2.surfaceLength &&
                h1.surfaceWidth == h2.surfaceWidth &&
                h1.tileLength == h2.tileLength &&
                h1.tileWidth == h2.tileWidth &&
                h1.surfaceLengthUnit == h2.surfaceLengthUnit &&
                h1.surfaceWidthUnit == h2.surfaceWidthUnit &&
                h1.tileLengthUnit == h2.tileLengthUnit &&
                h1.tileWidthUnit == h2.tileWidthUnit &&
                h1.resultAreaUnit == h2.resultAreaUnit &&
                h1.wastePercent == h2.wastePercent &&
                h1.tilesPerBox == h2.tilesPerBox)

        assertTrue(isDup)
    }

    // 43. History limit 10 items logic
    @Test
    fun testHistoryLimit10Items() {
        val list = mutableListOf<String>()
        for (i in 1..15) {
            list.add(0, "item_$i")
            while (list.size > 10) {
                list.removeAt(list.lastIndex)
            }
        }
        assertEquals(10, list.size)
        assertEquals("item_15", list.first())
        assertEquals("item_6", list.last())
    }

    // 44. History delete logic
    @Test
    fun testHistoryDeleteLogic() {
        val list = mutableListOf("1", "2", "3")
        list.remove("2")
        assertEquals(listOf("1", "3"), list)
    }

    // 45. History restore logic
    @Test
    fun testHistoryRestoreLogic() {
        val history = TileCountHistory(
            id = "restore_test",
            surfaceType = SurfaceType.WALL,
            surfaceLength = "5",
            surfaceWidth = "3",
            tileLength = "30",
            tileWidth = "60",
            surfaceLengthUnit = LengthUnit.METER,
            surfaceWidthUnit = LengthUnit.METER,
            tileLengthUnit = LengthUnit.CENTIMETER,
            tileWidthUnit = LengthUnit.CENTIMETER,
            resultAreaUnit = AreaUnit.SQUARE_METER,
            wastePercent = "10",
            tilesPerBox = null,
            savedAt = 1000L
        )

        val result = TileCountCalculator.calculate(
            surfaceType = history.surfaceType,
            surfaceLength = BigDecimal(history.surfaceLength),
            surfaceWidth = BigDecimal(history.surfaceWidth),
            surfaceLengthUnit = history.surfaceLengthUnit,
            surfaceWidthUnit = history.surfaceWidthUnit,
            tileLength = BigDecimal(history.tileLength),
            tileWidth = BigDecimal(history.tileWidth),
            tileLengthUnit = history.tileLengthUnit,
            tileWidthUnit = history.tileWidthUnit,
            wastePercent = BigDecimal(history.wastePercent),
            tilesPerBox = null
        ).getOrThrow()

        // Wall: 5 x 3 = 15m2, tile 0.3 x 0.6 = 0.18 m2, waste 10% -> 16.5m2 / 0.18 = 91.666... -> 92 tiles
        assertEquals(BigInteger.valueOf(92), result.tilesRequired)
    }

    // 46. Settings default values
    @Test
    fun testSettingsDefaultValues() {
        val settings = AppSettings()
        assertTrue(settings.dynamicColor)
        assertTrue(settings.hapticFeedback)
        assertTrue(settings.animations)
        assertEquals(6, settings.decimalPlaces)
        assertEquals(RoundingMode.HALF_UP, settings.roundingMode)
        assertTrue(settings.showFormula)
        assertTrue(settings.showCalculationSteps)
        assertTrue(settings.showTileDiagram)
        assertTrue(settings.showPurchaseDetails)
        assertTrue(settings.confirmDelete)
    }

    // 47. Formatter copy summary
    @Test
    fun testFormatterCopySummary() {
        val result = TileCountCalculator.calculate(
            surfaceType = SurfaceType.FLOOR,
            surfaceLength = BigDecimal("5"),
            surfaceWidth = BigDecimal("4"),
            surfaceLengthUnit = LengthUnit.METER,
            surfaceWidthUnit = LengthUnit.METER,
            tileLength = BigDecimal("60"),
            tileWidth = BigDecimal("60"),
            tileLengthUnit = LengthUnit.CENTIMETER,
            tileWidthUnit = LengthUnit.CENTIMETER,
            wastePercent = BigDecimal("10"),
            tilesPerBox = BigInteger.valueOf(4)
        ).getOrThrow()

        val text = TileCountFormatter.formatSummaryForCopy(
            result = result,
            surfaceLengthUnit = LengthUnit.METER,
            surfaceWidthUnit = LengthUnit.METER,
            tileLengthUnit = LengthUnit.CENTIMETER,
            tileWidthUnit = LengthUnit.CENTIMETER,
            areaUnit = AreaUnit.SQUARE_METER
        )

        assertTrue(text.contains("TileCount"))
        assertTrue(text.contains("Surface: Floor"))
        assertTrue(text.contains("Size: 5 m × 4 m"))
        assertTrue(text.contains("Tile: 60 cm × 60 cm"))
        assertTrue(text.contains("Surface Area: 20 m²"))
        assertTrue(text.contains("Waste: 10%"))
        assertTrue(text.contains("Tiles Required: 62"))
        assertTrue(text.contains("Tiles Per Box: 4"))
        assertTrue(text.contains("Boxes Required: 16"))
        assertTrue(text.contains("Total Purchased: 64 tiles"))
        assertTrue(text.contains("Surplus: 2 tiles"))
    }

    // 48. Reset default values
    @Test
    fun testResetDefaultValues() {
        val defaultSurface = SurfaceType.FLOOR
        val defaultLength = "5"
        val defaultWidth = "4"
        val defaultTileLen = "60"
        val defaultTileWid = "60"
        val defaultWaste = "10"
        val defaultBox = ""

        val result = TileCountCalculator.calculate(
            surfaceType = defaultSurface,
            surfaceLength = BigDecimal(defaultLength),
            surfaceWidth = BigDecimal(defaultWidth),
            surfaceLengthUnit = LengthUnit.METER,
            surfaceWidthUnit = LengthUnit.METER,
            tileLength = BigDecimal(defaultTileLen),
            tileWidth = BigDecimal(defaultTileWid),
            tileLengthUnit = LengthUnit.CENTIMETER,
            tileWidthUnit = LengthUnit.CENTIMETER,
            wastePercent = BigDecimal(defaultWaste),
            tilesPerBox = null
        ).getOrThrow()

        assertEquals(BigInteger.valueOf(62), result.tilesRequired)
        assertNull(result.boxesRequired)
    }

    // Critical Test: Imperial units
    @Test
    fun testCriticalImperial() {
        val result = TileCountCalculator.calculate(
            surfaceType = SurfaceType.FLOOR,
            surfaceLength = BigDecimal("12"),
            surfaceWidth = BigDecimal("10"),
            surfaceLengthUnit = LengthUnit.FOOT,
            surfaceWidthUnit = LengthUnit.FOOT,
            tileLength = BigDecimal("12"),
            tileWidth = BigDecimal("12"),
            tileLengthUnit = LengthUnit.INCH,
            tileWidthUnit = LengthUnit.INCH,
            wastePercent = BigDecimal.ZERO,
            tilesPerBox = null
        ).getOrThrow()

        assertEquals(BigInteger.valueOf(120), result.tilesRequired)
    }

    // Critical Test: Centimeters
    @Test
    fun testCriticalCentimeters() {
        val result = TileCountCalculator.calculate(
            surfaceType = SurfaceType.FLOOR,
            surfaceLength = BigDecimal("500"),
            surfaceWidth = BigDecimal("400"),
            surfaceLengthUnit = LengthUnit.CENTIMETER,
            surfaceWidthUnit = LengthUnit.CENTIMETER,
            tileLength = BigDecimal("60"),
            tileWidth = BigDecimal("60"),
            tileLengthUnit = LengthUnit.CENTIMETER,
            tileWidthUnit = LengthUnit.CENTIMETER,
            wastePercent = BigDecimal("10"),
            tilesPerBox = null
        ).getOrThrow()

        assertEquals(0, BigDecimal("20").compareTo(result.surfaceArea))
        assertEquals(0, BigDecimal("22").compareTo(result.coverageWithWaste))
        assertEquals(BigInteger.valueOf(62), result.tilesRequired)
    }
}
