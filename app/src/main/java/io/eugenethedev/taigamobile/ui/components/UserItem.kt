package io.eugenethedev.taigamobile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.glide.GlideImage
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import java.text.SimpleDateFormat
import java.util.*

/**
 * User info (name and avatar).
 */
@Composable
fun UserItem(
    user: User,
    dateTime: Date? = null
) = Row(verticalAlignment = Alignment.CenterVertically) {
    val dateTimeFormatter = remember { SimpleDateFormat.getDateTimeInstance() }
    val imageSize = if (dateTime != null) 46.dp else 40.dp

    GlideImage(
        data = user.avatarUrl ?: R.drawable.default_avatar,
        contentDescription = null,
        fadeIn = true,
        requestBuilder = { error(R.drawable.default_avatar) },
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(imageSize).clip(CircleShape)
    )

    Spacer(Modifier.width(6.dp))

    Column {
        Text(
            text = user.displayName,
            style = MaterialTheme.typography.subtitle1
        )

        dateTime?.let {
            Text(
                text = dateTimeFormatter.format(it),
                color = Color.Gray,
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserItemPreview() = TaigaMobileTheme {
    UserItem(
        user = User(
            _id = 0L,
            fullName = "Full Name",
            photo = null,
            bigPhoto = null,
            username = "username"
        )
    )
}