package com.example.titan7.presentation.ui



import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.titan7.presentation.QuoteViewModel

@Composable
fun QuoteListScreen(viewModel: QuoteViewModel) {
    val quotes by viewModel.quotes.collectAsState()
    // Логируем количество цитат
    Log.d("QuoteListScreen", "Количество цитат: ${quotes.size}")
    Column(modifier = Modifier.padding(16.dp)) {
        quotes.forEach { quote ->
            QuoteItem(quote)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}