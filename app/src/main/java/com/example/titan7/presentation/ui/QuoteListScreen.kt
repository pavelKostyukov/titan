package com.example.titan7.presentation.ui



import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.titan7.presentation.QuoteViewModel

@Composable
fun QuoteListScreen(viewModel: QuoteViewModel) {
    val quotes by viewModel.quotes.collectAsState()
    Log.d("QuoteListScreen", "Количество цитат: ${quotes.size}")
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        itemsIndexed(quotes) { index, quote ->
            QuoteItem(quote) // Получаем соответствующий логотип
            if (index < quotes.size - 1) {
                Divider(color = Color.LightGray, thickness = 1.dp)
            }
        }
    }
}