package com.example.titan7.domain

import com.example.titan7.data.Quote
import kotlinx.coroutines.flow.StateFlow

interface QuoteRepository {
    suspend fun startSocket()
    val updateDate: StateFlow<List<Quote>>
}