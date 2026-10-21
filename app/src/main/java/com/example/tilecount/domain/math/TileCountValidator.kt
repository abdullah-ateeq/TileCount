package com.example.tilecount.domain.math

import com.example.tilecount.data.model.TileCountError
import java.math.BigDecimal
import java.math.BigInteger

object TileCountValidator {
    val MAX_QUANTITY_LIMIT: BigInteger = BigInteger.valueOf(1_000_000_000L)
    val MAX_WASTE_PERCENT: BigDecimal = BigDecimal("100")
    val MIN_WASTE_PERCENT: BigDecimal = BigDecimal.ZERO

    fun validateSurfaceDimension(
        value: BigDecimal,
        isLength: Boolean = true,
        isFloor: Boolean = true
    ): Result<BigDecimal> {
        if (value <= BigDecimal.ZERO) {
            val msg = if (isFloor) {
                if (isLength) "Surface dimensions must be greater than zero." else "Surface dimensions must be greater than zero."
            } else {
                "Surface dimensions must be greater than zero."
            }
            return Result.failure(Exception(msg))
        }
        return Result.success(value)
    }

    fun validateTileDimension(value: BigDecimal): Result<BigDecimal> {
        if (value <= BigDecimal.ZERO) {
            return Result.failure(Exception("Tile dimensions must be greater than zero."))
        }
        return Result.success(value)
    }

    fun validateWastePercent(value: BigDecimal): Result<BigDecimal> {
        if (value < MIN_WASTE_PERCENT || value > MAX_WASTE_PERCENT) {
            return Result.failure(Exception(TileCountError.InvalidWastePercent.userMessage))
        }
        return Result.success(value)
    }

    fun validateTilesPerBox(value: BigInteger?): Result<BigInteger?> {
        if (value == null) return Result.success(null)
        if (value <= BigInteger.ZERO) {
            return Result.failure(Exception(TileCountError.NonPositiveTilesPerBox.userMessage))
        }
        return Result.success(value)
    }

    fun validateQuantityLimit(quantity: BigInteger): Result<BigInteger> {
        if (quantity > MAX_QUANTITY_LIMIT) {
            return Result.failure(Exception(TileCountError.QuantityTooLarge.userMessage))
        }
        return Result.success(quantity)
    }
}
