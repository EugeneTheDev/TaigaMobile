package io.eugenethedev.taigamobile.ui.components.appbars

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.utils.clickableUnindicated

@Composable
fun ProjectAppBar(
    projectName: String,
    actions: @Composable RowScope.() -> Unit = {},
    onTitleClick: () -> Unit
) = TopAppBar(
    title = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickableUnindicated(onClick = onTitleClick)
        ) {
            Text(
                text = projectName.takeIf { it.isNotEmpty() }
                    ?: stringResource(R.string.choose_project_title),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )

            Icon(
                painter = painterResource(R.drawable.ic_arrow_down),
                contentDescription = null
            )
        }
    },
    actions = actions,
    backgroundColor = MaterialTheme.colors.surface,
    elevation = 0.dp
)