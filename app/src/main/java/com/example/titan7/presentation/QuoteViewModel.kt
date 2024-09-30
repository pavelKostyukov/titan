package com.example.titan7.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.titan7.data.Quote
import com.example.titan7.data.QuoteRepositoryImpl
import com.example.titan7.data.WebResponse
import com.example.titan7.domain.Listing
import com.example.titan7.domain.QuoteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class QuoteViewModel(private val useCase: QuoteUseCase) : ViewModel() {
    private val _quotes = MutableStateFlow<List<Quote>>(emptyList())
    private val _logo = MutableStateFlow<List<Bitmap>>(emptyList())
    val quotes: StateFlow<List<Quote>> = _quotes
    val logo: StateFlow<List<Bitmap>> = _logo

    private val _logoUrl = mutableStateOf<String?>(null)
    val logoUrl: State<String?> = _logoUrl

 /*   fun fetchLogo(ticker: String) {

    }*/

    init {
        viewModelScope.launch {
            // Получаем URL-адреса логотипов
            useCase.getCompanyLogo().collect { bitmaps ->
                // Убедитесь, что вы получаете список Bitmap
                Log.d("QuoteViewModel", "Received Bitmaps count: ${bitmaps.size}")

                // Обновляем состояние логотипов
                _logo.value = bitmaps
            }
        }
        viewModelScope.launch {
            useCase.startSocket()
        }
        viewModelScope.launch {
            useCase.updateDate().collect{
                Log.d("ViewModel","ViewModel${it}")
                // Обновляем состояние
                _quotes.value = it
            }
        }
    }
}