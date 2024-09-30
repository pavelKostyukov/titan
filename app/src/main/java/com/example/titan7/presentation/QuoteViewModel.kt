package com.example.titan7.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.titan7.data.Listing
import com.example.titan7.domain.QuoteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuoteViewModel(private val useCase: QuoteUseCase) : ViewModel() {
    private val _quotes = MutableStateFlow<List<Listing>>(emptyList())
    val quotes: StateFlow<List<Listing>> = _quotes

    init {
        viewModelScope.launch {
            useCase.startSocket()
        }
        viewModelScope.launch {
            useCase.updateDate().collect {
                Log.d("ViewModel", "ViewModel${it}")
                // Обновляем состояние
                _quotes.value = it
            }
        }
    }
}