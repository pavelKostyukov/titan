package com.example.titan7.data

import android.graphics.Bitmap

data class Listing(
    val name: String? = "", //Тикер
    val symbol: String? = "", // Изменение в процентах относительно цены закрытия предыдущей торговой сессии
    val price: Double, //Биржа последней сделки
    var change: Double, // Название бумаги
    val previousClose: Double, //Цена последней сделки
    val exchange: Double,//(Изменение цены последней сделки в пунктах относительно цены закрытия предыдущей торговой сессии)
    var logo: Bitmap? = null)//логотип
