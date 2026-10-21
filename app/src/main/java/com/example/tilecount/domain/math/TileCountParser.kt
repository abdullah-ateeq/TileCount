package com.example.tilecount.domain.math

import com.example.tilecount.data.model.TileCountError
import java.math.BigDecimal
import java.math.BigInteger

object TileCountParser {
    private const val MAX_INPUT_LENGTH = 500

    fun parseNumber(input: String): Result<BigDecimal> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(Exception(TileCountError.EmptyInput.userMessage))
        }

        if (trimmed.length > MAX_INPUT_LENGTH) {
            return Result.failure(Exception(TileCountError.InputTooLong.userMessage))
        }

        val sanitized = sanitizeDecimalString(trimmed)
        return try {
            val bigDecimal = BigDecimal(sanitized)
            Result.success(bigDecimal)
        } catch (_: Exception) {
            Result.failure(Exception(TileCountError.InvalidNumber.userMessage))
        }
    }

    fun parsePositiveInteger(input: String): Result<BigInteger> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(Exception(TileCountError.EmptyInput.userMessage))
        }
        if (trimmed.length > MAX_INPUT_LENGTH) {
            return Result.failure(Exception(TileCountError.InputTooLong.userMessage))
        }
        return try {
            val bi = BigInteger(trimmed)
            if (bi <= BigInteger.ZERO) {
                Result.failure(Exception(TileCountError.NonPositiveTilesPerBox.userMessage))
            } else {
                Result.success(bi)
            }
        } catch (_: Exception) {
            Result.failure(Exception(TileCountError.InvalidNumber.userMessage))
        }
    }

    private fun sanitizeDecimalString(input: String): String {
        var str = input
        if (str.startsWith(".")) {
            str = "0$str"
        } else if (str.startsWith("-.")) {
            str = "-0." + str.substring(2)
        } else if (str.startsWith("+.")) {
            str = "0." + str.substring(2)
        }

        if (str.endsWith(".")) {
            str = "${str}0"
        }
        return str
    }
}
