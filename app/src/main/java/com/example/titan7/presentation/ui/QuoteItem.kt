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
import androidx.compose.foundation.shape.RoundedCornerShape
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Row {
                // Отображение логотипа, если он доступен
                quote.logo?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Logo for ${quote.name}",
                        modifier = Modifier
                            .size(24.dp) // Размер логотипа
                    )
                } ?: run {
                    // Отображение заглушки, если логотип отсутствует
                    Box(
                        modifier = Modifier
                            .size(24.dp) // Размер заглушки тот же
                            .background(Color.Gray, shape = RoundedCornerShape(4.dp))
                    )
                }
                // Проверка на null и вывод сообщения, если данных нет
                Text(
                    text = quote.name ?: "Ошибка получения катировки",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp) // Отступ слева
                )
            }

            Text(
                text = quote.symbol ?: "Нет символа катировки",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )
        }
        // Правая колонка с выравниванием по правому краю
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.weight(1f) // Занять оставшееся пространство
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