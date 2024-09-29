package com.example.titan7.di

import com.example.titan7.data.QuoteRepositoryImpl
import com.example.titan7.domain.QuoteRepository
import com.example.titan7.domain.QuoteUseCase
import com.example.titan7.presentation.QuoteViewModel
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val TitanModule = module {
    single<OkHttpClient> {
        OkHttpClient.Builder()
            .readTimeout(2000, TimeUnit.MILLISECONDS)
            .build()
    }
    single<Json> {
        Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }
    }
    single<QuoteRepository> { QuoteRepositoryImpl(get()) }
    viewModelOf(::QuoteViewModel)

    factory { QuoteUseCase(get()) }
}