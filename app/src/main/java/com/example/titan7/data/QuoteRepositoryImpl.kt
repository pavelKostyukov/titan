package com.example.titan7.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.titan7.domain.LogoResponse
import com.example.titan7.domain.QuoteRepository
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val logoValue = MutableStateFlow<List<Bitmap>>(listOf())

    private fun startWebSocket() {
        val request = Request.Builder().url(webSocketUrl).build()
        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
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
                        // Вызываем здесь handleQuoteUpdate как suspend
                        CoroutineScope(Dispatchers.IO).launch {
                            handleQuoteUpdate(data)
                        }
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

    // Зависимость от контекста корутин
    private suspend fun handleQuoteUpdate(data: JsonElement) {
        // Извлекаем данные из JSON объекта и преобразуем их в WebResponse
        val response = gson.fromJson(data, WebResponse::class.java)
        Log.d("WebSocket", "Received quote: $response")

        // Создаем список Quote из данных WebResponse
        val newQuote = response.mapToListing()
        datList.add(newQuote)
        dataValue.value = datList

        // Получаем логотип для нового тикера
        val logoUrl = getCompanyLogo(newQuote.name)
        logoUrl?.let {
            // Обновляем список логотипов
            logoValue.value = logoValue.value + it // Добавляем полученный логотип в список логотипов
        }
    }

    override suspend fun startSocket() {
        startWebSocket()
    }

    private suspend fun getCompanyLogo(ticker: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            val logoUrl = "https://tradernet.com/logos/get-logo-by-ticker?ticker=${ticker.lowercase()}"
            val request = Request.Builder().url(logoUrl).build()
            val response: Response = okHttpClient.newCall(request).execute()

            // Логируем тип контента для отладки
            val contentType = response.header("Content-Type")
            Log.d("LogoRequest", "Content-Type for $ticker: $contentType")

            return@withContext if (response.isSuccessful) {
                // Проверяем, что это изображение
                if (contentType != null && contentType.startsWith("image/")) {
                    // Читаем байты изображения
                    response.body?.byteStream()?.use { inputStream ->
                        // Декодируем изображение из потока
                        BitmapFactory.decodeStream(inputStream)
                    }
                } else {
                    Log.e("LogoRequest", "Unexpected content type: $contentType")
                    null
                }
            } else {
                Log.e("LogoRequest", "Failed to load logo for $ticker with status: ${response.code}")
                null
            }
        }
    }

    override val updateDate: StateFlow<List<Quote>>
        get() = dataValue.asStateFlow()
    override val getCompanyLogo: StateFlow<List<Bitmap>>
        get() = logoValue.asStateFlow()
}