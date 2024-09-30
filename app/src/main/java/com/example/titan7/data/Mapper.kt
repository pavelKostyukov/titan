package com.example.titan7.data


fun WebResponse.mapToListing() =
    Listing(
        name = this.c,              // Название бумаги
        symbol = this.name?: "UNKNOWN_SYMBOL" ,//Изменение в процентах относительно цены закрытия
        price = this.chg,           //Биржа последней сделки
        change = this.ltp,         //Цена последней сделки
        previousClose = this.p22,  //Цена последней сделки
        exchange = this.pcp        //(Изменение цены последней сделки в пунктах относи
    )