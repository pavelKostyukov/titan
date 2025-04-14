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

open class QuoteRepositoryImpl(
    private val okHttpClient: OkHttpClient
) : QuoteRepository {

    private val gson = Gson()
    private val datList = mutableListOf<Listing>()
    private val _dataValue = MutableStateFlow<List<Listing>>(emptyList())
    private var webSocket: WebSocket? = null
    private val logoCache = mutableMapOf<String, Bitmap?>()

    private fun startWebSocket() {
        val request = Request.Builder().url(DEFAULT_WEBSOCKET_URL).build()

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
                this@QuoteRepositoryImpl.webSocket = webSocket
                subscribeToQuotes(webSocket, tickersToWatchChanges)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Received message: $text")
                CoroutineScope(Dispatchers.Default).launch {
                    processWebSocketMessage(text)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Error: ${t.message}")
                reconnectWebSocket()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "Connection closed: $reason")
                reconnectWebSocket()
            }
        }

        okHttpClient.newWebSocket(request, webSocketListener)
    }

    companion object {
        const val DEFAULT_WEBSOCKET_URL = "wss://wss.tradernet.com"
        const val DEFAULT_LOGO_URL_TEMPLATE = "https://tradernet.com/logos/get-logo-by-ticker?ticker=%s"
    }

    private suspend fun processWebSocketMessage(text: String) {
        val jelement = JsonParser().parse(text)
        if (jelement is JsonArray && jelement.size() > 1) {
            val event = jelement[0].asString
            if (event == "q") {
                val data = jelement[1]
                handleQuoteUpdate(data)
            }
        } else {
            Log.e("WebSocket", "Unexpected message format: $text")
        }
    }

    private fun subscribeToQuotes(webSocket: WebSocket, tickers: List<String>) {
        val subscribeMessage = """["realtimeQuotes", ["${tickers.joinToString("\",\"") { it }}"]]"""
        webSocket.send(subscribeMessage)
    }

    private suspend fun handleQuoteUpdate(data: JsonElement) {
        try {
            val response = gson.fromJson(data, WebResponse::class.java)
            Log.d("WebSocket", "Received quote: $response")

            val newQuote = response.mapToListing()
            Log.d("Quote Update",
                "Name: ${newQuote.name}, " +
                        "Price: ${newQuote.formattedPrice}, " +
                        "Change: ${newQuote.formattedChange}")
            val logoBitmap = logoCache[newQuote.name] ?: newQuote.name?.let {
                val logoFromNetwork = getCompanyLogo(it)
                logoCache[newQuote.name] = logoFromNetwork
                logoFromNetwork
            }

            updateOrAddQuote(newQuote.copy(logo = logoBitmap))
            updateStateFlow()
        } catch (e: Exception) {
            Log.e("WebSocket", "Error processing data: ${e.message}")
        }
    }

    private fun updateOrAddQuote(newQuote: Listing) {
        synchronized(datList) {
            val existingIndex = datList.indexOfFirst { it.name == newQuote.name }
            if (existingIndex != -1) {
                val existing = datList[existingIndex]
                datList[existingIndex] = existing.updateFrom(newQuote)
            } else {
                datList.add(newQuote)
            }
        }
    }

    private suspend fun updateStateFlow() {
        val newList = synchronized(datList) { datList.toList() }
        withContext(Dispatchers.Main) {
            _dataValue.value = newList
        }
    }

    private fun reconnectWebSocket() {
        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                Log.d("WebSocket", "Attempting reconnect...")
                startWebSocket()
            }.onFailure {
                Log.e("WebSocket", "Reconnect failed: ${it.message}")
            }
        }
    }

    override suspend fun startSocket() {
        startWebSocket()
    }

    override val updateDate: StateFlow<List<Listing>>
        get() = _dataValue.asStateFlow()

    private suspend fun getCompanyLogo(ticker: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val logoUrl = DEFAULT_LOGO_URL_TEMPLATE.format(ticker.lowercase())
                val response = okHttpClient.newCall(Request.Builder().url(logoUrl).build()).execute()

                if (response.isSuccessful && response.header("Content-Type")?.startsWith("image/") == true) {
                    response.body?.byteStream()?.use { BitmapFactory.decodeStream(it) }
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.e("LogoRequest", "Error for $ticker: ${e.message}")
                null
            }
        }
    }

    private fun Listing.updateFrom(newData: Listing): Listing {
        return this.copy(
            change = newData.change,
            symbol = newData.symbol ?: this.symbol,
            logo = newData.logo ?: this.logo
        )
    }
}