package io.eugenethedev.taigamobile.ui.screens.scrum

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Sprint
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.ui.commons.LoadingResult
import io.eugenethedev.taigamobile.ui.components.appbars.ProjectAppBar
import io.eugenethedev.taigamobile.ui.components.buttons.PlusButton
import io.eugenethedev.taigamobile.ui.components.containers.ContainerBox
import io.eugenethedev.taigamobile.ui.components.containers.HorizontalTabbedPager
import io.eugenethedev.taigamobile.ui.components.containers.Tab
import io.eugenethedev.taigamobile.ui.components.dialogs.EditSprintDialog
import io.eugenethedev.taigamobile.ui.components.dialogs.LoadingDialog
import io.eugenethedev.taigamobile.ui.components.editors.TextFieldWithHint
import io.eugenethedev.taigamobile.ui.components.editors.searchFieldHorizontalPadding
import io.eugenethedev.taigamobile.ui.components.editors.searchFieldVerticalPadding
import io.eugenethedev.taigamobile.ui.components.lists.SimpleTasksListWithTitle
import io.eugenethedev.taigamobile.ui.components.loaders.DotsLoader
import io.eugenethedev.taigamobile.ui.components.texts.NothingToSeeHereText
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.commonVerticalPadding
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

    val projectName by viewModel.projectName.collectAsState()

    val stories = viewModel.stories
    stories.subscribeOnError {
        onError(R.string.common_error_message)
    }

    val sprints = viewModel.sprints
    sprints.subscribeOnError(onError)
    val createSprintResult by viewModel.createSprintResult.collectAsState()
    createSprintResult.subscribeOnError(onError)

    ScrumScreenContent(
        projectName = projectName,
        onTitleClick = { navController.navigate(Routes.projectsSelector) },
        stories = stories,
        searchStories = viewModel::searchStories,
        sprints = sprints,
        isCreateSprintLoading = createSprintResult is LoadingResult,
        navigateToBoard = {
            navController.navigateToSprint(it.id)
        },
        navigateToTask = navController::navigateToTaskScreen,
        navigateToCreateTask = { navController.navigateToCreateTaskScreen(CommonTaskType.UserStory) },
        createSprint = viewModel::createSprint
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ScrumScreenContent(
    projectName: String,
    onTitleClick: () -> Unit = {},
    stories: LazyPagingItems<CommonTask>? = null,
    searchStories: (query: String) -> Unit = {},
    sprints: LazyPagingItems<Sprint>? = null,
    isCreateSprintLoading: Boolean = false,
    navigateToBoard: (Sprint) -> Unit = {},
    navigateToTask: NavigateToTask = { _, _, _ -> },
    navigateToCreateTask: () -> Unit = {},
    createSprint: (name: String, start: LocalDate, end: LocalDate) -> Unit = { _, _, _ -> }
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    val pagerState = rememberPagerState(pageCount = Tabs.values().size)
    var isCreateSprintDialogVisible by remember { mutableStateOf(false) }

    ProjectAppBar(
        projectName = projectName,
        actions = {
            PlusButton(
                onClick = when (Tabs.values()[pagerState.currentPage]) {
                    Tabs.Backlog -> navigateToCreateTask
                    Tabs.Sprints -> { { isCreateSprintDialogVisible = true } }
                }
            )
        },
        onTitleClick = onTitleClick
    )

    if (isCreateSprintDialogVisible) {
        EditSprintDialog(
            onConfirm = { name, start, end ->
                createSprint(name, start, end)
                isCreateSprintDialogVisible = false
            },
            onDismiss = { isCreateSprintDialogVisible = false }
        )
    }

    if (isCreateSprintLoading) {
        LoadingDialog()
    }

    HorizontalTabbedPager(
        tabs = Tabs.values(),
        modifier = Modifier.fillMaxSize(),
        pagerState = pagerState
    ) { page ->
        when (Tabs.values()[page]) {
            Tabs.Backlog -> BacklogTabContent(
                stories = stories,
                searchStories = searchStories,
                navigateToTask = navigateToTask
            )
            Tabs.Sprints -> SprintsTabContent(
                sprints = sprints,
                navigateToBoard = navigateToBoard
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
    stories: LazyPagingItems<CommonTask>?,
    searchStories: (String) -> Unit,
    navigateToTask: NavigateToTask
) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxWidth()
) {
    var query by remember { mutableStateOf(TextFieldValue()) }

    TextFieldWithHint(
        hintId = R.string.tasks_search_hint,
        value = query,
        onValueChange = { query = it },
        onSearchClick = { searchStories(query.text) },
        horizontalPadding = searchFieldHorizontalPadding,
        verticalPadding = searchFieldVerticalPadding,
        hasBorder = true
    )

    LazyColumn(Modifier.fillMaxSize()) {
        SimpleTasksListWithTitle(
            commonTasksLazy = stories,
            navigateToTask = navigateToTask,
            horizontalPadding = mainHorizontalScreenPadding,
            bottomPadding = commonVerticalPadding
        )
    }
}

@Composable
private fun SprintsTabContent(
    sprints: LazyPagingItems<Sprint>?,
    navigateToBoard: (Sprint) -> Unit,
) = LazyColumn(Modifier.fillMaxSize()) {
    if (sprints == null) return@LazyColumn

    items(sprints, key = { it.id }) {
        if (it == null) return@items
        SprintItem(
            sprint = it,
            navigateToBoard = navigateToBoard
        )
    }

    item {
        if (sprints.loadState.refresh is LoadState.Loading || sprints.loadState.append is LoadState.Loading) {
            DotsLoader()
        } else if (sprints.itemCount == 0) {
            NothingToSeeHereText()
        }

        Spacer(Modifier.navigationBarsHeight(8.dp))
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
