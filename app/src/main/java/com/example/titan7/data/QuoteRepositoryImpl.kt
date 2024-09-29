package com.example.titan7.data

import android.util.Log
import com.example.titan7.domain.QuoteRepository
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener


open class QuoteRepositoryImpl(private val okHttpClient: OkHttpClient) : QuoteRepository {
    private val gson = Gson()
    private val datList = mutableListOf<Quote>()
    private val webSocketUrl = "wss://wss.tradernet.com"
    private val tickersToWatchChanges = listOf("AFLT", "AAPL.US", "SP500.IDX", "AAPL.US")

    /**
     * Получаем котировки
     * Подписываемся на веб-сокет и возвращаем поток котировок.
     */
    private val dataValue = MutableStateFlow<List<Quote>>(listOf())

    private fun startWebSocket() {
        val request = Request.Builder().url(webSocketUrl).build()
        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // Формируем строку для подписки на тикеры
                val subscribeMessage =
                    """["realtimeQuotes", ["${tickersToWatchChanges.joinToString("\",\"") { it }}"]]"""
                webSocket.send(subscribeMessage)
                Log.d("WebSocket", "Subscribed to realtimeQuotes: $tickersToWatchChanges")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Received message: $text")
                val jelement = JsonParser().parse(text)
                if (jelement is JsonArray && jelement.size() > 1) {
                    val event = jelement[0].asString
                    if (event == "q") {
                        val data = jelement[1]
                        handleQuoteUpdate(data)
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Error: ${t.message}")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "Connection closed: $reason")
            }
        }

        okHttpClient.newWebSocket(request, webSocketListener)
    }

    private fun handleQuoteUpdate(data: JsonElement) {
        // Извлекаем данные из JSON объекта и преобразуем их в WebResponse
        val response = gson.fromJson(data, WebResponse::class.java)
        Log.d("WebSocket", "Connection closed: $response")

        // Создаем список Quote из данных WebResponse
        datList.add(response.mapToListing())
        dataValue.value = datList
    }

    override suspend fun startSocket() {
        startWebSocket()
    }

    override val updateDate: StateFlow<List<Quote>>
        get() = dataValue.asStateFlow()

}