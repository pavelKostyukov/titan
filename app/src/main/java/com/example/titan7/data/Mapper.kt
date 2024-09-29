package com.example.titan7.data

fun WebResponse.mapToListing() =
    Quote(
        name = this.c,
        symbol = this.quoteBasis,          // Символ
        price = this.chg,            // Текущая цена
        change = this.ltp,          // Изменение цены
        previousClose = this.p22, // Цена закрытия
        exchange = this.bbt        // Биржа
    )