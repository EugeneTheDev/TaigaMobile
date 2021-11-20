package io.eugenethedev.taigamobile.viewmodels

import io.eugenethedev.taigamobile.BaseUnitTest
import io.eugenethedev.taigamobile.dagger.AppComponent
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.AfterClass
import kotlin.test.AfterTest

abstract class BaseViewModelTest : BaseUnitTest() {
    protected val mockAppComponent = mockk<AppComponent>(relaxed = true)

    @AfterTest
    fun resetMocks() {
        clearAllMocks()
    }

    companion object {
        @AfterClass
        @JvmStatic
        fun unmock() {
            unmockkAll()
        }
    }
}