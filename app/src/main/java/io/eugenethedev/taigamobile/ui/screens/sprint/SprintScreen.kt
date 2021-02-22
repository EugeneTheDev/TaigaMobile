package io.eugenethedev.taigamobile.ui.screens.sprint

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Sprint
import io.eugenethedev.taigamobile.domain.entities.Status
import io.eugenethedev.taigamobile.domain.entities.Story
import io.eugenethedev.taigamobile.ui.components.AppBarWithBackButton
import io.eugenethedev.taigamobile.ui.components.StoriesList
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.ResultStatus
import io.eugenethedev.taigamobile.ui.utils.subscribeOnError
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalAnimationApi
@Composable
fun SprintScreen(
    navController: NavController,
    sprint: Sprint,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: SprintViewModel = viewModel()
    remember {
        viewModel.start(sprint.id)
        null
    }
    val statuses by viewModel.statuses.observeAsState()
    statuses?.subscribeOnError(onError)
    val stories by viewModel.stories.observeAsState()
    stories?.subscribeOnError(onError)
    val loadingStatusIds by viewModel.loadingStatusIds.observeAsState()
    val visibleStatusIds by viewModel.visibleStatusIds.observeAsState()

    SprintScreenContent(
        sprintName = sprint.name,
        start = sprint.start,
        finish = sprint.finish,
        statuses = statuses?.data.orEmpty(),
        stories = stories?.data.orEmpty(),
        isStoriesLoading = statuses?.resultStatus == ResultStatus.LOADING,
        loadingStatusIds = loadingStatusIds.orEmpty(),
        loadStories = viewModel::loadStories,
        visibleStatusIds = visibleStatusIds.orEmpty(),
        onStatusClick = viewModel::statusClick,
        navigateBack = navController::popBackStack
    )
}

@ExperimentalAnimationApi
@Composable
fun SprintScreenContent(
    sprintName: String,
    start: Date,
    finish: Date,
    statuses: List<Status> = emptyList(),
    stories: List<Story> = emptyList(),
    isStoriesLoading: Boolean = false,
    loadingStatusIds: List<Long> = emptyList(),
    loadStories: (Status) -> Unit = {},
    visibleStatusIds: List<Long> = emptyList(),
    onStatusClick: (Long) -> Unit = {},
    navigateBack: () -> Unit = {}
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    val dateFormatter = remember { SimpleDateFormat.getDateInstance() }

    AppBarWithBackButton(
        title = {
            Text(
                text = sprintName,
                maxLines = 1
            )
        },
        navigateBack = navigateBack
    )

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {

        item {
            Column(Modifier.padding(horizontal = mainHorizontalScreenPadding)) {
                Text(
                    text = stringResource(R.string.sprint_dates_template).format(
                        dateFormatter.format(start),
                        dateFormatter.format(finish)
                    )
                )

                Text(
                    text = stringResource(R.string.stories),
                    style = MaterialTheme.typography.h6,
                )
            }
        }

        if (isStoriesLoading) {
            item {
                Loader()
            }
        } else {
            StoriesList(
                inverseCategoriesVisibility = true,
                statuses = statuses,
                stories = stories,
                loadingStatusIds = loadingStatusIds,
                visibleStatusIds = visibleStatusIds,
                onStatusClick = onStatusClick,
                loadData = loadStories
            )
        }

        item {
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun Loader() = Box(
    modifier = Modifier.fillMaxWidth().padding(8.dp),
    contentAlignment = Alignment.Center
) {
    CircularProgressIndicator(Modifier.size(40.dp))
}

@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun SprintScreenPreview() = TaigaMobileTheme {
    SprintScreenContent(
        sprintName = "0 sprint",
        start = Date(),
        finish = Date()
    )
}