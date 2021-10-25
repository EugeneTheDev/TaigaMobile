package io.eugenethedev.taigamobile.ui.components.badges

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.taigaDarkGray
import io.eugenethedev.taigamobile.ui.utils.textColor

@Composable
fun Badge(
    text: String,
    isActive: Boolean = true
) {
    val color = if (isActive) MaterialTheme.colors.primary else taigaDarkGray
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color,
        contentColor = color.textColor(),
        elevation = 4.dp
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(horizontal = 2.dp),
        )
    }
}

@Preview
@Composable
fun BadgePreview() = TaigaMobileTheme {
    Row(modifier = Modifier.padding(10.dp)) {
        Badge("1", isActive = false)
        Spacer(Modifier.width(4.dp))
        Badge("12", isActive = true)
    }
}