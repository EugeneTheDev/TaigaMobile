package io.eugenethedev.taigamobile.ui.components.appbars

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R

@Composable
fun AppBarWithBackButton(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    navigateBack: (() -> Unit)? = null
) = TopAppBar(
    title = title,
    navigationIcon = navigateBack?.let {
        {
            IconButton(onClick = it) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    },
    actions = actions,
    backgroundColor = MaterialTheme.colors.surface,
    elevation = 0.dp,
    modifier = modifier
)
