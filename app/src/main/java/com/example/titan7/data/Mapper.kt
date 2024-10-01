package com.example.titan7.data


fun WebResponse.mapToListing() =
    Listing(
        name = this.c,              // Название бумаги
        symbol = this.name,         // Убираем "UNKNOWN_SYMBOL", просто оставляем пустую строку
        price = this.chg,           // Изменение в процентах относительно цены закрытия
        change = this.ltp,          // Цена последней сделки
        previousClose = this.p22,   // Цена последней сделки
        exchange = this.pcp         // Изменение цены последней сделки в пунктах
    )