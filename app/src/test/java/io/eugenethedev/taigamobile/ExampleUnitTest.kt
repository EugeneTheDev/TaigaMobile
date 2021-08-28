package io.eugenethedev.taigamobile

import kotlin.test.Test
import kotlin.test.assertEquals


class ExampleUnitTest : BaseAndroidTest() {
    @Test
    fun `token is saved in mocked shared prefs`() {
        Session(context).let {
            it.token = "test"
            assertEquals("test", it.token)
        }
    }
}
