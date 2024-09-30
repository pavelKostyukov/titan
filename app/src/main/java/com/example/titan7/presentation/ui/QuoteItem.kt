package com.example.titan7.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        // Отображение иконки, если она доступна
        quote.logo?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Logo for ${quote.name}",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 8.dp)
            )
        } ?: run {
            // Отображение заглушки, если иконка отсутствует
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Gray)
                    .padding(end = 8.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Column {
            quote.name?.let { Text(text = it, style = MaterialTheme.typography.labelSmall) }
            quote.symbol?.let { Text(text = it, style = MaterialTheme.typography.labelSmall) }

            Text(
                text = "(${quote.previousClose}%)",
                color = if (quote.previousClose < 0) Color.Red else Color.DarkGray,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "(${quote.change}%)",
                color = if (quote.change < 0) Color.Red else Color.DarkGray,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "(${quote.price}%)",
                color = if (quote.price < 0) Color.Red else Color.DarkGray,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}