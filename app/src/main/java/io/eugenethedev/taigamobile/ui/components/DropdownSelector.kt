package io.eugenethedev.taigamobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme

/**
 * Implements dropdown menu (aka Spinner in classic android)
 */

// for now it is only badge, but I'll improve it in further commits
@Composable
fun DropdownSelector(
    text: String,
    color: Color
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.background(
            color = color,
            shape = MaterialTheme.shapes.medium
        )
        .padding(start = 6.dp)
        .padding(vertical = 2.dp)
) {
    Text(
        text = text,
        color = Color.White
    )

    Image(
        painter = painterResource(R.drawable.ic_arrow_down),
        contentDescription = null,
        colorFilter = ColorFilter.tint(Color.White)
    )

}

@Composable
fun DropdownSelector(
    text: String,
    colorHex: String
) = DropdownSelector(
    text,
    Color(android.graphics.Color.parseColor(colorHex))
)

@Preview(showBackground = true)
@Composable
fun DropdownSelectorPreview() = TaigaMobileTheme {
    DropdownSelector(
        text = "Sample",
        colorHex = "#25A28C"
    )
}