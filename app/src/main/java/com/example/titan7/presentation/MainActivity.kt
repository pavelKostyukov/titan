package com.example.titan7.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.titan7.data.QuoteRepositoryImpl
import com.example.titan7.domain.QuoteUseCase
import com.example.titan7.domain.QuoteViewModelFactory
import com.example.titan7.presentation.ui.QuoteListScreen
import com.example.titan7.presentation.ui.TraderNetTheme
import kotlinx.coroutines.delay
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

    private val webSocketUrl = "wss://wss.tradernet.com"
    val tickersToWatchChanges = listOf("AAPL.US")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val client = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()

        val request = Request.Builder().url(webSocketUrl).build()
        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // Подписка на обновления котировок
                val subscribeMessage = """["quotes", ${tickersToWatchChanges}]"""
                webSocket.send(subscribeMessage)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                val (event, data) = parseMessage(text)
                if (event == "q") {
                    updateWatcher(data)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                System.err.println("Error: ${t.message}")
            }
        }
        val webSocket = client.newWebSocket(request, webSocketListener)

        // Убедитесь, что программа продолжает работать, пока мы не решим её остановить
        runBlocking {
            delay(Long.MAX_VALUE)
        }

    val webSocketUrl = "wss://wss.tradernet.com"
        val tickersToWatchChanges = listOf("AAPL.US")
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
    fun parseMessage(message: String): Pair<String, List<Any>> {
        // Вы должны написать логику разбора входящего сообщения JSON
        // Это просто пример, фактическая реализация зависит от формата сообщения
        val json = kotlinx.serialization.json.Json.parseToJsonElement(message)
        val event = json.jsonArray[0].jsonPrimitive.content
        val data = json.jsonArray.drop(1).map { it.jsonPrimitive.content } // Предполагается, что data - это массив
        Log.d("test", data.toString())
        return Pair(event, data)
    }

    fun updateWatcher(data: List<Any>) {
        data.forEach { println(it) }
    }
}