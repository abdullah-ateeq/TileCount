package com.example.tilecount.data.model

import java.math.RoundingMode

enum class AppTheme(val displayName: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark")
}

data class AppSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val dynamicColor: Boolean = true,
    val hapticFeedback: Boolean = true,
    val animations: Boolean = true,
    val decimalPlaces: Int = 6,
    val roundingMode: RoundingMode = RoundingMode.HALF_UP,
    val showFormula: Boolean = true,
    val showCalculationSteps: Boolean = true,
    val showTileDiagram: Boolean = true,
    val showPurchaseDetails: Boolean = true,
    val confirmDelete: Boolean = true
)
