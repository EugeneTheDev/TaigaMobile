package io.eugenethedev.taigamobile.ui.components.containers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HorizontalTabbedPager(
    tabs: Array<out Tab>,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState(pageCount = tabs.size),
    scrollable: Boolean = true,
    edgePadding: Dp = mainHorizontalScreenPadding,
    content: @Composable PagerScope.(page: Int) -> Unit
) = Column(modifier = modifier) {
    val coroutineScope = rememberCoroutineScope()

    val indicator: @Composable (tabPositions: List<TabPosition>) -> Unit = { tabPositions ->
        TabRowDefaults.Indicator(
            Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
        )
    }

    val tabsRow: @Composable () -> Unit = {
        tabs.forEachIndexed { index, tab ->
            val selected = pagerState.run { targetPage.takeIf { it != currentPage } ?: currentPage == index }
            Tab(
                selected = selected,
                onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                },
                text = {
                    Text(
                        text = stringResource(tab.titleId),
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        }
    }

    if (scrollable) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            contentColor = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.surface,
            indicator = indicator,
            tabs = tabsRow,
            divider = {},
            edgePadding = edgePadding
        )
    } else {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            contentColor = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.surface,
            indicator = indicator,
            tabs = tabsRow,
            divider = {}
        )
    }

    Spacer(Modifier.height(8.dp))

    HorizontalPager(
        state = pagerState,
        content = content
    )
}

interface Tab {
    val titleId: Int
}
