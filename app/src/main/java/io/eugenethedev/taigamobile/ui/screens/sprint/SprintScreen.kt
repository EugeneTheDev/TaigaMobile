package io.eugenethedev.taigamobile.ui.screens.sprint

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Sprint
import io.eugenethedev.taigamobile.domain.entities.Status
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithBackButton
import io.eugenethedev.taigamobile.ui.components.dialogs.ConfirmActionDialog
import io.eugenethedev.taigamobile.ui.components.dialogs.EditSprintDialog
import io.eugenethedev.taigamobile.ui.components.dialogs.LoadingDialog
import io.eugenethedev.taigamobile.ui.components.loaders.CircularLoader
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.utils.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun SprintScreen(
    navController: NavController,
    sprintId: Long,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: SprintViewModel = viewModel()
    remember {
        viewModel.start(sprintId)
        null
    }

    val sprint by viewModel.sprint.observeAsState()
    sprint?.subscribeOnError(onError)

    val statuses by viewModel.statuses.observeAsState()
    statuses?.subscribeOnError(onError)

    val storiesWithTasks by viewModel.storiesWithTasks.observeAsState()
    storiesWithTasks?.subscribeOnError(onError)

    val storylessTasks by viewModel.storylessTasks.observeAsState()
    storylessTasks?.subscribeOnError(onError)

    val issues by viewModel.issues.observeAsState()
    issues?.subscribeOnError(onError)

    val editResult by viewModel.editResult.observeAsState()
    editResult?.subscribeOnError(onError)

    val deleteResult by viewModel.deleteResult.observeAsState()
    deleteResult?.subscribeOnError(onError)
    deleteResult?.takeIf { it.resultStatus == ResultStatus.Success }?.let {
        navController.popBackStack()
    }

    SprintScreenContent(
        sprint = sprint?.data,
        isLoading = listOf(statuses, storiesWithTasks, storylessTasks, issues).any { it?.resultStatus == ResultStatus.Loading },
        isEditLoading = editResult?.resultStatus == ResultStatus.Loading,
        isDeleteLoading = deleteResult?.resultStatus == ResultStatus.Loading,
        statuses = statuses?.data.orEmpty(),
        storiesWithTasks = storiesWithTasks?.data.orEmpty(),
        storylessTasks = storylessTasks?.data.orEmpty(),
        issues = issues?.data.orEmpty(),
        editSprint = viewModel::editSprint,
        deleteSprint = viewModel::deleteSprint,
        navigateBack = navController::popBackStack,
        navigateToTask = navController::navigateToTaskScreen,
        navigateToCreateTask = { type, parentId -> navController.navigateToCreateTaskScreen(type, parentId, sprintId) }
    )
}


@Composable
fun SprintScreenContent(
    sprint: Sprint?,
    isLoading: Boolean = false,
    isEditLoading: Boolean = false,
    isDeleteLoading: Boolean = false,
    statuses: List<Status> = emptyList(),
    storiesWithTasks: Map<CommonTask, List<CommonTask>> = emptyMap(),
    storylessTasks: List<CommonTask> = emptyList(),
    issues: List<CommonTask> = emptyList(),
    editSprint: (name: String, start: LocalDate, end: LocalDate) -> Unit = { _, _, _ -> },
    deleteSprint: () -> Unit = {},
    navigateBack: () -> Unit = {},
    navigateToTask: NavigateToTask = { _, _, _ -> },
    navigateToCreateTask: (type: CommonTaskType, parentId: Long?) -> Unit = { _, _ -> }
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }

    var isMenuExpanded by remember { mutableStateOf(false) }
    AppBarWithBackButton(
        title = {
            Column {
                Text(
                    text = sprint?.name.orEmpty(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = stringResource(R.string.sprint_dates_template).format(
                        sprint?.start?.format(dateFormatter).orEmpty(),
                        sprint?.end?.format(dateFormatter).orEmpty()
                    ),
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        actions = {
            Box {
                IconButton(onClick = { isMenuExpanded = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_options),
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary
                    )
                }

                // delete alert dialog
                var isDeleteAlertVisible by remember { mutableStateOf(false) }
                if (isDeleteAlertVisible) {
                    ConfirmActionDialog(
                        title = stringResource(R.string.delete_sprint_title),
                        text = stringResource(R.string.delete_sprint_text),
                        onConfirm = {
                            isDeleteAlertVisible = false
                            deleteSprint()
                        },
                        onDismiss = { isDeleteAlertVisible = false }
                    )
                }

                var isEditDialogVisible by remember { mutableStateOf(false) }
                if (isEditDialogVisible) {
                    EditSprintDialog(
                        initialName = sprint?.name.orEmpty(),
                        initialStart = sprint?.start,
                        initialEnd = sprint?.end,
                        onConfirm = { name, start, end ->
                            editSprint(name, start, end)
                            isEditDialogVisible = false
                        },
                        onDismiss = { isEditDialogVisible = false }
                    )
                }

                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    // edit
                    DropdownMenuItem(
                        onClick = {
                            isMenuExpanded = false
                            isEditDialogVisible = true
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.edit),
                            style = MaterialTheme.typography.body1
                        )
                    }

                    // delete
                    DropdownMenuItem(
                        onClick = {
                            isMenuExpanded = false
                            isDeleteAlertVisible = true
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.delete),
                            style = MaterialTheme.typography.body1
                        )
                    }
                }
            }
        },
        navigateBack = navigateBack
    )

    when {
        isLoading || sprint == null -> Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularLoader()
        }

        isEditLoading || isDeleteLoading -> LoadingDialog()

        else -> SprintKanban(
            statuses = statuses,
            storiesWithTasks = storiesWithTasks,
            storylessTasks = storylessTasks,
            issues = issues,
            navigateToTask = navigateToTask,
            navigateToCreateTask = navigateToCreateTask
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SprintScreenPreview() = TaigaMobileTheme {
    SprintScreenContent(
        sprint = Sprint(
            id = 0L,
            name = "0 sprint",
            start = LocalDate.now(),
            end = LocalDate.now(),
            order = 0,
            storiesCount = 0,
            isClosed = false
        )
    )
}
