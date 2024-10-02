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
    private val dataValue = MutableStateFlow<List<Listing>>(listOf())

    private fun startWebSocket() {
        val request = Request.Builder().url(webSocketUrl).build()
        datList.clear()
        val tickersToWatchChanges = listOf(
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
        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                val subscribeMessage =
                    """["realtimeQuotes", ["${tickersToWatchChanges.joinToString("\",\"") { it }}"]]"""
                webSocket.send(subscribeMessage)
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

    // Зависимость от контекста корутин
    suspend fun handleQuoteUpdate(data: JsonElement) {
        try {
            val response = gson.fromJson(data, WebResponse::class.java)
            Log.d("WebSocket", "Received quote: $response")

            val newQuote = response.mapToListing()
            if (newQuote == null) {
                Log.e("WebSocket", "Failed to map response to Listing")
                return
            }

            newQuote.logo = newQuote.name?.let { getCompanyLogo(it) }

            synchronized(datList) {
                val existingIndex = datList.indexOfFirst { it.name == newQuote.name }
                if (existingIndex == -1) {
                    datList.add(newQuote)
                } else {
                    datList[existingIndex] = newQuote
                }
            }

            // Обновляем StateFlow на главном потоке
            withContext(Dispatchers.Main) {
                dataValue.value = datList.toList()
            }
        } catch (e: Exception) {
            Log.e("WebSocket", "Ошибка при обработке данных: ${e.message}")
        }
    }

    private suspend fun getCompanyLogo(ticker: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            val logoUrl = "https://tradernet.com/logos/get-logo-by-ticker?ticker=${ticker.lowercase()}"
            val request = Request.Builder().url(logoUrl).build()
            val response = okHttpClient.newCall(request).execute()

            val contentType = response.header("Content-Type")
            Log.d("LogoRequest", "Content-Type $ticker: $contentType")

            if (response.isSuccessful && contentType != null && contentType.startsWith("image/")) {
                response.body?.byteStream()?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                } ?: run {
                    Log.e("LogoRequest", "Error $ticker")
                    null
                }
            } else {
                Log.e("LogoRequest", "$ticker status: ${response.code}")
                null
            }
        }
    }

    override suspend fun startSocket() {
        startWebSocket()
    }

    override val updateDate: StateFlow<List<Listing>>
        get() = dataValue.asStateFlow()
}