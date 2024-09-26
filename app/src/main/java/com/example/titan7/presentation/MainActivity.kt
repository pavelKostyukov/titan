package com.example.titan7.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.titan7.data.WebSocketClient
import com.example.titan7.domain.QuoteUseCase
import com.example.titan7.domain.QuoteViewModelFactory
import com.example.titan7.presentation.theme.Titan7Theme
import com.example.titan7.presentation.ui.QuoteListScreen
import com.example.titan7.presentation.ui.TraderNetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TraderNetTheme {
                val viewModel: QuoteViewModel = viewModel(factory = QuoteViewModelFactory(
                    QuoteUseCase(WebSocketClient())
                )
                )
                QuoteListScreen(viewModel)
            }
        }
    }
}