package com.example.titan7.data

import com.example.titan7.domain.Quote
import kotlinx.coroutines.flow.Flow

class WebSocketClient : QuoteRepository {
    override fun getQuotes(): Flow<List<Quote>> {
        // Здесь будет логика подключения к WebSocket и получения данных
        return flow {
            // Эмуляция получения данных
            emit(listOf(
                Quote("AAPL.US", 1.5, "NASDAQ", "Apple Inc.", 150.0, 2.0),
                Quote("GAZP", -0.5, "MOEX", "Газпром", 200.0, -1.0)
            ))
        }
    }
}