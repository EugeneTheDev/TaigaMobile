package io.eugenethedev.taigamobile

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class BaseAndroidTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
}
