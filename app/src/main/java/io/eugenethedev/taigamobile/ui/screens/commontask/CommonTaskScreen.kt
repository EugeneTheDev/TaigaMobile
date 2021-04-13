package io.eugenethedev.taigamobile.ui.screens.commontask

import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.ui.components.*
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithBackButton
import io.eugenethedev.taigamobile.ui.components.editors.TaskEditor
import io.eugenethedev.taigamobile.ui.components.loaders.CircularLoader
import io.eugenethedev.taigamobile.ui.components.loaders.DotsLoader
import io.eugenethedev.taigamobile.ui.components.loaders.LoadingDialog
import io.eugenethedev.taigamobile.ui.components.texts.MarkdownText
import io.eugenethedev.taigamobile.ui.components.texts.NothingToSeeHereText
import io.eugenethedev.taigamobile.ui.components.texts.TitleWithIndicators
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.*
import java.util.*

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun CommonTaskScreen(
    navController: NavController,
    commonTaskId: Long,
    commonTaskType: CommonTaskType,
    ref: Int,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: CommonTaskViewModel = viewModel()
    remember {
        viewModel.start(commonTaskId, commonTaskType)
        null
    }

    val commonTask by viewModel.commonTask.observeAsState()
    commonTask?.subscribeOnError(onError)
    val creator by viewModel.creator.observeAsState()
    creator?.subscribeOnError(onError)
    val assignees by viewModel.assignees.observeAsState()
    assignees?.subscribeOnError(onError)
    val watchers by viewModel.watchers.observeAsState()
    watchers?.subscribeOnError(onError)
    val userStories by viewModel.userStories.observeAsState()
    userStories?.subscribeOnError(onError)
    val tasks by viewModel.tasks.observeAsState()
    tasks?.subscribeOnError(onError)
    val comments by viewModel.comments.observeAsState()
    comments?.subscribeOnError(onError)

    val statuses by viewModel.statuses.observeAsState()
    statuses?.subscribeOnError(onError)
    val statusSelectResult by viewModel.statusSelectResult.observeAsState()
    statusSelectResult?.subscribeOnError(onError)

    val sprints by viewModel.sprints.observeAsState()
    sprints?.subscribeOnError(onError)
    val sprintSelectResult by viewModel.sprintSelectResult.observeAsState()
    sprintSelectResult?.subscribeOnError(onError)

    val epics by viewModel.epics.observeAsState()
    epics?.subscribeOnError(onError)
    val epicsSelectResult by viewModel.epicsSelectResult.observeAsState()
    epicsSelectResult?.subscribeOnError(onError)

    val team by viewModel.team.observeAsState()
    team?.subscribeOnError(onError)

    val assigneesResult by viewModel.assigneesResult.observeAsState()
    assigneesResult?.subscribeOnError(onError)

    val watchersResult by viewModel.watchersResult.observeAsState()
    watchersResult?.subscribeOnError(onError)

    val commentsResult by viewModel.commentsResult.observeAsState()
    commentsResult?.subscribeOnError(onError)

    val editResult by viewModel.editResult.observeAsState()
    editResult?.subscribeOnError(onError)

    val deleteResult by viewModel.deleteResult.observeAsState()
    deleteResult?.subscribeOnError(onError)
    deleteResult?.takeIf { it.resultStatus == ResultStatus.SUCCESS }?.let {
        navController.popBackStack()
    }

    val promoteResult by viewModel.promoteResult.observeAsState()
    promoteResult?.subscribeOnError(onError)
    promoteResult?.takeIf { it.resultStatus == ResultStatus.SUCCESS }?.data?.let {
        navController.popBackStack()
        navController.navigateToTaskScreen(it.id, CommonTaskType.USERSTORY, it.ref)
    }

    commonTask?.data.let {
        CommonTaskScreenContent(
            commonTaskType = commonTaskType,
            toolbarTitle = stringResource(
                when (commonTaskType) {
                    CommonTaskType.USERSTORY -> R.string.userstory_slug
                    CommonTaskType.TASK -> R.string.task_slug
                    CommonTaskType.EPIC -> R.string.epic_slug
                    CommonTaskType.ISSUE -> R.string.issue_slug
                }
            ).format(ref),
            epicColor = it?.color ?: "#000000",
            statusName = it?.status?.name ?: "",
            statusColorHex = it?.status?.color ?: "#000000",
            sprintName = it?.sprint?.name,
            storyTitle = it?.title ?: "",
            story = it?.userStoryShortInfo,
            epics = it?.epicsShortInfo.orEmpty(),
            description = it?.description ?: "",
            creationDateTime = it?.createdDateTime ?: Date(),
            creator = creator?.data,
            assignees = assignees?.data.orEmpty(),
            watchers = watchers?.data.orEmpty(),
            userStories = userStories?.data.orEmpty(),
            tasks = tasks?.data.orEmpty(),
            comments = comments?.data.orEmpty(),
            isLoading = commonTask?.resultStatus == ResultStatus.LOADING,
            navigateBack = navController::popBackStack,
            navigateToCreateTask = { navController.navigateToCreateTaskScreen(CommonTaskType.TASK, commonTaskId) },
            navigateToTask = navController::navigateToTaskScreen,
            editStatus = EditAction(
                items = statuses?.data.orEmpty(),
                loadItems = viewModel::loadStatuses,
                isItemsLoading = statuses?.resultStatus == ResultStatus.LOADING,
                selectItem = viewModel::selectStatus,
                isResultLoading = statusSelectResult?.resultStatus == ResultStatus.LOADING
            ),
            editSprint = EditAction(
                items = sprints?.data.orEmpty(),
                loadItems = viewModel::loadSprints,
                isItemsLoading = sprints?.resultStatus == ResultStatus.LOADING,
                selectItem = viewModel::selectSprint,
                isResultLoading = sprintSelectResult?.resultStatus == ResultStatus.LOADING
            ),
            editEpics = EditAction(
                items = epics?.data.orEmpty(),
                loadItems = viewModel::loadEpics,
                isItemsLoading = epics?.resultStatus == ResultStatus.LOADING,
                selectItem = viewModel::linkToEpic,
                isResultLoading = epicsSelectResult?.resultStatus == ResultStatus.LOADING,
                removeItem = {
                    // Since epic structure in CommonTaskExtended differs from what is used in edit there is separate lambda
                }
            ),
            unlinkFromEpic = viewModel::unlinkFromEpic,
            editAssignees = EditAction(
                items = team?.data.orEmpty(),
                loadItems = viewModel::loadTeam,
                isItemsLoading = team?.resultStatus == ResultStatus.LOADING,
                selectItem = viewModel::addAssignee,
                isResultLoading = assigneesResult?.resultStatus == ResultStatus.LOADING,
                removeItem = viewModel::removeAssignee
            ),
            editWatchers = EditAction(
                items = team?.data.orEmpty(),
                loadItems = viewModel::loadTeam,
                isItemsLoading = team?.resultStatus == ResultStatus.LOADING,
                selectItem = viewModel::addWatcher,
                isResultLoading = watchersResult?.resultStatus == ResultStatus.LOADING,
                removeItem = viewModel::removeWatcher
            ),
            editComments = EditCommentsAction(
                createComment = viewModel::createComment,
                deleteComment = viewModel::deleteComment,
                isResultLoading = commentsResult?.resultStatus == ResultStatus.LOADING
            ),
            editTask = viewModel::editTask,
            deleteTask = viewModel::deleteTask,
            isEditLoading = editResult?.resultStatus == ResultStatus.LOADING,
            isDeleteLoading = deleteResult?.resultStatus == ResultStatus.LOADING,
            promoteTask = viewModel::promoteTask,
            isPromoteLoading = promoteResult?.resultStatus == ResultStatus.LOADING
        )
    }

}

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun CommonTaskScreenContent(
    commonTaskType: CommonTaskType,
    toolbarTitle: String,
    epicColor: String,
    statusName: String,
    statusColorHex: String,
    sprintName: String?,
    storyTitle: String,
    epics: List<EpicShortInfo> = emptyList(),
    story: UserStoryShortInfo?,
    description: String,
    creationDateTime: Date,
    creator: User?,
    assignees: List<User> = emptyList(),
    watchers: List<User> = emptyList(),
    userStories: List<CommonTask> = emptyList(),
    tasks: List<CommonTask> = emptyList(),
    comments: List<Comment> = emptyList(),
    isLoading: Boolean = false,
    navigateBack: () -> Unit = {},
    navigateToCreateTask: () -> Unit = {},
    navigateToTask: NavigateToTask = { _, _, _ -> },
    editStatus: EditAction<Status> = EditAction(),
    editSprint: EditAction<Sprint?> = EditAction(),
    editEpics: EditAction<CommonTask> = EditAction(),
    unlinkFromEpic: (EpicShortInfo) -> Unit = {},
    editAssignees: EditAction<User> = EditAction(),
    editWatchers: EditAction<User> = EditAction(),
    editComments: EditCommentsAction = EditCommentsAction(),
    editTask: (title: String, description: String) -> Unit = { _, _ -> },
    deleteTask: () -> Unit = {},
    isEditLoading: Boolean = false,
    isDeleteLoading: Boolean = false,
    promoteTask: () -> Unit = {},
    isPromoteLoading: Boolean = false
) = Box(Modifier.fillMaxSize()) {
    var isTaskEditorVisible by remember { mutableStateOf(false) }

    var isStatusSelectorVisible by remember { mutableStateOf(false) }
    var isSprintSelectorVisible by remember { mutableStateOf(false) }
    var isAssigneesSelectorVisible by remember { mutableStateOf(false) }
    var isWatchersSelectorVisible by remember { mutableStateOf(false) }
    var isEpicsSelectorVisible by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        var isMenuExpanded by remember { mutableStateOf(false) }
        AppBarWithBackButton(
            title = {
                Text(
                    text = toolbarTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
                        ConfirmActionAlert(
                            title = stringResource(R.string.delete_task_title),
                            text = stringResource(R.string.delete_task_text),
                            onConfirm = {
                                isDeleteAlertVisible = false
                                deleteTask()
                            },
                            onDismiss = { isDeleteAlertVisible = false }
                        )
                    }

                    // promote alert dialog
                    var isPromoteAlertVisible by remember { mutableStateOf(false) }
                    if (isPromoteAlertVisible) {
                        ConfirmActionAlert(
                            title = stringResource(R.string.promote_title),
                            text = stringResource(R.string.promote_text),
                            onConfirm = {
                                isPromoteAlertVisible = false
                                promoteTask()
                            },
                            onDismiss = { isPromoteAlertVisible = false }
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
                                isTaskEditorVisible = true
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

                        // promote
                        if (commonTaskType == CommonTaskType.TASK) {
                            DropdownMenuItem(
                                onClick = {
                                    isMenuExpanded = false
                                    isPromoteAlertVisible = true
                                }
                            ) {
                                Text(
                                    text = stringResource(R.string.promote_to_user_story),
                                    style = MaterialTheme.typography.body1
                                )
                            }
                        }
                    }
                }
            },
            navigateBack = navigateBack
        )

        if (isLoading || creator == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularLoader()
            }
        } else {
            val sectionsMargin = 10.dp

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = mainHorizontalScreenPadding)
            ) {

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // epic color
                        if (commonTaskType == CommonTaskType.EPIC) {
                            Spacer(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = Color(android.graphics.Color.parseColor(epicColor)),
                                        shape = MaterialTheme.shapes.small
                                    )
                            )

                            Spacer(Modifier.width(8.dp))
                        }

                        // status
                        ClickableBadge(
                            text = statusName,
                            colorHex = statusColorHex,
                            onClick = {
                                isStatusSelectorVisible = true
                                editStatus.loadItems(null)
                            },
                            isLoading = editStatus.isResultLoading
                        )

                        Spacer(Modifier.width(8.dp))

                        // sprint
                        if (commonTaskType != CommonTaskType.EPIC) {
                            ClickableBadge(
                                text = sprintName ?: stringResource(R.string.no_sprint),
                                color = sprintName?.let { MaterialTheme.colors.primary }
                                    ?: Color.Gray,
                                onClick = {
                                    isSprintSelectorVisible = true
                                    editSprint.loadItems(null)
                                },
                                isLoading = editSprint.isResultLoading,
                                isClickable = commonTaskType != CommonTaskType.TASK
                            )
                        }

                    }

                    // title
                    Text(
                        text = storyTitle,
                        style = MaterialTheme.typography.h5
                    )

                    Spacer(Modifier.height(4.dp))
                }

                // belongs to (epics)
                if (commonTaskType == CommonTaskType.USERSTORY) {
                    item {
                        Text(
                            text = stringResource(R.string.belongs_to_epics),
                            style = MaterialTheme.typography.subtitle1
                        )
                    }

                    items(epics) {
                        EpicItemWithAction(
                            epic = it,
                            onClick = { navigateToTask(it.id, CommonTaskType.EPIC, it.ref) },
                            onRemoveClick = { unlinkFromEpic(it) }
                        )

                        Spacer(Modifier.height(2.dp))
                    }

                    item {
                        if (editEpics.isResultLoading) {
                            DotsLoader()
                        }

                        AddButton(
                            text = stringResource(R.string.link_to_epic),
                            onClick = {
                                isEpicsSelectorVisible = true
                                editEpics.loadItems(null)
                            }
                        )
                    }
                }

                // belongs to (story)
                if (commonTaskType == CommonTaskType.TASK) {
                    story?.let {
                        item {
                            Text(
                                text = stringResource(R.string.belongs_to_story),
                                style = MaterialTheme.typography.subtitle1
                            )

                            UserStoryItem(
                                story = story,
                                onClick = { navigateToTask(it.id, CommonTaskType.USERSTORY, it.ref) }
                            )
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(sectionsMargin * 2))

                    // description
                    if (description.isNotEmpty()) {
                        MarkdownText(
                            text = description,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        NothingToSeeHereText()
                    }

                    Spacer(Modifier.height(sectionsMargin * 2))

                    // created by
                    Text(
                        text = stringResource(R.string.created_by),
                        style = MaterialTheme.typography.subtitle1
                    )

                    UserItem(
                        user = creator,
                        dateTime = creationDateTime
                    )

                    Spacer(Modifier.height(sectionsMargin))

                    // assigned to
                    Text(
                        text = stringResource(R.string.assigned_to),
                        style = MaterialTheme.typography.subtitle1
                    )
                }

                itemsIndexed(assignees) { index, item ->
                    UserItemWithAction(
                        user = item,
                        onRemoveClick = { editAssignees.removeItem(item) }
                    )

                    if (index < assignees.lastIndex) {
                        Spacer(Modifier.height(6.dp))
                    }
                }

                // add assignee & loader
                item {
                    if (editAssignees.isResultLoading) {
                        DotsLoader()
                    }
                    AddButton(
                        text = stringResource(R.string.add_user),
                        onClick = {
                            isAssigneesSelectorVisible = true
                            editAssignees.loadItems(null)
                        }
                    )
                }


                item {
                    Spacer(Modifier.height(sectionsMargin))

                    // watchers
                    Text(
                        text = stringResource(R.string.watchers),
                        style = MaterialTheme.typography.subtitle1
                    )
                }

                itemsIndexed(watchers) { index, item ->
                    UserItemWithAction(
                        user = item,
                        onRemoveClick = { editWatchers.removeItem(item) }
                    )

                    if (index < watchers.lastIndex) {
                        Spacer(Modifier.height(6.dp))
                    }
                }

                // add watcher & loader
                item {
                    if (editWatchers.isResultLoading) {
                        DotsLoader()
                    }
                    AddButton(
                        text = stringResource(R.string.add_user),
                        onClick = {
                            isWatchersSelectorVisible = true
                            editWatchers.loadItems(null)
                        }
                    )
                }

                // user stories
                if (commonTaskType == CommonTaskType.EPIC) {
                    CommonTasksList(
                        titleText = R.string.userstories,
                        margin = sectionsMargin * 2,
                        tasks = userStories,
                        isCreateSupported = false,
                        navigateToTask = navigateToTask
                    )
                }

                // tasks
                if (commonTaskType == CommonTaskType.USERSTORY) {
                    CommonTasksList(
                        titleText = R.string.tasks,
                        margin = sectionsMargin * 2,
                        tasks = tasks,
                        isCreateSupported = true,
                        navigateToTask = navigateToTask,
                        navigateToCreateCommonTask = navigateToCreateTask
                    )
                }

                item {
                    Divider(
                        modifier = Modifier.padding(
                            top = sectionsMargin * 2,
                            bottom = sectionsMargin
                        ),
                        color = Color.LightGray,
                        thickness = 2.dp
                    )

                    // comments
                    Text(
                        text = stringResource(R.string.comments_template).format(comments.size),
                        style = MaterialTheme.typography.h6
                    )

                    Spacer(Modifier.height(4.dp))
                }

                itemsIndexed(comments) { index, item ->
                    CommentItem(
                        comment = item,
                        onDeleteClick = { editComments.deleteComment(item) }
                    )

                    if (index < comments.lastIndex) {
                        Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color.LightGray
                        )
                    }
                }

                item {
                    if (editComments.isResultLoading) {
                        DotsLoader()
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }

            CreateCommentBar(editComments.createComment)
        }
    }


    // Bunch of list selectors
    Selectors(
        editStatus = editStatus,
        isStatusSelectorVisible = isStatusSelectorVisible,
        hideStatusSelector = { isStatusSelectorVisible = false },
        editSprint = editSprint,
        isSprintSelectorVisible = isSprintSelectorVisible,
        hideSprintSelector = { isSprintSelectorVisible = false },
        editAssignees = editAssignees,
        isAssigneesSelectorVisible = isAssigneesSelectorVisible,
        hideAssigneesSelector = { isAssigneesSelectorVisible = false },
        editWatchers = editWatchers,
        isWatchersSelectorVisible = isWatchersSelectorVisible,
        hideWatchersSelector = { isWatchersSelectorVisible = false },
        editEpics = editEpics,
        isEpicsSelectorVisible = isEpicsSelectorVisible,
        hideEpicsSelector = { isEpicsSelectorVisible = false }
    )

    // Editor
    if (isTaskEditorVisible || isEditLoading) {
        TaskEditor(
            toolbarText = stringResource(R.string.edit),
            title = storyTitle,
            description = description,
            onSaveClick = { title, description ->
                isTaskEditorVisible = false
                editTask(title, description)
            },
            navigateBack = { isTaskEditorVisible = false }
        )
    }

    if (isEditLoading || isDeleteLoading || isPromoteLoading) {
        LoadingDialog()
    }
}

@Composable
private fun EpicItemWithAction(
    epic: EpicShortInfo,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit,
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
) {
    var isAlertVisible by remember { mutableStateOf(false) }

    if (isAlertVisible) {
        ConfirmActionAlert(
            title = stringResource(R.string.unlink_epic_title),
            text = stringResource(R.string.unlink_epic_text),
            onConfirm = {
                isAlertVisible = false
                onRemoveClick()
            },
            onDismiss = { isAlertVisible = false }
        )
    }

    Text(
        text = buildAnnotatedString {
            append(stringResource(R.string.title_with_ref_pattern).format(epic.ref, epic.title))
            append(" ")

            pushStyle(SpanStyle(color = Color(android.graphics.Color.parseColor(epic.color))))

            append("⬤") // 2B24

        },
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.subtitle1,
        modifier = Modifier
            .weight(1f, fill = false)
            .padding(end = 4.dp)
            .clickableUnindicated(onClick = onClick)
    )

    IconButton(
        onClick = { isAlertVisible = true },
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_remove),
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

@Composable
private fun UserStoryItem(
    story: UserStoryShortInfo,
    onClick: () -> Unit
) = TitleWithIndicators(
    ref = story.ref,
    title = story.title,
    textColor = MaterialTheme.colors.primary,
    indicatorColorsHex = story.epicColors,
    modifier = Modifier.clickableUnindicated(onClick = onClick)
)

@Composable
private fun UserItemWithAction(
    user: User,
    onRemoveClick: () -> Unit
) {
    var isAlertVisible by remember { mutableStateOf(false) }

    if (isAlertVisible) {
        ConfirmActionAlert(
            title = stringResource(R.string.remove_user_title),
            text = stringResource(R.string.remove_user_text),
            onConfirm = {
                isAlertVisible = false
                onRemoveClick()
            },
            onDismiss = { isAlertVisible = false }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        UserItem(user)

        IconButton(onClick = { isAlertVisible = true }) {
            Icon(
                painter = painterResource(R.drawable.ic_remove),
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

private fun LazyListScope.CommonTasksList(
    @StringRes titleText: Int,
    margin: Dp,
    tasks: List<CommonTask>,
    isCreateSupported: Boolean,
    navigateToTask: NavigateToTask,
    navigateToCreateCommonTask: () -> Unit = {}
) {
    item {
        Spacer(Modifier.height(margin))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(titleText),
                style = MaterialTheme.typography.h6
            )

            if (isCreateSupported) {
                PlusButton(onClick = navigateToCreateCommonTask)
            }
        }

        if (tasks.isEmpty()) {
            NothingToSeeHereText()
        }
    }

    itemsIndexed(tasks) { index, item ->
        CommonTaskItem(
            commonTask = item,
            horizontalPadding = 0.dp,
            navigateToTask = navigateToTask
        )

        if (index < tasks.lastIndex) {
            Divider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = Color.LightGray
            )
        }
    }
}

@Composable
private fun CommentItem(
    comment: Comment,
    onDeleteClick: () -> Unit
) = Column {
    var isAlertVisible by remember { mutableStateOf(false) }

    if (isAlertVisible) {
        ConfirmActionAlert(
            title = stringResource(R.string.delete_comment_title),
            text = stringResource(R.string.delete_comment_text),
            onConfirm = {
                isAlertVisible = false
                onDeleteClick()
            },
            onDismiss = { isAlertVisible = false }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        UserItem(
            user = comment.author,
            dateTime = comment.postDateTime
        )

        if (comment.canDelete == true) {
            IconButton(onClick = { isAlertVisible = true }) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = null,
                    tint = Color.Red
                )
            }
        }
    }

    MarkdownText(
        text = comment.text,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun AddButton(
    text: String,
    onClick: () -> Unit
) = TextButton(
    onClick = onClick,
    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(R.drawable.ic_add),
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )

        Text(
            text = text,
            color = MaterialTheme.colors.primary
        )
    }
}


@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CommonTaskScreenPreview() = TaigaMobileTheme {
    CommonTaskScreenContent(
        commonTaskType = CommonTaskType.USERSTORY,
        toolbarTitle = "Userstory #99",
        epicColor = "#000000",
        statusName = "In progress",
        statusColorHex = "#729fcf",
        sprintName = "Very very very long sprint name",
        storyTitle = "Very cool and important story. Need to do this quickly",
        story = null,
        epics = List(1) {
            EpicShortInfo(
                id = 1L,
                title = "Important epic",
                ref = 1,
                color = "#F2C94C"
            )
        },
        description = "Some description about this wonderful task",
        creationDateTime = Date(),
        creator = User(
            _id = 0L,
            fullName = "Full Name",
            photo = null,
            bigPhoto = null,
            username = "username"
        ),
        assignees = List(1) {
            User(
                _id = 0L,
                fullName = "Full Name",
                photo = null,
                bigPhoto = null,
                username = "username"
            )
        },
        watchers = List(2) {
            User(
                _id = 0L,
                fullName = "Full Name",
                photo = null,
                bigPhoto = null,
                username = "username"
            )
        },
        tasks = List(1) {
            CommonTask(
                id = it.toLong(),
                createdDate = Date(),
                title = "Very cool story",
                ref = 100,
                status = Status(
                    id = (0..2).random().toLong(),
                    name = "In progress",
                    color = "#729fcf"
                ),
                assignee = CommonTask.Assignee(
                    id = it.toLong(),
                    fullName = "Name Name"
                ),
                projectSlug = "000",
                taskType = CommonTaskType.USERSTORY,
                isClosed = false
            )
        },
        comments = List(1) {
            Comment(
                id = "",
                author = User(
                    _id = 0L,
                    fullName = "Full Name",
                    photo = null,
                    bigPhoto = null,
                    username = "username"
                ),
                text = "This is comment text",
                postDateTime = Date(),
                deleteDate = null
            )
        }
    )
}