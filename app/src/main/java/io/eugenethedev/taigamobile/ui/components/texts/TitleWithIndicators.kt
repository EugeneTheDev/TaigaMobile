package io.eugenethedev.taigamobile.ui.components.texts

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.utils.safeParseHexColor
import timber.log.Timber

/**
 * Text with colored dots (indicators) at the end
 */
@Composable
fun TitleWithIndicators(
    ref: Int,
    title: String,
    modifier: Modifier = Modifier,
    isInactive: Boolean = false,
    textColor: Color = MaterialTheme.colors.onSurface,
    indicatorColorsHex: List<String> = emptyList(),
) = Text(
    text = buildAnnotatedString {
        if (isInactive) pushStyle(SpanStyle(color = Color.Gray, textDecoration = TextDecoration.LineThrough))
        append(stringResource(R.string.title_with_ref_pattern).format(ref, title))
        if (isInactive) pop()

        append(" ")

        indicatorColorsHex.forEach {
            pushStyle(SpanStyle(color = safeParseHexColor(it)))
            append("⬤") // 2B24
            pop()
        }
    },
    color = textColor,
    style = MaterialTheme.typography.subtitle1,
    modifier = modifier
)
