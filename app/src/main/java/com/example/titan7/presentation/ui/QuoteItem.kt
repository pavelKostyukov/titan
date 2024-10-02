package com.example.titan7.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.titan7.data.Listing

@Composable
fun QuoteItem(quote: Listing) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Отображение логотипа, если он доступен
        quote.logo?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Logo for ${quote.name}",
                modifier = Modifier
                    .size(40.dp) // Размер логотипа - в тз нет figma - не указано
                    .padding(end = 8.dp)
            )
        } ?: run {
            // Отображение заглушки, если логотип отсутствует
            Box(
                modifier = Modifier
                    .size(40.dp) // Размер заглушки -тот же
                    .background(Color.Gray)
                    .padding(end = 8.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            // Проверка на null и вывод сообщения, если данных нет
            Text(text = quote.name ?: "Ошибка получения катировки", style = MaterialTheme.typography.bodyLarge)
            Text(text = quote.symbol ?: "Нет символа катировки", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            // Процент с изменением цвета
            Text(
                text = "${quote.price}%",
                color = if (quote.price < 0) Color.Red else Color.Green,
                style = MaterialTheme.typography.bodyLarge
            )
            Row {
                Text(
                    text = "${quote.exchange}",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Black)
                )
                Spacer(modifier = Modifier.width(4.dp)) // Отступ между текстами
                Text(
                    text = "(${quote.change})",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Black)
                )
            }
        }
    }
}