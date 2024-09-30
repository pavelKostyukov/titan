package com.example.titan7

import com.example.titan7.data.Listing
import com.example.titan7.data.QuoteRepositoryImpl
import com.google.gson.JsonParser
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class QuoteRepositoryImplTest {

    private val okHttpClient: OkHttpClient = mockk(relaxed = true)
    private val quoteRepository = QuoteRepositoryImpl(okHttpClient).apply {
        dataValue = MutableStateFlow(emptyList()) // Инициализация состояния
    }

    @Test
    fun `test startSocket subscribes to websocket`() = runBlocking {
        // Здесь можно добавить свою логику веб-сокета
        quoteRepository.startSocket()

        // Проверяем состояние, что пустой (так как ничего не добавлено)
        assertEquals(emptyList<Listing>(), quoteRepository.updateDate.value)
    }

    @Test
    fun `test handleQuoteUpdate adds new quote`() = runBlocking {
        val mockResponse = """{"name": "AAPL", "price": 150.0, "change": 1.5}""" // Пример ответа
        val dataElement = JsonParser().parse(mockResponse)

        // Вызов метода, который будет тестироваться
        quoteRepository.handleQuoteUpdate(dataElement)

        // Проверяем, что добавленная котировка есть в списке
        assertEquals(1, quoteRepository.updateDate.value.size)
        assertEquals("AAPL", quoteRepository.updateDate.value[0].name)
    }

    @Test
    fun `test handleQuoteUpdate updates existing quote`() = runBlocking {
        // Подготовка
        val initialQuoteJson = """{"name": "AAPL", "price": 150.0, "change": 1.5}""" // Исходный ответ
        val initialDataElement = JsonParser().parse(initialQuoteJson)

        // Добавляем котировку
        quoteRepository.handleQuoteUpdate(initialDataElement)

        // Обновление котировки
        val updatedQuoteJson = """{"name": "AAPL", "price": 155.0, "change": 2.0}""" // Новое значение
        val updatedDataElement = JsonParser().parse(updatedQuoteJson)

        // Обновляем котировку
        quoteRepository.handleQuoteUpdate(updatedDataElement)

        // Проверка, что цена была обновлена
        assertEquals(1, quoteRepository.updateDate.value.size)
        assertEquals("AAPL", quoteRepository.updateDate.value[0].name)
        assertEquals(155.0, quoteRepository.updateDate.value[0].price, 0.01) // Проверка на точное значение
    }
}