package com.example.titan7.domain

import com.example.titan7.data.QuoteRepositoryImpl

class QuoteUseCase(private val repository: QuoteRepositoryImpl) {
    suspend fun startSocket() = repository.startSocket()
    fun updateDate() = repository.updateDate
}