package com.example.titan7.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.titan7.presentation.QuoteViewModel

@Composable
fun QuoteListScreen(viewModel: QuoteViewModel) {
    val quotes by viewModel.quotes.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        quotes.forEach { quote ->
            QuoteItem(quote)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}