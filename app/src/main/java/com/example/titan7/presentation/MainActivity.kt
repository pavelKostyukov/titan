package com.example.titan7.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.titan7.presentation.ui.TraderNetTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    val webSocketUrl = "wss://wss.tradernet.com"
    val tickersToWatchChanges = listOf("AAPL.US")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
                startWebSocket()
        }

        setContent {
            TraderNetTheme {
                // Создание экземпляра репозитория
                //  val repository = QuoteRepositoryImpl() // Создайте репозиторий

                // Создание экземпляра UseCase с репозиторием
                //   val useCase = QuoteUseCase(repository)

                // Создание экземпляра  фабрики
                // val factory = QuoteViewModelFactory(useCase)

                // Получение ViewModel с использованием пользовательской фабрики
                //  val viewModel: QuoteViewModel = viewModel(factory = factory)

                //  QuoteListScreen(viewModel)
            }
        }
    }
    suspend fun startWebSocket() {
        val request = Request.Builder().url(webSocketUrl).build()
        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // Подписка на обновления котировок
                response
                val subscribeMessage = """["quotes", ${tickersToWatchChanges}]"""
                webSocket.send(subscribeMessage)
                println("Подписка на котировки: $tickersToWatchChanges")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                val (event, data) = parseMessage(text)
                if (event == "q") {
                    updateWatcher(data)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("Ошибка: ${t.message}")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                println("Соединение закрыто: $reason")
            }
        }
        val webSocket = client.newWebSocket(request, webSocketListener)
    }
    fun parseMessage(message: String): Pair<String, List<Any>> {
        // Здесь простая логика разбора строки JSON, её нужно реализовать по вашим требованиям.
        val json = kotlinx.serialization.json.Json.parseToJsonElement(message)
        val event = json.jsonArray[0].jsonPrimitive.content
        val data = json.jsonArray.drop(1).map { it.jsonPrimitive.content } // Предполагается, что data - это массив
        return Pair(event, data)
    }

    fun updateWatcher(data: List<Any>) {
        data.forEach { println(it) }
    }
}