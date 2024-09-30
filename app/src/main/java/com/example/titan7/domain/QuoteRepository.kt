package com.example.titan7.domain

import com.example.titan7.data.Listing
import kotlinx.coroutines.flow.StateFlow

interface QuoteRepository {
    suspend fun startSocket()
    val updateDate: StateFlow<List<Listing>>
}