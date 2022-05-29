package io.eugenethedev.taigamobile.ui.screens.wiki.selector

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.components.appbars.ClickableAppBar
import io.eugenethedev.taigamobile.ui.components.buttons.PlusButton
import io.eugenethedev.taigamobile.ui.components.containers.ContainerBox
import io.eugenethedev.taigamobile.ui.components.containers.HorizontalTabbedPager
import io.eugenethedev.taigamobile.ui.components.containers.Tab
import io.eugenethedev.taigamobile.ui.components.dialogs.EmptyWikiDialog
import io.eugenethedev.taigamobile.ui.components.loaders.CircularLoader
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.utils.LoadingResult
import io.eugenethedev.taigamobile.ui.utils.navigateToWikiPageScreen
import io.eugenethedev.taigamobile.ui.utils.subscribeOnError

@Composable
fun WikiSelectorScreen(
    navController: NavController,
    showMessage: (message: Int) -> Unit = {},
) {
    val viewModel: WikiSelectorViewModel = viewModel()

    val projectName by viewModel.projectName.collectAsState()

    val wikiLinks by viewModel.wikiLinksResult.collectAsState()
    wikiLinks.subscribeOnError(showMessage)

    val wikiPages by viewModel.wikiPagesResult.collectAsState()
    wikiPages.subscribeOnError(showMessage)

    LaunchedEffect(Unit) {
        viewModel.getWikiPage()
        viewModel.getWikiLinks()
    }

    if (wikiLinks is LoadingResult || wikiPages is LoadingResult) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularLoader()
        }
    } else {
        val wikiPagesSlug = wikiPages.data.orEmpty().map { it.slug }
        val wikiLinksSlug = wikiLinks.data.orEmpty().filter { it.ref !in wikiPagesSlug }.map { it.ref }

        WikiPageSelectorContent(
            projectName = projectName,
            bookmarksTitles = wikiLinks.data.orEmpty().map { it.title },
            allPagesSlug = wikiPagesSlug + wikiLinksSlug,
            onTitleClick = { navController.navigate(Routes.projectsSelector) },
            navigateToCreatePage = {
                navController.navigate(Routes.wiki_create_page)
            },
            navigateToPageByTitle = { title ->
                wikiLinks.data.orEmpty().find { it.title == title }?.ref?.let {
                    navController.navigateToWikiPageScreen(it)
                }
            },
            navigateToPageBySlug = { slug ->
                navController.navigateToWikiPageScreen(slug)
            },
            navigateBack = { navController.popBackStack() }
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun WikiPageSelectorContent(
    projectName: String,
    bookmarksTitles: List<String> = emptyList(),
    allPagesSlug: List<String> = emptyList(),
    onTitleClick: () -> Unit = {},
    navigateToCreatePage: () -> Unit = {},
    navigateToPageByTitle: (title: String) -> Unit = {},
    navigateToPageBySlug: (slug: String) -> Unit = {},
    navigateBack: () -> Unit = {}
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    ClickableAppBar(
        projectName = projectName,
        actions = {
            PlusButton(
                onClick = navigateToCreatePage
            )
        },
        onTitleClick = onTitleClick,
        navigateBack = navigateBack
    )

    if (bookmarksTitles.isEmpty() && allPagesSlug.isEmpty()) {
        EmptyWikiDialog(
            createNewPage = navigateToCreatePage
        )
    }

    HorizontalTabbedPager(
        tabs = WikiTabs.values(),
        modifier = Modifier.fillMaxSize(),
        pagerState = rememberPagerState()
    ) { page ->
        when (WikiTabs.values()[page]) {
            WikiTabs.Bookmarks -> WikiSelectorList(
                titles = bookmarksTitles,
                onClick = navigateToPageByTitle
            )
            WikiTabs.AllWikiPages -> WikiSelectorList(
                titles = allPagesSlug,
                onClick = navigateToPageBySlug
            )
        }
    }
}

private enum class WikiTabs(@StringRes override val titleId: Int) : Tab {
    Bookmarks(R.string.bookmarks),
    AllWikiPages(R.string.all_wiki_pages)
}

@Composable
fun WikiSelectorList(
    titles: List<String> = emptyList(),
    onClick: (name: String) -> Unit = {}
) = Box(
    Modifier.fillMaxSize(),
    contentAlignment = Alignment.TopStart
) {
    val listItemContent: @Composable LazyItemScope.(Int, String) -> Unit = lambda@{ index, item ->
        WikiSelectorItem(
            title = item,
            onClick = { onClick(item) }
        )

        if (index < titles.lastIndex) {
            Divider(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.outline
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn {
            itemsIndexed(titles, itemContent = listItemContent)
        }
    }

    if (titles.isEmpty()) {
        EmptyWikiDialog(
            isButtonAvailable = false
        )
    }
}

@Composable
private fun WikiSelectorItem(
    title: String,
    onClick: () -> Unit = {}
) = ContainerBox(
    verticalPadding = 16.dp,
    onClick = onClick
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(0.8f)) {
            Text(
                text = title
            )
        }
    }
}

@Preview
@Composable
fun WikiPageSelectorPreview() {
    WikiPageSelectorContent(
        projectName = "Cool project"
    )
}

