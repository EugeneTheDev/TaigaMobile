package io.eugenethedev.taigamobile.ui.components.badges

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.shapes
import io.eugenethedev.taigamobile.ui.utils.textColor
import io.eugenethedev.taigamobile.ui.utils.toColor

/**
 * Badge on which you can click. With cool shimmer loading animation
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClickableBadge(
    text: String,
    color: Color,
    onClick: () -> Unit = {},
    isLoading: Boolean = false,
    isClickable: Boolean = true
) {
    val textColor = color.textColor()

    val infiniteTransition = rememberInfiniteTransition()
    val animationDuration = 800

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f, // BoxWithConstraints won't work there, because maxWidth always changing when this element is part of a list
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDuration, easing = FastOutSlowInEasing),
        )
    )

    CompositionLocalProvider(
        LocalMinimumTouchTargetEnforcement provides false
    ) {
        Surface(
            shape = shapes.large,
            color = color
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(
                    indication = rememberRipple(),
                    onClick = onClick ,
                    interactionSource = remember { MutableInteractionSource() }
                ).padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = text,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 120.dp)
                )

                if (isClickable) {
                    Image(
                        painter = painterResource(R.drawable.ic_arrow_down),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(textColor),
                        modifier = Modifier.rotate(if (isLoading) rotation else 0f)
                    )
                } else {
                    Spacer(Modifier.width(6.dp))
                }
            }
        }
    }
}


@Composable
fun ClickableBadge(
    text: String,
    colorHex: String,
    onClick: () -> Unit = {},
    isLoading: Boolean = false,
    isClickable: Boolean = true
) = ClickableBadge(
    text,
    colorHex.toColor(),
    onClick,
    isLoading,
    isClickable
)

@Preview(showBackground = true)
@Composable
fun ClickableBadgePreview() = TaigaMobileTheme {
    ClickableBadge(
        text = "Sample",
        colorHex = "#25A28C",
        isLoading = true
    )
}