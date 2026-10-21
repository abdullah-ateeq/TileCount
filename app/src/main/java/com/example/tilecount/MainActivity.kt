package com.example.tilecount

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.tilecount.ui.TileCountViewModel
import com.example.tilecount.ui.navigation.TileCountApp
import com.example.tilecount.ui.theme.TileCountTheme

class MainActivity : ComponentActivity() {
    private val viewModel: TileCountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            TileCountTheme(
                appTheme = uiState.settings.theme,
                dynamicColor = uiState.settings.dynamicColor
            ) {
                TileCountApp(viewModel = viewModel)
            }
        }
    }
}
