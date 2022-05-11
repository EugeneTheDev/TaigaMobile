package io.eugenethedev.taigamobile.ui.components.texts

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Tag
import io.eugenethedev.taigamobile.ui.components.Chip
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.taigaRed
import io.eugenethedev.taigamobile.ui.utils.textColor
import io.eugenethedev.taigamobile.ui.utils.toColor

/**
 * Text with colored dots (indicators) at the end and tags
 */
@Composable
fun CommonTaskTitle(
    ref: Int,
    title: String,
    modifier: Modifier = Modifier,
    isInactive: Boolean = false,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    indicatorColorsHex: List<String> = emptyList(),
    tags: List<Tag> = emptyList(),
    isBlocked: Boolean = false
) = Column(modifier = modifier) {
    val space = 4.dp

    Text(
        text = buildAnnotatedString {
            if (isInactive) pushStyle(SpanStyle(color = MaterialTheme.colorScheme.outline, textDecoration = TextDecoration.LineThrough))
            append(stringResource(R.string.title_with_ref_pattern).format(ref, title))
            if (isInactive) pop()

            append(" ")

            indicatorColorsHex.forEach {
                pushStyle(SpanStyle(color = it.toColor()))
                append("⬤") // 2B24
                pop()
            }
        },
        color = if (isBlocked) taigaRed else textColor,
        style = MaterialTheme.typography.titleMedium
    )

    if (tags.isNotEmpty()) {
        Spacer(Modifier.height(space))

        FlowRow(
            mainAxisSpacing = space,
            crossAxisSpacing = space
        ) {
            tags.forEach {
                val bgColor = it.color.toColor()

                Chip(color = bgColor) {
                    Text(
                        text = it.name,
                        color = bgColor.textColor(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommonTaskTitlePreview() = TaigaMobileTheme {
    CommonTaskTitle(
        ref = 42,
        title = "Some title",
        tags = listOf(Tag("one", "#25A28C"), Tag("two", "#25A28C")),
        isBlocked = true
    )
}

