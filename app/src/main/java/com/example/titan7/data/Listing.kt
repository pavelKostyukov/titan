package com.example.titan7.data

import android.graphics.Bitmap
import android.icu.text.DecimalFormat

data class Listing(
    val name: String? = "", //Тикер
    val symbol: String? = "", // Изменение в процентах относительно цены закрытия предыдущей торговой сессии
    val price: Double, //Биржа последней сделки
    var change: Double, // Название бумаги
    val previousClose: Double, //Цена последней сделки
    val exchange: Double,//(Изменение цены последней сделки в пунктах относительно цены закрытия предыдущей торговой сессии)
    var logo: Bitmap? = null)//логотип
{
    val hasLogo: Boolean
        get() = logo != null

    val formattedPrice: String
        get() = DecimalFormat("#,###.00").format(price)

    val formattedChange: String
        get() = DecimalFormat("#,###.00").format(change)
}