package com.example.tilecount.data.model

import java.math.BigDecimal

enum class AreaUnit(
    val symbol: String,
    val displayName: String,
    val factorToSquareMeter: BigDecimal
) {
    SQUARE_MILLIMETER("mm²", "Square Millimeter", BigDecimal("0.000001")),
    SQUARE_CENTIMETER("cm²", "Square Centimeter", BigDecimal("0.0001")),
    SQUARE_METER("m²", "Square Meter", BigDecimal("1")),
    SQUARE_KILOMETER("km²", "Square Kilometer", BigDecimal("1000000")),
    SQUARE_INCH("in²", "Square Inch", BigDecimal("0.00064516")),
    SQUARE_FOOT("ft²", "Square Foot", BigDecimal("0.09290304")),
    SQUARE_YARD("yd²", "Square Yard", BigDecimal("0.83612736"));

    companion object {
        fun fromSymbol(symbol: String): AreaUnit =
            entries.firstOrNull { it.symbol.equals(symbol, ignoreCase = true) } ?: SQUARE_METER
    }
}
