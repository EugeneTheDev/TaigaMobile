package io.eugenethedev.taigamobile.ui.components.badges

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.utils.clickableUnindicated
import io.eugenethedev.taigamobile.ui.utils.textColor
import io.eugenethedev.taigamobile.ui.utils.toColor

/**
 * Badge on which you can click. With cool shimmer loading animation
 */

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

    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 300f, // BoxWithConstraints won't work there, because maxWidth always changing when this element is part of a list
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDuration, easing = LinearEasing),
        )
    )
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDuration, easing = LinearEasing),
        )
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color.Transparent,
            Color.LightGray.copy(alpha = 0.9f),
            Color.Transparent
        ),
        start = Offset(offsetX, offsetY),
        end = Offset(offsetX + 50, offsetY + 50)
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = color,
                shape = MaterialTheme.shapes.medium
            )
            .let {
                if (isLoading) {
                    it.background(
                        brush = brush,
                        shape = MaterialTheme.shapes.medium
                    )
                } else {
                    it
                }
            }
            .padding(start = 6.dp)
            .padding(vertical = 2.dp)
            .clickableUnindicated(enabled = isClickable, onClick = onClick)
    ) {
        Text(
            text = text,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            // I really want to use Modifier.weight(0.8, fill = false) here and below,
            // but its a peace of shit and fill = false modifier simply doesn't work at all
            // (this Row takes all available width anyways, which is of course bad for this case).
            // Maybe this will be fixed in the future, but for now I'll leave it like this...
            modifier = Modifier.widthIn(max = 120.dp)
        )

        if (isClickable) {
            Image(
                painter = painterResource(R.drawable.ic_arrow_down),
                contentDescription = null,
                colorFilter = ColorFilter.tint(textColor)
            )
        } else {
            Spacer(Modifier.width(6.dp))
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