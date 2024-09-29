package com.example.titan7.di

import com.example.titan7.data.QuoteRepositoryImpl
import com.example.titan7.domain.QuoteRepository
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import org.koin.dsl.module

val TitanModule = module {
    single<Json> {
        Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }
    }
    single<StompClient> { StompClient(OkHttpWebSocketClient(get())) }
    single<QuoteRepository> { QuoteRepositoryImpl(/*get(), get()*/) }
}