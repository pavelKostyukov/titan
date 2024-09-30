package com.example.titan7.presentation.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.titan7.data.Quote
import com.example.titan7.presentation.QuoteViewModel

@Composable
fun QuoteItem(quote: Quote, logo: List<Bitmap>, viewModel: QuoteViewModel) {
    Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 0.dp)) {
        /*CompanyLogo(viewModel = viewModel, ticker = logo.toString())*/
        // Проверяем, получен ли логотип
        logo.map {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Company Logo",
                        modifier = Modifier.padding(8.dp) // Добавляем некоторый отступ для логотипа
                    )
            }
        }

        Text(
            text = quote.symbol,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "(${quote.change}%)",
            color = if (quote.change < 0) Color.Red else Color.Green,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = quote.price.toString(),
            style = MaterialTheme.typography.bodyLarge
        )
    }