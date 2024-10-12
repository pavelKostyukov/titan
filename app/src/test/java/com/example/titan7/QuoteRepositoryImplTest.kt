package com.example.titan7

import com.example.titan7.data.Listing
import com.example.titan7.data.QuoteRepositoryImpl
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(Enclosed::class)
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

    class QuoteRepositoryImplTest {

        private lateinit var mockWebServer: MockWebServer
        private lateinit var quoteRepository: QuoteRepositoryImpl
        private lateinit var okHttpClient: OkHttpClient

        @Before
        fun setUp() {
            mockWebServer = MockWebServer()
            mockWebServer.start()
            okHttpClient = OkHttpClient.Builder().build()
            quoteRepository = QuoteRepositoryImpl(okHttpClient)
        }

        @After
        fun tearDown() {
            mockWebServer.shutdown()
        }

        @Test
        fun testWebSocketConnection() = runBlocking {
            // This would be expanded with actual testing for WebSocket connection.
            quoteRepository.startSocket()
            // Add assertions or verifications for connection success.
        }

        @Test
        fun testHandleQuoteUpdate() = runBlocking {
            // Simulate a quote update message.
            val sampleQuoteJson = """["q", {"name": "AAPL.US", "price": 150.0}]"""
            val requestBody = JsonPrimitive(sampleQuoteJson)

            // Mock the response when dealing with quote updates
            quoteRepository.handleQuoteUpdate(requestBody)

            // Assert the expected changes in dataValue.
            val expectedListings = quoteRepository.updateDate.value
            assertEquals(1, expectedListings.size)
            assertEquals("AAPL.US", expectedListings[0].name)
            assertEquals(150.0, expectedListings[0].price, 0.01)
        }

        @Test
        fun testGetCompanyLogo() = runBlocking {
            // Assuming the logo URL is functional and returns a valid Bitmap.
            val logoBitmap = quoteRepository.getCompanyLogo("AAPL")
            assertNotNull(logoBitmap)
        }
    }
}