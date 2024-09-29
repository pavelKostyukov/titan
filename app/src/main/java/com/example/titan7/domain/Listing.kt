package com.example.titan7.domain

data class Listing(
    val ticker: String,
    val changePercent: Double? = 0.00,
    val exchange: String? = "",
    val name: String?= "",
    val lastPrice: Double? = 0.00,
    val changePrice: Double = 0.00
)
