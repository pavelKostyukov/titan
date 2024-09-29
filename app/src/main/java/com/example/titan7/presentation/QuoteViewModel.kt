package com.example.titan7.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.titan7.data.Quote
import com.example.titan7.data.QuoteRepositoryImpl
import com.example.titan7.data.WebResponse
import com.example.titan7.domain.Listing
import com.example.titan7.domain.QuoteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuoteViewModel(private val useCase: QuoteUseCase) : ViewModel() {
    private val _quotes = MutableStateFlow<List<Quote>>(emptyList())
    val quotes: StateFlow<List<Quote>> = _quotes

    init {
        viewModelScope.launch {
            useCase.startSocket()
        }
        viewModelScope.launch {
            useCase.updateDate().collect{
                Log.d("ViewModel","ViewModel${it}")
            }
        }
    }
}