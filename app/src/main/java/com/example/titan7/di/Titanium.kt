package com.example.titan7.di

import com.example.titan7.data.QuoteRepositoryImpl
import com.example.titan7.domain.QuoteRepository
import com.example.titan7.domain.QuoteUseCase
import com.example.titan7.presentation.QuoteViewModel
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val TitanModule = module {

    single { OkHttpClient.Builder().build() }

    single<QuoteRepository> { QuoteRepositoryImpl(get(),get()) }

    viewModel { QuoteViewModel(get()) }

    single<QuoteRepository> {
        QuoteRepositoryImpl(
            okHttpClient = get(),
            context = get()
        )
    }
    single { QuoteUseCase(get()) }
    viewModel { QuoteViewModel(get()) }
}