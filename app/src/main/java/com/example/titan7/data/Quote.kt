package com.example.titan7.data

data class Quote(
    val symbol: String,
    val price: Double,
    val change: Double,
    val previousClose: Double,
    val exchange: String
)