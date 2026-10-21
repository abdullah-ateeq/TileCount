package com.example.tilecount.domain.math

import com.example.tilecount.data.model.AreaUnit
import java.math.BigDecimal
import java.math.MathContext

object AreaUnitConverter {
    fun toSquareMeters(
        value: BigDecimal,
        unit: AreaUnit,
        mathContext: MathContext = MathContext(50)
    ): BigDecimal {
        if (unit == AreaUnit.SQUARE_METER) return value
        return value.multiply(unit.factorToSquareMeter, mathContext)
    }

    fun fromSquareMeters(
        valueInSquareMeters: BigDecimal,
        targetUnit: AreaUnit,
        mathContext: MathContext = MathContext(50)
    ): BigDecimal {
        if (targetUnit == AreaUnit.SQUARE_METER) return valueInSquareMeters
        return valueInSquareMeters.divide(targetUnit.factorToSquareMeter, mathContext)
    }
}
