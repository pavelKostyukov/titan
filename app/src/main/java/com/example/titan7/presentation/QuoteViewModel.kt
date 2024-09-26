package com.example.titan7.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.titan7.domain.Quote
import com.example.titan7.domain.QuoteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuoteViewModel(private val useCase: QuoteUseCase) : ViewModel() {

    private val _quotes = MutableStateFlow<List<Quote>>(emptyList())
    val quotes: StateFlow<List<Quote>> = _quotes


    init {

        viewModelScope.launch {
         /*   useCase.getQuotes().collect { quotesList ->
                _quotes.value = quotesList
            }*/
        }
    }
}