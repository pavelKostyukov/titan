package com.example.titan7.presentation

class QuoteViewModel(private val useCase: QuoteUseCase) : ViewModel() {
    private val _quotes = MutableStateFlow<List<Quote>>(emptyList())
    val quotes: StateFlow<List<Quote>> = _quotes

    init {
        viewModelScope.launch {
            useCase.getQuotes().collect { quotesList ->
                _quotes.value = quotesList
            }
        }
    }
}