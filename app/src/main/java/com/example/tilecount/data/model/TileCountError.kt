package com.example.tilecount.data.model

sealed interface TileCountError {
    val userMessage: String

    data object EmptyInput : TileCountError {
        override val userMessage: String = "Enter all required dimensions."
    }

    data object InvalidNumber : TileCountError {
        override val userMessage: String = "Enter a valid number."
    }

    data object NonPositiveDimension : TileCountError {
        override val userMessage: String = "Dimensions must be greater than zero."
    }

    data object NonPositiveTilesPerBox : TileCountError {
        override val userMessage: String = "Tiles per box must be greater than zero."
    }

    data object InvalidWastePercent : TileCountError {
        override val userMessage: String = "Waste allowance must be between 0% and 100%."
    }

    data object InputTooLong : TileCountError {
        override val userMessage: String = "Number is too long. Maximum 500 digits."
    }

    data object QuantityTooLarge : TileCountError {
        override val userMessage: String = "Calculated quantity is too large. Please check your dimensions and units."
    }
}
