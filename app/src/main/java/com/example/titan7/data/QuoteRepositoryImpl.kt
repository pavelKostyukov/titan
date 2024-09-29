package com.example.titan7.data

import android.util.Log
import com.example.titan7.domain.Listing
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
import java.util.concurrent.TimeUnit

open class QuoteRepositoryImpl() : QuoteRepository {
    private val client = OkHttpClient.Builder()
        .readTimeout(2000, TimeUnit.MILLISECONDS)
        .build()
    private val gson = Gson()
    private val webSocketUrl = "wss://wss.tradernet.com"
    private val tickersToWatchChanges = listOf("AFLT", "AAPL.US","SP500.IDX")

    // Сохраним текущие котировки
    private val currentQuotes = mutableListOf<Listing>()

    /**
     * Получаем котировки
     * Подписываемся на веб-сокет и возвращаем поток котировок.
     */
    private val dataValue = MutableStateFlow<List<Quote>>(listOf())

    /*
        suspend fun getQuotes(): Flow<List<Quote>> {

        }
    */

    private fun startWebSocket(/*listener: (List<Quote>) -> Unit*/) {
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
                        handleQuoteUpdate(data/*, listener*/)
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

        client.newWebSocket(request, webSocketListener)
    }

    private fun handleQuoteUpdate(data: JsonElement/*, listener: (List<Quote>) -> Unit*/) {
        // Извлекаем данные из JSON объекта и преобразуем их в WebResponse
        val response = gson.fromJson(data, WebResponse::class.java)

        // Создаем список Quote из данных WebResponse

         val mapping =    Quote(
                symbol = response.quoteBasis,          // Символ
                price = response.chg,            // Текущая цена
                change = response.ltp,          // Изменение цены
                previousClose = response.p22, // Цена закрытия
                exchange = response.bbt        // Биржа
            )
        // Обновляем значение.
   //     dataValue.value = mapping
    }


    override suspend fun startSocket() {
        startWebSocket()
    }

    override val updateDate: StateFlow<List<Quote>>
        get() = dataValue.asStateFlow()

}