package com.example.titan7.data

fun WebResponse.mapToListing() =
    Listing(
        name = this.c,
        symbol = this.quoteBasis?: "UNKNOWN_SYMBOL" ,          // Символ
        price = this.chg,            // Текущая цена
        change = this.ltp,          // Изменение цены
        previousClose = this.p22, // Цена закрытия
        exchange = this.bbt        // Биржа
    )