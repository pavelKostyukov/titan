package com.example.titan7.data

import android.util.Log
import com.example.titan7.domain.Quote
import com.example.titan7.domain.QuoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.subscribeText

open class QuoteRepositoryImpl(
    private val stompClient: StompClient,
    private val json: Json
): QuoteRepository {
    private var session: StompSession? = null
  //  private val sessionState = MutableStateFlow(SessionState())
    private suspend fun observeAllMessages() {
      /*  session?.subscribeText("/topic/${token.userId}/chat")?.map {
            Log.d("myTest", "initSession collect $it")
            json.decodeFromString<MessagesContentDto>(it)
        }?.collect {
            updateAllAvailableChats(it)
            updateAllMessages(it)
            increaseMessagesCount(it)
        }

        override fun initChatSession() {
            MainScope().launch(Dispatchers.IO) {
                    try {
                        Log.d("myTest", "initSession try")
                        if (token.applicationToken != null) {
                            createSession()
                            customCoroutine.launch { observeAnyMessageRead() }
                            observeAllMessages()
                        } else {
                            Log.d("myTest", "initSession reconnect")
                            delay(DELAY_BEFORE_RECONNECTING)
                        }
                    }
            }*/

    }



















    /*private val quotes = listOf(
        Quote("AAPL.US", 1.5, "NASDAQ", "Apple Inc.", 150.0, 2.0),
        Quote("GAZP", -0.5, "MOEX", "Газпром", 200.0, -1.0)
    )
    // Метод для получения цитат
    fun getQuotes(): Flow<List<Quote>> {
        return flow {
            // Эмулируем задержку для иллюстрации асинхронного запроса
            kotlinx.coroutines.delay(1000)
            emit(quotes) // Emit списка цитат
        }
    }*/
}