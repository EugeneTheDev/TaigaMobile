package io.eugenethedev.taigamobile.ui.screens.stories

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
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
import io.eugenethedev.taigamobile.ui.utils.ResultStatus
import io.eugenethedev.taigamobile.ui.utils.clickableUnindicated
import timber.log.Timber

@ExperimentalAnimationApi
@Composable
fun StoriesScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: StoriesViewModel = viewModel()
    remember {
        viewModel.onScreenOpen()
        null
    }
    val statuses by viewModel.statuses.observeAsState()
    val stories by viewModel.stories.observeAsState()
    val loadingStatusIds by viewModel.loadingStatusIds.observeAsState()

    StoriesScreenContent(
        projectName = viewModel.projectName,
        onTitleClick = { navController.navigate(Routes.projectsSelector) },
        statuses = statuses?.data ?: emptySet(),
        stories = stories ?: emptySet(),
        isStoriesLoading = statuses?.resultStatus == ResultStatus.LOADING,
        loadingStatusIds = loadingStatusIds!!
    )
}

@ExperimentalAnimationApi
@Composable
fun StoriesScreenContent(
    projectName: String,
    onTitleClick: () -> Unit = {},
    statuses: Set<Status> = emptySet(),
    stories: Set<Story> = emptySet(),
    isStoriesLoading: Boolean = false,
    loadingStatusIds: List<Long> = emptyList()
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickableUnindicated(onClick = onTitleClick)
            ) {
                Text(
                    text = projectName.takeIf { it.isNotEmpty() } ?: stringResource(R.string.choose_project_title),
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

    if (isStoriesLoading) {
        CircularProgressIndicator(Modifier
            .size(48.dp)
            .padding(4.dp))
    } else {
        StoriesList(
            statuses = statuses,
            stories = stories,
            loadingStatusIds = loadingStatusIds
        )
    }
}

@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun StoriesScreenPreview() = TaigaMobileTheme {
    StoriesScreenContent("")
}