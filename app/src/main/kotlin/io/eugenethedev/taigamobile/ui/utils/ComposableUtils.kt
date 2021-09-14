package io.eugenethedev.taigamobile.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import io.eugenethedev.taigamobile.ui.commons.ErrorResult
import io.eugenethedev.taigamobile.ui.commons.Result
import timber.log.Timber

/**
 * Utility function to handle press on back button
 */
@SuppressLint("ComposableNaming")
@Composable
fun onBackPressed(action: () -> Unit) {
    LocalContext
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
inline fun Result<*>.subscribeOnError(onError: @Composable (message: Int) -> Unit) = (this as? ErrorResult)?.message?.let { onError(it) }

val Context.activity: AppCompatActivity get() = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.activity
    else -> throw IllegalStateException("Context is not an Activity")
}

// Color functions

fun String.toColor(): Color = try {
    Color(android.graphics.Color.parseColor(this))
} catch (e: Exception) {
    Timber.w("'$this' $e")
    Color.Transparent
}

fun Color.toHex() = "#%08X".format(toArgb()).replace("#FF", "#")
