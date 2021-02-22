package io.eugenethedev.taigamobile.ui.screens.sprint

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Status
import io.eugenethedev.taigamobile.domain.entities.Story
import io.eugenethedev.taigamobile.ui.components.AppBarWithBackButton
import io.eugenethedev.taigamobile.ui.components.StoriesList
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding

@Composable
fun SprintScreen() {

}

@ExperimentalAnimationApi
@Composable
fun SprintScreenContent(
    sprintName: String,
    statuses: List<Status> = emptyList(),
    stories: List<Story> = emptyList(),
    isStoriesLoading: Boolean = false,
    loadingStatusIds: List<Long> = emptyList(),
    loadStories: (Status) -> Unit = {},
    visibleStatusIds: List<Long> = emptyList(),
    onStatusClick: (Long) -> Unit = {}
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    AppBarWithBackButton(
        title = {
            Text(
                text = sprintName,
                maxLines = 1
            )
        }
    )

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {

        item {
            Text(
                text = stringResource(R.string.stories_without_sprint),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(horizontal = mainHorizontalScreenPadding)
            )
        }

        if (isStoriesLoading) {
            item {
                Loader()
            }
        } else {
            StoriesList(
                statuses = statuses,
                stories = stories,
                loadingStatusIds = loadingStatusIds,
                visibleStatusIds = visibleStatusIds,
                onStatusClick = onStatusClick,
                loadData = loadStories
            )
        }
    }
}

@Composable
private fun Loader() = Box(
    modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
    contentAlignment = Alignment.Center
) {
    CircularProgressIndicator(Modifier.size(40.dp))
}

@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun SprintScreenPreview() = TaigaMobileTheme {
    SprintScreenContent(
        sprintName = "0 sprint"
    )
}