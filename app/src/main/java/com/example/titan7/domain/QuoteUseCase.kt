package com.example.titan7.domain


class QuoteUseCase(private val repository: QuoteRepository) {
    suspend fun startSocket() = repository.startSocket()
    fun updateDate() = repository.updateDate
}