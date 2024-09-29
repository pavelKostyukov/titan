package com.example.titan7.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.titan7.presentation.QuoteViewModel

class QuoteViewModelFactory(private val useCase: QuoteUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuoteViewModel(useCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}