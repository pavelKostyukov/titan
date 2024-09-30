package com.example.titan7.presentation.ui



import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
//noinspection UsingMaterialAndMaterial3Libraries
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
    val logo by viewModel.logo.collectAsState()
    // Логируем количество цитат
    Log.d("QuoteListScreen", "Количество цитат: ${quotes.size}")
    Column(modifier = Modifier.padding(16.dp)) {
        quotes.forEachIndexed { index, quote ->
            QuoteItem(quote,logo,viewModel)
            if (index < quotes.size - 1) { // Добавляем Divider только между элементами
                Divider(color = Color.LightGray, thickness = 1.dp)
            }
        }
    }
}