package io.eugenethedev.taigamobile.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import io.eugenethedev.taigamobile.R
import timber.log.Timber
import kotlin.math.ln

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


@SuppressLint("UnnecessaryComposedModifier")
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


// Error functions
@Composable
inline fun Result<*>.subscribeOnError(onError: (message: Int) -> Unit) = (this as? ErrorResult)?.message?.let { onError(it) }

@SuppressLint("ComposableNaming")
@Composable
inline fun <T : Any> LazyPagingItems<T>.subscribeOnError(onError: (message: Int) -> Unit) {
    if (loadState.run { listOf(refresh, prepend, append) }.any { it is LoadState.Error }) {
        onError(R.string.common_error_message)
    }
}


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
// calculate optimal text color for colored background background
fun Color.textColor() = if (luminance() < 0.5) Color.White else Color.Black
// copy from library, because it is internal in library
fun ColorScheme.surfaceColorAtElevation(elevation: Dp, ): Color {
    if (elevation == 0.dp) return surface
    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    return primary.copy(alpha = alpha).compositeOver(surface)
}
