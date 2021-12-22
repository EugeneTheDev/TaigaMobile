package io.eugenethedev.taigamobile.ui.components.appbars

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import io.eugenethedev.taigamobile.R

@Composable
fun AppBarWithBackButton(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    navigateBack: (() -> Unit)? = null
) = SmallTopAppBar(
    title = title,
    navigationIcon = navigateBack?.let {
        {
            IconButton(onClick = it) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    } ?: {},
    actions = actions,
    modifier = modifier.statusBarsPadding()
)
