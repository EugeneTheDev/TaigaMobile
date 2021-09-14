package io.eugenethedev.taigamobile.ui.screens.scrum

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
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
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Sprint
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.ui.commons.LoadingResult
import io.eugenethedev.taigamobile.ui.components.appbars.ProjectAppBar
import io.eugenethedev.taigamobile.ui.components.buttons.AddButton
import io.eugenethedev.taigamobile.ui.components.containers.ContainerBox
import io.eugenethedev.taigamobile.ui.components.containers.HorizontalTabbedPager
import io.eugenethedev.taigamobile.ui.components.containers.Tab
import io.eugenethedev.taigamobile.ui.components.dialogs.EditSprintDialog
import io.eugenethedev.taigamobile.ui.components.editors.TextFieldWithHint
import io.eugenethedev.taigamobile.ui.components.editors.searchFieldHorizontalPadding
import io.eugenethedev.taigamobile.ui.components.editors.searchFieldVerticalPadding
import io.eugenethedev.taigamobile.ui.components.lists.SimpleTasksListWithTitle
import io.eugenethedev.taigamobile.ui.components.loaders.DotsLoader
import io.eugenethedev.taigamobile.ui.components.texts.NothingToSeeHereText
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ScrumScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: ScrumViewModel = viewModel()
    LaunchedEffect(Unit) {
        viewModel.start()
    }

    val stories by viewModel.stories.collectAsState()
    stories.subscribeOnError(onError)

    val sprints by viewModel.sprints.collectAsState()
    sprints.subscribeOnError(onError)

    ScrumScreenContent(
        projectName = viewModel.projectName,
        onTitleClick = {
            navController.navigate(Routes.projectsSelector)
            viewModel.reset()
        },
        stories = stories.data.orEmpty(),
        isStoriesLoading = stories is LoadingResult,
        loadStories = viewModel::loadStories,
        sprints = sprints.data.orEmpty(),
        isSprintsLoading = sprints is LoadingResult,
        loadSprints = viewModel::loadSprints,
        navigateToBoard = {
            navController.navigateToSprint(it.id)
        },
        navigateToTask = navController::navigateToTaskScreen,
        navigateToCreateTask = { navController.navigateToCreateTaskScreen(CommonTaskType.UserStory) },
        createSprint = viewModel::createSprint
    )
}

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
    navigateToCreateTask: () -> Unit = {},
    createSprint: (name: String, start: LocalDate, end: LocalDate) -> Unit = { _, _, _ -> }
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
                loadSprints = loadSprints,
                createSprint = createSprint
            )
        }
    }

}

private enum class Tabs(@StringRes override val titleId: Int) : Tab {
    Backlog(R.string.backlog),
    Sprints(R.string.sprints_title)
}

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
            TextFieldWithHint(
                hintId = R.string.tasks_search_hint,
                value = query,
                onValueChange = { query = it },
                onSearchClick = { loadStories(query.text) },
                horizontalPadding = searchFieldHorizontalPadding,
                verticalPadding = searchFieldVerticalPadding,
                hasBorder = true
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
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
        )
    }
}

@Composable
private fun SprintsTabContent(
    sprints: List<Sprint>,
    isSprintsLoading: Boolean,
    navigateToBoard: (Sprint) -> Unit,
    loadSprints: () -> Unit,
    createSprint: (name: String, start: LocalDate, end: LocalDate) -> Unit
) = LazyColumn(Modifier.fillMaxSize()) {
    item {
        var isCreateSprintDialogVisible by remember { mutableStateOf(false) }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            AddButton(
                text = stringResource(R.string.add_sprint),
                onClick = { isCreateSprintDialogVisible = true }
            )
        }

        if (isCreateSprintDialogVisible) {
            EditSprintDialog(
                onConfirm = { name, start, end ->
                    createSprint(name, start, end)
                    isCreateSprintDialogVisible = false
                },
                onDismiss = { isCreateSprintDialogVisible = false }
            )
        }
    }

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
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }

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
                    sprint.start.format(dateFormatter),
                    sprint.end.format(dateFormatter)
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
            start = LocalDate.now(),
            end = LocalDate.now(),
            storiesCount = 4,
            isClosed = true
        )
    )
}

@Preview(showBackground = true)
@Composable
fun ScrumScreenPreview() = TaigaMobileTheme {
    ScrumScreenContent("Lol")
}
