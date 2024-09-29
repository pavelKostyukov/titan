package com.example.titan7.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.titan7.data.QuoteRepositoryImpl

import com.example.titan7.data.WebResponse
import com.example.titan7.domain.QuoteUseCase
import com.example.titan7.domain.QuoteViewModelFactory
import com.example.titan7.presentation.ui.QuoteListScreen
import com.example.titan7.presentation.ui.TraderNetTheme
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TraderNetTheme {
                // Создание экземпляра репозитория
                  val repository = QuoteRepositoryImpl() // Создайте репозиторий

                // Создание экземпляра UseCase с репозиторием
                   val useCase = QuoteUseCase(repository)

                // Создание экземпляра  фабрики
                 val factory = QuoteViewModelFactory(useCase)

                // Получение ViewModel с использованием пользовательской фабрики
                  val viewModel: QuoteViewModel = viewModel(factory = factory)

                  QuoteListScreen(viewModel)
            }
        }

     /*   CoroutineScope(Dispatchers.IO).launch {
            startWebSocket()
        }*/
    }
}