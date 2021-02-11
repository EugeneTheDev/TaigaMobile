package io.eugenethedev.taigamobile.ui.utils

import android.annotation.SuppressLint
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AmbientContext

/**
 * Utility function to handle press on back button
 */
@SuppressLint("ComposableNaming")
@Composable
fun onBackPressed(action: () -> Unit) {
    (AmbientContext.current as OnBackPressedDispatcherOwner).onBackPressedDispatcher.addCallback(
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                action()
            }
        }
    )
}
