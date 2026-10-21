package com.example.tilecount.domain.math

import com.example.tilecount.data.model.LengthUnit
import java.math.BigDecimal
import java.math.MathContext

object LengthUnitConverter {
    fun toMeters(
        value: BigDecimal,
        unit: LengthUnit,
        mathContext: MathContext = MathContext(50)
    ): BigDecimal {
        if (unit == LengthUnit.METER) return value
        return value.multiply(unit.factorToMeter, mathContext)
    }

    fun fromMeters(
        valueInMeters: BigDecimal,
        targetUnit: LengthUnit,
        mathContext: MathContext = MathContext(50)
    ): BigDecimal {
        if (targetUnit == LengthUnit.METER) return valueInMeters
        return valueInMeters.divide(targetUnit.factorToMeter, mathContext)
    }
}
