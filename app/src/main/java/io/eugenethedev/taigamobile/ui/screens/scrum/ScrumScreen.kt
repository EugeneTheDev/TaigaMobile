package io.eugenethedev.taigamobile.ui.screens.scrum

import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.pager.ExperimentalPagerApi
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Sprint
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import io.eugenethedev.taigamobile.ui.components.appbars.ProjectAppBar
import io.eugenethedev.taigamobile.ui.components.buttons.AddButton
import io.eugenethedev.taigamobile.ui.components.containers.ContainerBox
import io.eugenethedev.taigamobile.ui.components.containers.HorizontalTabbedPager
import io.eugenethedev.taigamobile.ui.components.containers.Tab
import io.eugenethedev.taigamobile.ui.components.editors.SearchField
import io.eugenethedev.taigamobile.ui.components.lists.SimpleTasksListWithTitle
import io.eugenethedev.taigamobile.ui.components.loaders.DotsLoader
import io.eugenethedev.taigamobile.ui.components.texts.NothingToSeeHereText
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.*
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalPagerApi
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

    val stories by viewModel.stories.observeAsState()
    stories?.subscribeOnError(onError)

    val sprints by viewModel.sprints.observeAsState()
    sprints?.subscribeOnError(onError)

    ScrumScreenContent(
        projectName = viewModel.projectName,
        onTitleClick = {
            navController.navigate(Routes.projectsSelector)
            viewModel.reset()
        },
        stories = stories?.data.orEmpty(),
        isStoriesLoading = stories?.resultStatus == ResultStatus.Loading,
        loadStories = viewModel::loadStories,
        sprints = sprints?.data.orEmpty(),
        isSprintsLoading = sprints?.resultStatus == ResultStatus.Loading,
        loadSprints = viewModel::loadSprints,
        navigateToBoard = {
            navController.navigate(Routes.sprint, Routes.Arguments.sprint to it)
        },
        navigateToTask = navController::navigateToTaskScreen,
        navigateToCreateTask = { navController.navigateToCreateTaskScreen(CommonTaskType.UserStory) }
    )
}

@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun ScrumScreenContent(
    projectName: String,
    onTitleClick: () -> Unit = {},
    stories: List<CommonTask> = emptyList(),
    isStoriesLoading: Boolean = false,
    loadStories: (query: String) -> Unit = {},
    sprints: List<Sprint> = emptyList(),
    isSprintsLoading: Boolean = false,
    loadSprints: () -> Unit = {},
    navigateToBoard: (Sprint) -> Unit = {},
    navigateToTask: NavigateToTask = { _, _, _ -> },
    navigateToCreateTask: () -> Unit = {}
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    ProjectAppBar(
        projectName = projectName,
        onTitleClick = onTitleClick
    )

    HorizontalTabbedPager(
        tabs = Tabs.values(),
        modifier = Modifier.fillMaxSize()
    ) { page ->
        when (Tabs.values()[page]) {
            Tabs.Backlog -> BacklogTabContent(
                stories = stories,
                isStoriesLoading = isStoriesLoading,
                loadStories = loadStories,
                navigateToTask = navigateToTask,
                navigateToCreateTask = navigateToCreateTask
            )
            Tabs.Sprints -> SprintsTabContent(
                sprints = sprints,
                isSprintsLoading = isSprintsLoading,
                navigateToBoard = navigateToBoard,
                loadSprints = loadSprints
            )
        }
    }

}

private enum class Tabs(@StringRes override val titleId: Int) : Tab {
    Backlog(R.string.backlog),
    Sprints(R.string.sprints_title)
}

@ExperimentalAnimationApi
@Composable
private fun BacklogTabContent(
    stories: List<CommonTask>,
    isStoriesLoading: Boolean,
    loadStories: (String) -> Unit,
    navigateToTask: NavigateToTask,
    navigateToCreateTask: () -> Unit
) {
    var query by remember { mutableStateOf(TextFieldValue()) }

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            SearchField(
                hintId = R.string.tasks_search_hint,
                value = query,
                onValueChange = { query = it },
                onSearchClick = { loadStories(query.text) }
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                AddButton(
                    text = stringResource(R.string.add_userstory),
                    onClick = navigateToCreateTask
                )
            }
        }

        SimpleTasksListWithTitle(
            commonTasks = stories,
            navigateToTask = navigateToTask,
            isTasksLoading = isStoriesLoading,
            loadData = { loadStories(query.text) },
            horizontalPadding = mainHorizontalScreenPadding,
            showEmptyMessage = false
        )
    }
}

@Composable
private fun SprintsTabContent(
    sprints: List<Sprint>,
    isSprintsLoading: Boolean,
    navigateToBoard: (Sprint) -> Unit,
    loadSprints: () -> Unit
) = LazyColumn(Modifier.fillMaxSize()) {
    items(sprints) {
        SprintItem(
            sprint = it,
            navigateToBoard = navigateToBoard
        )
    }

    item {
        if (isSprintsLoading) {
            DotsLoader()
        } else if (sprints.isEmpty()) {
            NothingToSeeHereText()
        }

        Spacer(Modifier.navigationBarsHeight(8.dp))

        LaunchedEffect(sprints.size) {
            loadSprints()
        }
    }
}

@Composable
private fun SprintItem(
    sprint: Sprint,
    navigateToBoard: (Sprint) -> Unit = {}
) = ContainerBox(clickEnabled = false) {
    val dateFormatter = remember { SimpleDateFormat.getDateInstance() }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.weight(0.7f)) {
            Text(
                text = sprint.name,
                style = MaterialTheme.typography.subtitle1
            )

            Text(
                stringResource(R.string.sprint_dates_template).format(
                    dateFormatter.format(sprint.start),
                    dateFormatter.format(sprint.finish)
                )
            )

            Row {
                Text(
                    text = stringResource(R.string.stories_count_template).format(sprint.storiesCount),
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.body2
                )

                Spacer(Modifier.width(6.dp))

                if (sprint.isClosed) {
                    Text(
                        text = stringResource(R.string.closed),
                        color = Color.Gray,
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }

        Button(
            onClick = { navigateToBoard(sprint) },
            modifier = Modifier.weight(0.3f),
            colors = buttonColors(
                backgroundColor = if (!sprint.isClosed) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                        .compositeOver(MaterialTheme.colors.surface)
                }
            )
        ) {
            Text(stringResource(R.string.taskboard))
        }
        
    }
}

@Preview(showBackground = true)
@Composable
fun SprintPreview() = TaigaMobileTheme {
    SprintItem(
        Sprint(
            id = 0L,
            name = "1 sprint",
            order = 0,
            start = Date(),
            finish = Date(),
            storiesCount = 4,
            isClosed = true
        )
    )
}

@ExperimentalPagerApi
@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun ScrumScreenPreview() = TaigaMobileTheme {
    ScrumScreenContent("Lol")
}