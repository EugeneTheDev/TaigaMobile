package io.eugenethedev.taigamobile.ui.utils

import android.annotation.SuppressLint
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import io.eugenethedev.taigamobile.ui.commons.Result
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import timber.log.Timber

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
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        enabled = enabled,
        onClickLabel = null,
        role = null,
        onClick
    )
}

@Composable
inline fun Result<*>.subscribeOnError(onError: @Composable (message: Int) -> Unit) = takeIf { it.resultStatus == ResultStatus.Error }?.let { onError(it.message!!) }

fun safeParseHexColor(hexColor: String): Color = try {
    Color(android.graphics.Color.parseColor(hexColor))
} catch (e: IllegalArgumentException) {
    Timber.w("'$hexColor' $e")
    Color.Transparent
}
