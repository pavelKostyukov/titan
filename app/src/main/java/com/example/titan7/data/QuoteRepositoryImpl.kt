package com.example.titan7.data

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.titan7.R
import com.example.titan7.domain.QuoteRepository
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.pow

open class QuoteRepositoryImpl(
    private val okHttpClient: OkHttpClient,
    private val context: Context
) : QuoteRepository {

    private val tickersToWatchChanges by lazy {
        context.resources.getStringArray(R.array.default_tickers).toList()
    }

    private val gson = Gson()
    private val datList = CopyOnWriteArrayList<Listing>()
    private val _dataValue = MutableStateFlow<List<Listing>>(emptyList())
    private var webSocket: WebSocket? = null
    private val logoCache = ConcurrentHashMap<String, Bitmap?>()
    private var reconnectJob: Job? = null
    private var reconnectAttempts = 0

    companion object {
        const val DEFAULT_WEBSOCKET_URL = "wss://wss.tradernet.com"
        const val DEFAULT_LOGO_URL_TEMPLATE = "https://tradernet.com/logos/get-logo-by-ticker?ticker=%s"
        const val EVENT_QUOTE = "q"
        const val COMMAND_REALTIME_QUOTES = "realtimeQuotes"
        private const val MAX_RECONNECT_ATTEMPTS = 5
        private const val RECONNECT_BASE_DELAY_MS = 1000L
    }

    private fun startWebSocket() {
        val request = Request.Builder().url(DEFAULT_WEBSOCKET_URL).build()

        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                this@QuoteRepositoryImpl.webSocket = webSocket
                reconnectAttempts = 0
                subscribeToQuotes(webSocket, tickersToWatchChanges)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Received message: $text")
                CoroutineScope(Dispatchers.Default).launch {
                    try {
                        processWebSocketMessage(text)
                    } catch (e: Exception) {
                        Log.e("WebSocket", "Error processing message: ${e.message}", e)
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Error: ${t.message}")
                scheduleReconnect()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "Connection closed: $reason")
                scheduleReconnect()
            }
        }

        webSocket = okHttpClient.newWebSocket(request, webSocketListener)
    }

    private fun subscribeToQuotes(webSocket: WebSocket, tickers: List<String>) {
        val subscribeMessage = gson.toJson(listOf(COMMAND_REALTIME_QUOTES, tickers))
        webSocket.send(subscribeMessage)
    }

    suspend fun processWebSocketMessage(text: String) {
        val jelement = JsonParser().parse(text)
        if (jelement is JsonArray && jelement.size() > 1) {
            val event = jelement[0].asString
            if (event == EVENT_QUOTE) {
                val data = jelement[1]
                handleQuoteUpdate(data)
            }
        } else {
            Log.e("WebSocket", "Unexpected message format: $text")
        }
    }

    private suspend fun handleQuoteUpdate(data: JsonElement) {
        try {
            val response = gson.fromJson(data, WebResponse::class.java)
            val newQuote = response.mapToListing()

            newQuote.name?.let { ticker ->
                val logoBitmap = logoCache[ticker] ?: getCompanyLogo(ticker)?.also {
                    logoCache[ticker] = it
                }
                updateOrAddQuote(newQuote.copy(logo = logoBitmap))
                updateStateFlow()
            } ?: Log.w("WebSocket", "Received quote with null ticker")
        } catch (e: Exception) {
            Log.e("WebSocket", "Error processing data: ${e.message}", e)
        }
    }

    fun updateOrAddQuote(newQuote: Listing) {
        val existingIndex = datList.indexOfFirst { it.name == newQuote.name }
        if (existingIndex != -1) {
            datList[existingIndex] = datList[existingIndex].updateFrom(newQuote)
        } else {
            datList.add(newQuote)
        }
    }

    private suspend fun updateStateFlow() {
        _dataValue.emit(datList.toList())
    }

    private fun scheduleReconnect() {
        reconnectJob?.cancel()
        reconnectJob = CoroutineScope(Dispatchers.IO).launch {
            if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                val delayMs = (RECONNECT_BASE_DELAY_MS * 2.0.pow(reconnectAttempts.toDouble())).toLong()
                    .coerceAtMost(30_000)
                delay(delayMs)
                reconnectAttempts++
                startWebSocket()
            }
        }
    }

    override suspend fun startSocket() {
        webSocket?.close(1000, "Reinitializing connection")
        startWebSocket()
    }

    override val updateDate: StateFlow<List<Listing>>
        get() = _dataValue.asStateFlow()

    suspend fun getCompanyLogo(ticker: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val logoUrl = DEFAULT_LOGO_URL_TEMPLATE.format(ticker.lowercase())
            okHttpClient.newCall(Request.Builder().url(logoUrl).build()).execute().use { response ->
                if (response.isSuccessful && response.body?.contentType()?.type == "image") {
                    response.body?.byteStream()?.use(BitmapFactory::decodeStream)
                } else null
            }
        } catch (e: Exception) {
            Log.e("LogoRequest", "Error for $ticker: ${e.message}")
            null
        }
    }

    private fun Listing.updateFrom(newData: Listing): Listing = this.copy(
        change = newData.change,
        symbol = newData.symbol ?: this.symbol,
        logo = newData.logo ?: this.logo
    )
}