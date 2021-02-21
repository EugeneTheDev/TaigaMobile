package io.eugenethedev.taigamobile.ui.screens.stories

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.compose.navigate
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Status
import io.eugenethedev.taigamobile.domain.entities.Story
import io.eugenethedev.taigamobile.ui.components.StoriesList
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.ResultStatus
import io.eugenethedev.taigamobile.ui.utils.clickableUnindicated
import io.eugenethedev.taigamobile.ui.utils.subscribeOnError

@ExperimentalAnimationApi
@Composable
fun ScrumScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: ScrumViewModel = viewModel()
    remember {
        viewModel.start()
        null
    }
    val statuses by viewModel.statuses.observeAsState()
    statuses?.subscribeOnError(onError)
    val stories by viewModel.stories.observeAsState()
    stories?.subscribeOnError(onError)
    val loadingStatusIds by viewModel.loadingStatusIds.observeAsState()
    val visibleStatusIds by viewModel.visibleStatusIds.observeAsState()

    ScrumScreenContent(
        projectName = viewModel.projectName,
        onTitleClick = {
            navController.navigate(Routes.projectsSelector)
            viewModel.reset()
        },
        statuses = statuses?.data.orEmpty(),
        stories = stories?.data.orEmpty(),
        isStoriesLoading = statuses?.resultStatus == ResultStatus.LOADING,
        loadingStatusIds = loadingStatusIds.orEmpty(),
        loadStories = viewModel::loadStories,
        visibleStatusIds = visibleStatusIds.orEmpty(),
        onStatusClick = viewModel::statusClick
    )
}

@ExperimentalAnimationApi
@Composable
fun ScrumScreenContent(
    projectName: String,
    onTitleClick: () -> Unit = {},
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
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickableUnindicated(onClick = onTitleClick)
            ) {
                Text(
                    text = projectName.takeIf { it.isNotEmpty() }
                        ?: stringResource(R.string.choose_project_title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.preferredWidthIn(max = 250.dp)
                )
                Icon(
                    imageVector = vectorResource(R.drawable.ic_arrow_down),
                    contentDescription = null
                )
            }
        },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
    )

    if (projectName.isNotEmpty()) {

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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(Modifier.size(40.dp))
                    }
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

            item {
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun ScrumScreenPreview() = TaigaMobileTheme {
    ScrumScreenContent("")
}