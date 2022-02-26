package io.eugenethedev.taigamobile.ui.components.editors

import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed as itemsIndexedLazy
import com.google.accompanist.insets.navigationBarsHeight
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithBackButton
import io.eugenethedev.taigamobile.ui.components.loaders.DotsLoader
import io.eugenethedev.taigamobile.ui.utils.onBackPressed

/**
 * Selector list, which expands from bottom to top.
 * Could be used to search and select something
 */
@Composable
fun <T : Any> SelectorList(
    @StringRes titleHintId: Int,
    items: List<T> = emptyList(),
    itemsLazy: LazyPagingItems<T>? = null,
    key: ((index: Int, item: T) -> Any)? = null, // used to preserve position with lazy items
    isVisible: Boolean = false,
    isItemsLoading: Boolean = false,
    isSearchable: Boolean = true,
    searchData: (String) -> Unit = {},
    navigateBack: () -> Unit = {},
    animationDurationMillis: Int = SelectorListConstants.defaultAnimDurationMillis,
    itemContent: @Composable (T) -> Unit
) = AnimatedVisibility(
    visibleState = remember { MutableTransitionState(false) }
        .apply { targetState = isVisible },
    enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(animationDurationMillis)),
    exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(animationDurationMillis))
) {
    var query by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }

    onBackPressed(navigateBack)

    val isLoading = itemsLazy
        ?.run { loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading }
        ?: isItemsLoading

    val lastIndex = itemsLazy?.itemCount?.minus(1) ?: items.lastIndex

    val listItemContent: @Composable LazyItemScope.(Int, T?) -> Unit = lambda@ { index, item ->
        if (item == null) return@lambda

        itemContent(item)

        if (index < lastIndex) {
            Divider(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.outline
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
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
                            onSearchClick = { searchData(query.text) }
                        )
                    } else {
                        Text(stringResource(titleHintId))
                    }
                }
            },
            navigateBack = navigateBack
        )

        LazyColumn {
            itemsLazy?.let {
                itemsIndexedLazy(
                    items = it,
                    key = key,
                    itemContent = listItemContent
                )
            } ?: itemsIndexed(items, itemContent = listItemContent)

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
