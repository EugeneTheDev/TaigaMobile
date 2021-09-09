package io.eugenethedev.taigamobile.repositories

import io.eugenethedev.taigamobile.manager.TaigaTestInstanceManager
import kotlin.test.Test

class DummyTest {
    @Test
    fun test() {
        TaigaTestInstanceManager().apply {
            clear()
        }
    }
}
