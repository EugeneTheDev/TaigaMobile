package io.eugenethedev.taigamobile.repositories

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.eugenethedev.taigamobile.Session
import io.eugenethedev.taigamobile.dagger.DataModule
import io.eugenethedev.taigamobile.data.api.TaigaApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.mockwebserver.MockWebServer
import kotlin.test.BeforeTest
import org.junit.runner.RunWith
import kotlin.test.AfterTest

@RunWith(AndroidJUnit4::class)
abstract class BaseRepositoryTest {
    lateinit var mockServer: MockWebServer
    lateinit var mockSession: Session
    lateinit var mockTaigaApi: TaigaApi

    @OptIn(ObsoleteCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("Test thread")

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)

        val dataModule = DataModule() // contains methods for API configuration

        mockServer = MockWebServer().also { it.start() }
        mockSession = Session(ApplicationProvider.getApplicationContext()).also {
            it.server = mockServer.url("/").run { "$host:$port" }
            it.currentProjectId = 0
            it.currentProjectName = "Test"
            it.currentUserId = 0
        }
        mockTaigaApi = dataModule.provideTaigaApi(mockSession, dataModule.provideGson())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterTest
    fun cleanup() {
        mockServer.shutdown()
        Dispatchers.resetMain()
    }
}
