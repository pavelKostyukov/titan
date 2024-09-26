package com.example.titan7.data

import com.example.titan7.domain.Quote
import kotlinx.coroutines.flow.Flow

class QuoteRepository {
    fun getQuotes(): Flow<List<Quote>>
}