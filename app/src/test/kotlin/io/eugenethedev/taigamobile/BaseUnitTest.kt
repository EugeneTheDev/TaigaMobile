package io.eugenethedev.taigamobile

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.AfterClass
import org.junit.BeforeClass

abstract class BaseUnitTest {
    companion object {
        @OptIn(ExperimentalCoroutinesApi::class)
        @JvmStatic
        @BeforeClass
        fun configureTestDispatcher() {
            Dispatchers.setMain(Dispatchers.Unconfined)
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        @JvmStatic
        @AfterClass
        fun resetDispatcher() {
            Dispatchers.resetMain()
        }
    }
}