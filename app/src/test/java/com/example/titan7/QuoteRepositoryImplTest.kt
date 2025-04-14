
package com.example.titan7

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.titan7.data.QuoteRepositoryImpl
import com.google.gson.JsonParser
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.json.JsonArray
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(Enclosed::class)
class QuoteRepositoryImplTest {

    class WebSocketTests {
        private lateinit var mockWebServer: MockWebServer
        private lateinit var okHttpClient: OkHttpClient
        private lateinit var quoteRepository: QuoteRepositoryImpl

        @get:Rule
        val coroutineRule = CoroutineTestRule()

        @Before
        fun setup() {
            mockWebServer = MockWebServer()
            mockWebServer.start()
            okHttpClient = OkHttpClient.Builder().build()
            quoteRepository = QuoteRepositoryImpl(
                okHttpClient = okHttpClient,
                context = ApplicationProvider.getApplicationContext()
            )
        }

        @After
        fun teardown() {
            mockWebServer.shutdown()
        }

        @Test
        fun `startSocket should connect to websocket`(): Unit = coroutineRule.runBlockingTest {
            quoteRepository.startSocket()

            // Assert
            val recordedRequest = mockWebServer.takeRequest()
            assertEquals("websocket", recordedRequest.headers["Upgrade"])
            assertEquals("GET", recordedRequest.method)
        }

        @Test
        fun `handle valid quote update should update state`(): Unit = coroutineRule.runBlockingTest {
            // Arrange
            val json = JsonParser.parseString("""["q", {"ticker": "AAPL.US", "price": 150.0}]""") as JsonArray

            // Act
            quoteRepository.processWebSocketMessage(json.toString())

            // Assert
            val listings = quoteRepository.updateDate.value
            assertEquals(1, listings.size)
            assertEquals("AAPL.US", listings[0].name)
            assertEquals(150.0, listings[0].price, 0.01)
        }
    }

    class UnitTests {
        private val okHttpClient: OkHttpClient = mockk(relaxed = true)
        private val context: Context = mockk(relaxed = true)
        private lateinit var quoteRepository: QuoteRepositoryImpl

        @Before
        fun setup() {
            every { context.resources.getStringArray(any()) } returns arrayOf("AAPL.US", "GOOGL.US")
            quoteRepository = QuoteRepositoryImpl(okHttpClient, context)
        }

        @Test
        fun `getCompanyLogo should return null for invalid response`() = runBlocking {
            // Arrange
            val mockResponse: Response = mockk {
                every { isSuccessful } returns false
                every { body } returns null
            }
            every { okHttpClient.newCall(any()).execute() } returns mockResponse

            // Act
            val result = quoteRepository.getCompanyLogo("INVALID")

            // Assert
            assertNull(result)
        }

        @Test
        fun `updateOrAddQuote should add new quote`() {
            // Arrange
           // val newQuote = Listing(name = "TEST", price = 100.00)

            // Act
          //  quoteRepository.updateOrAddQuote(newQuote)

            // Assert
            assertEquals(1, quoteRepository.updateDate.value.size)
        }
    }

    class CoroutineTestRule : TestWatcher() {
        private val testDispatcher = StandardTestDispatcher()

        @OptIn(ExperimentalCoroutinesApi::class)
        override fun starting(description: Description) {
            Dispatchers.setMain(testDispatcher)
        }

        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }

        fun runBlockingTest(block: suspend TestScope.() -> Unit) =
            runTest(testDispatcher) { block() }
    }
}
