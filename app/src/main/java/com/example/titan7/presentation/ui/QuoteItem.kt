package com.example.titan7.presentation.ui

import androidx.compose.runtime.Composable
import com.example.titan7.domain.Quote

@Composable
fun QuoteItem(quote: Quote) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = quote.ticker, fontSize = 20.sp)
            Text(text = quote.name, fontSize = 14.sp)
        }
        Text(
            text = "${quote.lastPrice} (${quote.changePercent}%)",
            fontSize = 16.sp,
            color = if (quote.changePercent >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }
}