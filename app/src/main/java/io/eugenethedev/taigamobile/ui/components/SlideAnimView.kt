package io.eugenethedev.taigamobile.ui.components

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import io.eugenethedev.taigamobile.ui.utils.onBackPressed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Custom implementation of sliding animation for navigation.
 */
@Composable
fun SlideAnimView(
    navigateBack: () -> Unit,
    animationTimeMillis: Int = 150,
    slideAnim: SlideAnim = SlideAnim.UP_DOWN,
    content: @Composable (navigateBack: () -> Unit) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val screenState = remember { MutableTransitionState(ScreenState.EXITED) }

    // execute only once when screen created for the first time
    remember {
        screenState.targetState = ScreenState.ENTERED
        null
    }

    // sliding animation
    val currentOffset by updateTransition(screenState).animateFloat(transitionSpec = { tween(animationTimeMillis) }) {
        when(it) {
            ScreenState.EXITED -> 1.0f
            ScreenState.ENTERED -> 0.0f
        }
    }

    // navigate function
    fun navigate() {
        screenState.targetState = ScreenState.EXITED
        coroutineScope.launch {
            delay(animationTimeMillis.toLong())
            navigateBack()
        }
    }

    // navigate back if back button pressed
    onBackPressed(::navigate)

    ConstraintLayout(Modifier.fillMaxSize()) {
        val contentLayout = createRef()
        val topGuideline = createGuidelineFromTop(currentOffset)
        val bottomGuideline = createGuidelineFromBottom(currentOffset)
        val startGuideline = createGuidelineFromStart(currentOffset)
        val endGuideline = createGuidelineFromEnd(currentOffset)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(contentLayout) {
                    when (slideAnim) {
                        SlideAnim.UP_DOWN -> top.linkTo(topGuideline)
                        SlideAnim.DOWN_UP -> bottom.linkTo(bottomGuideline)
                        SlideAnim.START_END -> start.linkTo(startGuideline)
                        SlideAnim.END_START -> end.linkTo(endGuideline)
                    }
                }
        ) {
            content(::navigate)
        }
    }
}

/**
 * Supported directions for animation
 */
enum class SlideAnim {
    UP_DOWN,
    DOWN_UP,
    START_END,
    END_START
}

private enum class ScreenState {
    ENTERED,
    EXITED
}