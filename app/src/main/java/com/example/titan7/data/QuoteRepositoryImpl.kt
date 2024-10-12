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
import java.text.DecimalFormat

open class QuoteRepositoryImpl(private val okHttpClient: OkHttpClient) : QuoteRepository {
    private val gson = Gson()
    private val datList = mutableListOf<Listing>()
    val decimalFormat =
        DecimalFormat("#.##") // Форматируем числа до двух знаков после запятой
    val dataValue = MutableStateFlow<List<Listing>>(listOf())

    private fun startWebSocket() {
        val request = Request.Builder().url(Companion.webSocketUrl).build()

        val tickersToWatchChanges = listOf(
            "AFLT", "AAPL.US", "SP500.IDX", "RSTI", "GAZP",
            "MRKZ", "RUAL", "HYDR", "MRKS", "SBER",
            "FEES", "TGKA", "VTBR", "ANH.US", "VICL.US",
            "RG.US", "NBL.US", "YETI.US", "WSFS.US", "NIO.US",
            "DXC.US", "MIC.US", "HSBC.US", "EXPN.EU", "GSK.EU",
            "SHP.EU", "MAN.EU", "DB1.EU", "MUV2.EU", "TATE.EU",
            "KGF.EU", "MGGT.EU", "SGGD.EU"
        )

        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                subscribeToQuotes(webSocket, tickersToWatchChanges)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Received message: $text")
                val jelement = JsonParser().parse(text)
                if (jelement is JsonArray && jelement.size() > 1) {
                    val event = jelement[0].asString
                    if (event == "q") {
                        val data = jelement[1]
                        CoroutineScope(Dispatchers.IO).launch {
                            handleQuoteUpdate(data)
                        }
                    }
                } else {
                    Log.e("WebSocket", "Unexpected message format: $text")
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

    private fun subscribeToQuotes(webSocket: WebSocket, tickers: List<String>) {
        val subscribeMessage = """["realtimeQuotes", ["${tickers.joinToString("\",\"") { it }}"]]"""
        webSocket.send(subscribeMessage)
    }

    private suspend fun handleQuoteUpdate(data: JsonElement) {
        try {
            val response = gson.fromJson(data, WebResponse::class.java)
            Log.d("WebSocket", "Received quote: $response")

            val newQuote = response.mapToListing() ?: run {
                Log.e("WebSocket", "Failed to map response to Listing")
                return
            }

            // Обновление существующих данных, если они уже есть
            updateOrAddQuote(newQuote)

            // Получаем логотип асинхронно
            newQuote.logo = newQuote.name?.let { getCompanyLogo(it) }
            updateStateFlow()
        } catch (e: Exception) {
            Log.e("WebSocket", "Error processing data: ${e.message}")
        }
    }

    private fun updateOrAddQuote(newQuote: Listing) {
        synchronized(datList) {
            val existingQuoteIndex = datList.indexOfFirst { it.name == newQuote.name }
            if (existingQuoteIndex != -1) {
                // Обновляем данные существующей котировки
                val existingQuote = datList[existingQuoteIndex]
                existingQuote.updateFrom(newQuote) // Метод для обновления существующей котировки
            } else {
                // Добавляем новую котировку
                datList.add(newQuote)
            }
        }
    }

    private suspend fun updateStateFlow() {
        withContext(Dispatchers.Main) {
            dataValue.value = datList.toList()
        }
    }

    override suspend fun startSocket() {
        startWebSocket()
    }

    override val updateDate: StateFlow<List<Listing>>
        get() = dataValue.asStateFlow()

    suspend fun getCompanyLogo(ticker: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            val logoUrl = Companion.logoUrlTemplate.format(ticker.lowercase())
            val response = okHttpClient.newCall(Request.Builder().url(logoUrl).build()).execute()

            val contentType = response.header("Content-Type")
            Log.d("LogoRequest", "Content-Type $ticker: $contentType")

            if (response.isSuccessful && contentType?.startsWith("image/") == true) {
                response.body?.byteStream()?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                } ?: run {
                    Log.e("LogoRequest", "Error processing logo for $ticker")
                    null
                }
            } else {
                Log.e("LogoRequest", "$ticker status: ${response.code}")
                null
            }
        }
    }

    companion object {
        private const val webSocketUrl = "wss://wss.tradernet.com"
        private const val logoUrlTemplate = "https://tradernet.com/logos/get-logo-by-ticker?ticker=%s"
    }

    // Дополнительный класс или метод для обновления котировки
    fun Listing.updateFrom(newData: Listing) {
      //  this.value = newData.value // Например, обновляем значение
        this.change = newData.change // Обновляем изменение
        // Добавьте сюда любые другие поля, которые необходимо обновить
    }

    // Функция форматирования числа без экспоненциального формата
    fun formatNumber(value: Double): String {
        return decimalFormat.format(value)
    }
}

