package com.example.tilecount.data.model

import java.math.BigDecimal

enum class LengthUnit(
    val symbol: String,
    val displayName: String,
    val factorToMeter: BigDecimal
) {
    MILLIMETER("mm", "Millimeter", BigDecimal("0.001")),
    CENTIMETER("cm", "Centimeter", BigDecimal("0.01")),
    METER("m", "Meter", BigDecimal("1")),
    KILOMETER("km", "Kilometer", BigDecimal("1000")),
    INCH("in", "Inch", BigDecimal("0.0254")),
    FOOT("ft", "Foot", BigDecimal("0.3048")),
    YARD("yd", "Yard", BigDecimal("0.9144"));

    companion object {
        fun fromSymbol(symbol: String): LengthUnit =
            entries.firstOrNull { it.symbol.equals(symbol, ignoreCase = true) } ?: METER
    }
}
