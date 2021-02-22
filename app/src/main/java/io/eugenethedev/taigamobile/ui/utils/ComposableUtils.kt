package io.eugenethedev.taigamobile.ui.utils

import android.annotation.SuppressLint
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext

/**
 * Utility function to handle press on back button
 */
@SuppressLint("ComposableNaming")
@Composable
fun onBackPressed(action: () -> Unit) {
    (LocalContext.current as? OnBackPressedDispatcherOwner)?.onBackPressedDispatcher?.let { dispatcher ->
        val callback = remember {
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    action()
                    remove()
                }
            }.also {
                dispatcher.addCallback(it)
            }
        }

        DisposableEffect(Unit) {
            onDispose(callback::remove)
        }
    }
}

fun Modifier.clickableUnindicated(
    enabled: Boolean = true,
    onClick: () -> Unit
) = composed {
    Modifier.clickable(
        interactionState = remember { InteractionState() },
        indication = null,
        enabled = enabled,
        onClickLabel = null,
        role = null,
        onClick
    )
}

@Composable
inline fun Result<*>.subscribeOnError(onError: @Composable (message: Int) -> Unit) = takeIf { it.resultStatus == ResultStatus.ERROR }?.let { onError(it.message!!) }
