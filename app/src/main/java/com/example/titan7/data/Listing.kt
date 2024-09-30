package com.example.titan7.data

import android.graphics.Bitmap

data class Listing(
    val name: String? = "",
    val symbol: String? = "",
    val price: Double,
    val change: Double,
    val previousClose: Double,
    val exchange: String? = "",
    var logo: Bitmap? = null
)