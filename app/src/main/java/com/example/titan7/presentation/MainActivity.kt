package com.example.titan7.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.titan7.presentation.ui.QuoteListScreen
import com.example.titan7.presentation.ui.TraderNetTheme


class MainActivity : ComponentActivity() {
    private val viewModel: QuoteViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel
        setContent {
            TraderNetTheme {
                QuoteListScreen(viewModel)
            }
        }
    }
}