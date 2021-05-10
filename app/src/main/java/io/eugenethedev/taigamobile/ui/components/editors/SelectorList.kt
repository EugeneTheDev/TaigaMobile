package io.eugenethedev.taigamobile.ui.components.editors

import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsHeight
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithBackButton
import io.eugenethedev.taigamobile.ui.components.loaders.DotsLoader
import io.eugenethedev.taigamobile.ui.utils.onBackPressed

/**
 * Selector list, which expands from bottom to top.
 * Could be used to search and select something
 */
@ExperimentalAnimationApi
@Composable
fun <T> SelectorList(
    @StringRes titleHintId: Int,
    items: List<T>,
    isVisible: Boolean = false,
    isLoading: Boolean = false,
    isSearchable: Boolean = true,
    loadData: (String) -> Unit = {},
    navigateBack: () -> Unit = {},
    animationDurationMillis: Int = SelectorListConstants.defaultAnimDurationMillis,
    itemContent: @Composable (T) -> Unit
) = AnimatedVisibility(
    visible = isVisible,
    enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(animationDurationMillis)),
    exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(animationDurationMillis)),
    initiallyVisible = false // always show animation when becoming visible
) {
    var query by remember { mutableStateOf(TextFieldValue()) }

    onBackPressed(navigateBack)

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AppBarWithBackButton(
            title = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (isSearchable) {
                        TextFieldWithHint(
                            hintId = titleHintId,
                            value = query,
                            onValueChange = { query = it },
                            singleLine = true,
                            onSearchClick = { loadData(query.text) }
                        )
                    } else {
                        Text(stringResource(titleHintId))
                    }
                }
            },
            navigateBack = navigateBack
        )

        LazyColumn {
            itemsIndexed(items) { index, item ->
                itemContent(item)

                if (index < items.size - 1) {
                    Divider(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.LightGray
                    )
                }

                if (index == items.lastIndex) {
                    LaunchedEffect(items.size) {
                        loadData(query.text)
                    }
                }
            }

            item {
                if (isLoading) {
                    DotsLoader()
                }
                Spacer(Modifier.navigationBarsHeight(8.dp))
            }
        }
    }
}

object SelectorListConstants {
    const val defaultAnimDurationMillis = 200
}