package io.eugenethedev.taigamobile.ui.utils

import android.annotation.SuppressLint
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.AmbientContext

/**
 * Utility function to handle press on back button
 */
@SuppressLint("ComposableNaming")
@Composable
fun onBackPressed(action: () -> Unit) {
    (AmbientContext.current as? OnBackPressedDispatcherOwner)?.onBackPressedDispatcher?.let {
        remember {
            it.addCallback(
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        action()
                        remove()
                    }
                }
            )
            null
        }
    }
}

fun Modifier.clickableUnindicated(
    enabled: Boolean = true,
    onLongClickLabel: String? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onClick: () -> Unit
) = composed {
    Modifier.clickable(
        enabled = enabled,
        interactionState = remember { InteractionState() },
        indication = null,
        onClickLabel = null,
        role = null,
        onLongClickLabel,
        onLongClick,
        onDoubleClick,
        onClick
    )
}

@Composable
inline fun Result<*>.subscribeOnError(onError: @Composable (message: Int) -> Unit) = takeIf { it.resultStatus == ResultStatus.ERROR }?.let { onError(it.message!!) }
