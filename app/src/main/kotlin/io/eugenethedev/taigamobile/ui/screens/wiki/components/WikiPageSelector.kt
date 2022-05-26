package io.eugenethedev.taigamobile.ui.screens.wiki.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.imePadding
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.WikiLink
import io.eugenethedev.taigamobile.domain.entities.WikiPage
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithBackButton
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding

@Composable
fun WikiPageSelector(
    links: List<WikiLink>,
    pages: List<WikiPage>,
    currentPageTitle: String,
    currentPageSlug: String,
    selectPage: (content: String, isBySlug: Boolean) -> Unit = { _, _ -> },
    navigateBack: () -> Unit = {},
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.surface)
        .imePadding(),
) {
    val horizontalPadding = 8.dp
    val sectionsPadding = 32.dp
    val pagesTitle = links.map { it.title }
    val pagesTitleWithoutLinks = pages.filter { it.slug !in links.map { it.ref } }.map { it.slug }
    val isTitlesVisible = pagesTitle.isNotEmpty() && pagesTitleWithoutLinks.isNotEmpty()

    AppBarWithBackButton(
        title = { Text(stringResource(R.string.list_of_bookmarks)) },
        navigateBack = navigateBack
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding / 2),
    ) {
        if (isTitlesVisible) {
            item {
                Text(
                    text = stringResource(R.string.pages),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = horizontalPadding)
                )
            }

            AdvancedSpacer(sectionsPadding / 2)
        }

        items(pagesTitle) {
            WikiPageItem(
                pageTitle = it,
                currentPageTitle = currentPageTitle,
                onClick = {
                    selectPage(it, false)
                }
            )
        }

        if (isTitlesVisible) {
            AdvancedSpacer(sectionsPadding)

            item {
                Text(
                    text = stringResource(R.string.pages_without_link),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = horizontalPadding)
                )
            }

            AdvancedSpacer(sectionsPadding / 2)
        }

        items(pagesTitleWithoutLinks) {
            WikiPageItem(
                pageTitle = it,
                currentPageTitle = currentPageSlug,
                onClick = {
                    selectPage(it, true)
                }
            )
        }
    }

}

@Composable
fun WikiPageItem(
    pageTitle: String,
    currentPageTitle: String,
    onClick: () -> Unit = {}
) = Surface(
    shape = MaterialTheme.shapes.small,
    border = if (pageTitle == currentPageTitle) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    },
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = mainHorizontalScreenPadding, vertical = 4.dp)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onClick
            )
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = pageTitle
        )

        if (pageTitle == currentPageTitle) {
            Image(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }
    }
}
