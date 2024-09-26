package com.example.titan7.domain

import com.example.titan7.data.QuoteRepository

class QuoteUseCase(private val repository: QuoteRepository) {
    fun getQuotes() = repository.getQuotes()
}