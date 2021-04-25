package io.eugenethedev.taigamobile.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import kotlinx.coroutines.launch

/**
 * Swipeable tabs
 */

@ExperimentalPagerApi
@Composable
fun HorizontalTabbedPager(
    tabs: Array<out Tab>,
    modifier: Modifier = Modifier,
    scrollable: Boolean = true,
    offscreenLimit: Int = 10, // keep screens loaded
    edgePadding: Dp = mainHorizontalScreenPadding,
    content: @Composable PagerScope.(page: Int) -> Unit
) = Column(modifier = modifier) {
    val pagerState = rememberPagerState(pageCount = tabs.size)
    val coroutineScope = rememberCoroutineScope()

    val indicator: @Composable (tabPositions: List<TabPosition>) -> Unit = { tabPositions ->
        TabRowDefaults.Indicator(
            Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
        )
    }

    val tabsRow: @Composable () -> Unit = {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = pagerState.run { targetPage?.takeIf { it != currentPage } ?: currentPage == index },
                onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                },
                text = { Text(stringResource(tab.titleId)) },
                unselectedContentColor = Color.Gray
            )
        }
    }

    if (scrollable) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            contentColor = MaterialTheme.colors.primary,
            backgroundColor = MaterialTheme.colors.surface,
            indicator = indicator,
            tabs = tabsRow,
            divider = {},
            edgePadding = edgePadding
        )
    } else {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            contentColor = MaterialTheme.colors.primary,
            backgroundColor = MaterialTheme.colors.surface,
            indicator = indicator,
            tabs = tabsRow,
            divider = {}
        )
    }

    Spacer(Modifier.height(8.dp))

    HorizontalPager(
        state = pagerState,
        offscreenLimit = offscreenLimit,
        content = content
    )
}

interface Tab {
    val titleId: Int
}
