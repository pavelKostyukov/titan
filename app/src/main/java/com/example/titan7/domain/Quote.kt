package com.example.titan7.domain

data class Quote(
    val ticker: String,
    val changePercent: Double,
    val exchange: String,
    val name: String,
    val lastPrice: Double,
    val changePrice: Double
)
