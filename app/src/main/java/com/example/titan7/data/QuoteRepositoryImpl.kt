package com.example.titan7.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.titan7.domain.QuoteRepository
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
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
    private val datList = mutableListOf<Listing>()
    private val webSocketUrl = "wss://wss.tradernet.com"
    private val tickersToWatchChanges = listOf(
        "AFLT",
        "AAPL.US",
        "SP500.IDX",
        "AAPL.US",
        "RSTI",
        "GAZP",
        "MRKZ",
        "RUAL",
        "HYDR",
        "MRKS",
        "SBER",
        "FEES",
        "TGKA",
        "VTBR",
        "ANH.US",
        "VICL.US",
        "RG.US",
        "NBL.US",
        "YETI.US",
        "WSFS.US",
        "NIO.US",
        "DXC.US",
        "MIC.US",
        "HSBC.US",
        "EXPN.EU",
        "GSK.EU",
        "SHP.EU",
        "MAN.EU",
        "DB1.EU",
        "MUV2.EU",
        "TATE.EU",
        "KGF.EU",
        "MGGT.EU",
        "SGGD.EU"
    )


    /**
     * Получаем котировки
     * Подписываемся на веб-сокет и возвращаем поток котировок.
     */
    private val dataValue = MutableStateFlow<List<Listing>>(listOf())

    private fun startWebSocket() {
        val request = Request.Builder().url(webSocketUrl).build()
        datList.clear()
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

        val newQuote = response.mapToListing()

        // Применяем логику замены или уникальности
        val existingQuotes = datList.map { it.name }
        // Предполагая, что у Quote есть поле "name"
        if (!existingQuotes.contains(newQuote.name)) {
            // Получаем логотип для нового тикера
            val logoBitmap = newQuote.name?.let {
                getCompanyLogo(it)
            }

            newQuote.logo = logoBitmap // Присваиваем иконку котировке

            datList.add(newQuote)
        } else {
            // Обновляем существующую котировку
            val index = existingQuotes.indexOf(newQuote.name)
            if (index != -1) {
                datList[index] = newQuote
                // Обновляем иконку, если необходимо
                datList[index].logo = newQuote.logo ?: datList[index].logo
            }
        }

        // Обновляем состояние
        dataValue.value = datList.toList()
    }

    override suspend fun startSocket() {
        startWebSocket()
    }

    private suspend fun getCompanyLogo(ticker: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            val logoUrl =
                "https://tradernet.com/logos/get-logo-by-ticker?ticker=${ticker.lowercase()}"
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
                Log.e(
                    "LogoRequest",
                    "Failed to load logo for $ticker with status: ${response.code}"
                )
                null
            }
        }
    }

    override val updateDate: StateFlow<List<Listing>>
        get() = dataValue.asStateFlow()
}