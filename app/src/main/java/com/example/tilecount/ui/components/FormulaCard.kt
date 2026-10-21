package com.example.tilecount.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FormulaCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "TILE FORMULAS",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        FormulaItem(
            name = "Surface Area",
            formula = "A = Length × Width"
        )

        Spacer(modifier = Modifier.height(8.dp))

        FormulaItem(
            name = "Tile Area",
            formula = "T = Tile Length × Tile Width"
        )

        Spacer(modifier = Modifier.height(8.dp))

        FormulaItem(
            name = "Coverage With Waste",
            formula = "C = A × (1 + Waste% / 100)"
        )

        Spacer(modifier = Modifier.height(8.dp))

        FormulaItem(
            name = "Tiles Required",
            formula = "N = CEILING(C / T)"
        )

        Spacer(modifier = Modifier.height(8.dp))

        FormulaItem(
            name = "Boxes Required",
            formula = "B = CEILING(N / Tiles Per Box)"
        )
    }
}

@Composable
private fun FormulaItem(name: String, formula: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = formula,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
