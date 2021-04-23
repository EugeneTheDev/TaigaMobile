package io.eugenethedev.taigamobile.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

/**
 * Swipeable tabs
 */

@ExperimentalPagerApi
@Composable
fun HorizontalTabbedPager(
    tabs: Array<out Tab>,
    modifier: Modifier = Modifier,
    content: @Composable PagerScope.(page: Int) -> Unit
) = Column(modifier = modifier) {
    val pagerState = rememberPagerState(pageCount = tabs.size)
    val coroutineScope = rememberCoroutineScope()

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        modifier = Modifier.fillMaxWidth(),
        contentColor = MaterialTheme.colors.primary,
        backgroundColor = MaterialTheme.colors.surface,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        }
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                },
                text = { Text(stringResource(tab.titleId)) }
            )
        }
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
