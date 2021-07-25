package io.eugenethedev.taigamobile.ui.screens.commontask

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.insets.navigationBarsWithImePadding
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.domain.entities.CustomField
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import io.eugenethedev.taigamobile.ui.components.editors.TaskEditor
import io.eugenethedev.taigamobile.ui.components.lists.SimpleTasksListWithTitle
import io.eugenethedev.taigamobile.ui.components.loaders.CircularLoader
import io.eugenethedev.taigamobile.ui.components.loaders.LoadingDialog
import io.eugenethedev.taigamobile.ui.screens.commontask.components.*
import io.eugenethedev.taigamobile.ui.screens.main.FilePicker
import io.eugenethedev.taigamobile.ui.screens.main.LocalFilePicker
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.*
import java.time.LocalDateTime

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

    val epics by viewModel.epics.observeAsState()
    epics?.subscribeOnError(onError)

    val team by viewModel.team.observeAsState()
    team?.subscribeOnError(onError)

    val customFields by viewModel.customFields.observeAsState()
    customFields?.subscribeOnError(onError)

    val attachments by viewModel.attachments.observeAsState()
    attachments?.subscribeOnError(onError)

    val tags by viewModel.tags.observeAsState()
    tags?.subscribeOnError(onError)

    val editResult by viewModel.editResult.observeAsState()
    editResult?.subscribeOnError(onError)

    val deleteResult by viewModel.deleteResult.observeAsState()
    deleteResult?.subscribeOnError(onError)
    deleteResult?.takeIf { it.resultStatus == ResultStatus.Success }?.let {
        navController.popBackStack()
    }

    val promoteResult by viewModel.promoteResult.observeAsState()
    promoteResult?.subscribeOnError(onError)
    promoteResult?.takeIf { it.resultStatus == ResultStatus.Success }?.data?.let {
        navController.popBackStack()
        navController.navigateToTaskScreen(it.id, CommonTaskType.UserStory, it.ref)
    }
    
    fun makeEditStatusAction(statusType: StatusType) = EditAction(
        items = statuses?.data.orEmpty(),
        isItemsLoading = statuses?.resultStatus == ResultStatus.Loading,
        selectItem = viewModel::selectStatus,
        isResultLoading = statusSelectResult?.let { it.data == statusType && it.resultStatus == ResultStatus.Loading } ?: false
    )


    CommonTaskScreenContent(
        commonTaskType = commonTaskType,
        toolbarTitle = stringResource(
            when (commonTaskType) {
                CommonTaskType.UserStory -> R.string.userstory_slug
                CommonTaskType.Task -> R.string.task_slug
                CommonTaskType.Epic -> R.string.epic_slug
                CommonTaskType.Issue -> R.string.issue_slug
            }
        ).format(ref),
        commonTask = commonTask?.data,
        creator = creator?.data,
        customFields = customFields?.data?.fields.orEmpty(),
        attachments = attachments?.data.orEmpty(),
        assignees = assignees?.data.orEmpty(),
        watchers = watchers?.data.orEmpty(),
        userStories = userStories?.data.orEmpty(),
        tasks = tasks?.data.orEmpty(),
        comments = comments?.data.orEmpty(),
        editActions = EditActions(
            editStatus = makeEditStatusAction(StatusType.Status),
            editType = makeEditStatusAction(StatusType.Type),
            editSeverity = makeEditStatusAction(StatusType.Severity),
            editPriority = makeEditStatusAction(StatusType.Priority),
            loadStatuses = { viewModel.loadStatuses(it) },
            editSprint = EditAction(
                items = sprints?.data.orEmpty(),
                loadItems = viewModel::loadSprints,
                isItemsLoading = sprints?.resultStatus == ResultStatus.Loading,
                selectItem = viewModel::selectSprint,
                isResultLoading = sprints?.resultStatus == ResultStatus.Loading
            ),
            editEpics = EditAction(
                items = epics?.data.orEmpty(),
                loadItems = viewModel::loadEpics,
                isItemsLoading = epics?.resultStatus == ResultStatus.Loading,
                selectItem = viewModel::linkToEpic,
                isResultLoading = epics?.resultStatus == ResultStatus.Loading,
                removeItem = {
                    // Since epic structure in CommonTaskExtended differs from what is used in edit there is separate lambda
                }
            ),
            unlinkFromEpic = viewModel::unlinkFromEpic,
            editAttachments = EditAttachmentsAction(
                deleteAttachment = viewModel::deleteAttachment,
                addAttachment = viewModel::addAttachment,
                isResultLoading = attachments?.resultStatus == ResultStatus.Loading
            ),
            editAssignees = EditAction(
                items = team?.data.orEmpty(),
                loadItems = viewModel::loadTeam,
                isItemsLoading = team?.resultStatus == ResultStatus.Loading,
                selectItem = viewModel::addAssignee,
                isResultLoading = assignees?.resultStatus == ResultStatus.Loading,
                removeItem = viewModel::removeAssignee
            ),
            editWatchers = EditAction(
                items = team?.data.orEmpty(),
                loadItems = viewModel::loadTeam,
                isItemsLoading = team?.resultStatus == ResultStatus.Loading,
                selectItem = viewModel::addWatcher,
                isResultLoading = watchers?.resultStatus == ResultStatus.Loading,
                removeItem = viewModel::removeWatcher
            ),
            editComments = EditCommentsAction(
                createComment = viewModel::createComment,
                deleteComment = viewModel::deleteComment,
                isResultLoading = comments?.resultStatus == ResultStatus.Loading
            ),
            editTask = viewModel::editTask,
            deleteTask = viewModel::deleteTask,
            promoteTask = viewModel::promoteToUserStory,
            editCustomField = viewModel::editCustomField,
            editTags = EditAction(
                items = tags?.data.orEmpty(),
                loadItems = viewModel::loadTags,
                selectItem = viewModel::addTag,
                removeItem = viewModel::deleteTag,
                isResultLoading = tags?.resultStatus == ResultStatus.Loading
            )
        ),
        loaders = Loaders(
            isLoading = commonTask?.resultStatus == ResultStatus.Loading,
            isEditLoading = editResult?.resultStatus == ResultStatus.Loading,
            isDeleteLoading = deleteResult?.resultStatus == ResultStatus.Loading,
            isPromoteLoading = promoteResult?.resultStatus == ResultStatus.Loading,
            isCustomFieldsLoading = customFields?.resultStatus == ResultStatus.Loading
        ),
        navigationActions = NavigationActions(
            navigateBack = navController::popBackStack,
            navigateToCreateTask = { navController.navigateToCreateTaskScreen(CommonTaskType.Task, commonTaskId) },
            navigateToTask = navController::navigateToTaskScreen
        )
    )
}

@Composable
fun CommonTaskScreenContent(
    commonTaskType: CommonTaskType,
    toolbarTitle: String,
    commonTask: CommonTaskExtended?,
    creator: User?,
    customFields: List<CustomField> = emptyList(),
    attachments: List<Attachment> = emptyList(),
    assignees: List<User> = emptyList(),
    watchers: List<User> = emptyList(),
    userStories: List<CommonTask> = emptyList(),
    tasks: List<CommonTask> = emptyList(),
    comments: List<Comment> = emptyList(),
    editActions: EditActions = EditActions(),
    loaders: Loaders = Loaders(),
    navigationActions: NavigationActions = NavigationActions()
) = Box(Modifier.fillMaxSize()) {
    var isTaskEditorVisible by remember { mutableStateOf(false) }

    var isStatusSelectorVisible by remember { mutableStateOf(false) }
    var isTypeSelectorVisible by remember { mutableStateOf(false) }
    var isSeveritySelectorVisible by remember { mutableStateOf(false) }
    var isPrioritySelectorVisible by remember { mutableStateOf(false) }
    var isSprintSelectorVisible by remember { mutableStateOf(false) }
    var isAssigneesSelectorVisible by remember { mutableStateOf(false) }
    var isWatchersSelectorVisible by remember { mutableStateOf(false) }
    var isEpicsSelectorVisible by remember { mutableStateOf(false) }


    var customFieldsValues by remember { mutableStateOf(emptyMap<Long, CustomFieldValue?>()) }
    customFieldsValues = customFields.map { it.id to (if (it.id in customFieldsValues) customFieldsValues[it.id] else it.value) }.toMap()

    Column(Modifier.fillMaxSize()) {
        CommonTaskAppBar(
            toolbarTitle = toolbarTitle,
            commonTaskType = commonTaskType,
            showTaskEditor = { isTaskEditorVisible = true },
            editActions = editActions,
            navigationActions = navigationActions
        )

        if (loaders.isLoading || creator == null || commonTask == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularLoader()
            }
        } else {
            val sectionsPadding = 16.dp

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.BottomCenter
            ) {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = mainHorizontalScreenPadding)
                ) {

                    CommonTaskHeader(
                        commonTask = commonTask,
                        editActions = editActions,
                        showStatusSelector = { isStatusSelectorVisible = true },
                        showSprintSelector = { isSprintSelectorVisible = true },
                        showTypeSelector = { isTypeSelectorVisible = true },
                        showSeveritySelector = { isSeveritySelectorVisible = true },
                        showPrioritySelector = { isPrioritySelectorVisible = true },
                    )

                    CommonTaskBelongsTo(
                        commonTask = commonTask,
                        navigationActions = navigationActions,
                        editActions = editActions,
                        showEpicsSelector = { isEpicsSelectorVisible = true }
                    )

                    item {
                        Spacer(Modifier.height(sectionsPadding))
                    }

                    CommonTaskDescription(commonTask)

                    item {
                        Spacer(Modifier.height(sectionsPadding))
                    }

                    CommonTaskTags(
                        commonTask = commonTask,
                        editActions = editActions
                    )

                    item {
                        Spacer(Modifier.height(sectionsPadding))

                    }

                    CommonTaskCreatedBy(
                        creator = creator,
                        commonTask = commonTask
                    )

                    item {
                        Spacer(Modifier.height(sectionsPadding))
                    }

                    CommonTaskAssignees(
                        assignees = assignees,
                        editActions = editActions,
                        showAssigneesSelector = { isAssigneesSelectorVisible = true }
                    )

                    item {
                        Spacer(Modifier.height(sectionsPadding))
                    }

                    CommonTaskWatchers(
                        watchers = watchers,
                        editActions = editActions,
                        showWatchersSelector = { isWatchersSelectorVisible = true }
                    )

                    item {
                        Spacer(Modifier.height(sectionsPadding * 2))
                    }

                    if (customFields.isNotEmpty()) {
                        CommonTaskCustomFields(
                            customFields = customFields,
                            customFieldsValues = customFieldsValues,
                            onValueChange = { itemId, value -> customFieldsValues = customFieldsValues - itemId + Pair(itemId, value) },
                            editActions = editActions,
                            loaders = loaders
                        )

                        item {
                            Spacer(Modifier.height(sectionsPadding * 3))
                        }
                    }

                    CommonTaskAttachments(
                        attachments = attachments,
                        editActions = editActions
                    )

                    item {
                        Spacer(Modifier.height(sectionsPadding))
                    }

                    // user stories
                    if (commonTaskType == CommonTaskType.Epic) {
                        SimpleTasksListWithTitle(
                            titleText = R.string.userstories,
                            bottomPadding = sectionsPadding,
                            commonTasks = userStories,
                            navigateToTask = navigationActions.navigateToTask
                        )
                    }

                    // tasks
                    if (commonTaskType == CommonTaskType.UserStory) {
                        SimpleTasksListWithTitle(
                            titleText = R.string.tasks,
                            bottomPadding = sectionsPadding,
                            commonTasks = tasks,
                            navigateToTask = navigationActions.navigateToTask,
                            navigateToCreateCommonTask = navigationActions.navigateToCreateTask
                        )
                    }

                    item {
                        Spacer(Modifier.height(sectionsPadding))
                    }

                    CommonTaskComments(
                        comments = comments,
                        editActions = editActions
                    )

                    item {
                        Spacer(Modifier.navigationBarsWithImePadding().height(72.dp))
                    }
                }

                CreateCommentBar(editActions.editComments.createComment)
            }
        }
    }


    // Bunch of list selectors
    Selectors(
        statusEntry = SelectorEntry(
            edit = editActions.editStatus,
            isVisible = isStatusSelectorVisible,
            hide = { isStatusSelectorVisible = false }
        ),
        typeEntry = SelectorEntry(
            edit = editActions.editType,
            isVisible = isTypeSelectorVisible,
            hide = { isTypeSelectorVisible = false }
        ),
        severityEntry = SelectorEntry(
            edit = editActions.editSeverity,
            isVisible = isSeveritySelectorVisible,
            hide = { isSeveritySelectorVisible = false }
        ),
        priorityEntry = SelectorEntry(
            edit = editActions.editPriority,
            isVisible = isPrioritySelectorVisible,
            hide = { isPrioritySelectorVisible = false }
        ),
        sprintEntry = SelectorEntry(
            edit = editActions.editSprint,
            isVisible = isSprintSelectorVisible,
            hide = { isSprintSelectorVisible = false }
        ),
        epicsEntry = SelectorEntry(
            edit = editActions.editEpics,
            isVisible = isEpicsSelectorVisible,
            hide = { isEpicsSelectorVisible = false }
        ),
        assigneesEntry = SelectorEntry(
            edit = editActions.editAssignees,
            isVisible = isAssigneesSelectorVisible,
            hide = { isAssigneesSelectorVisible = false }
        ),
        watchersEntry = SelectorEntry(
            edit = editActions.editWatchers,
            isVisible = isWatchersSelectorVisible,
            hide = { isWatchersSelectorVisible = false }
        )
    )

    // Editor
    if (isTaskEditorVisible || loaders.isEditLoading) {
        TaskEditor(
            toolbarText = stringResource(R.string.edit),
            title = commonTask?.title.orEmpty(),
            description = commonTask?.description.orEmpty(),
            onSaveClick = { title, description ->
                isTaskEditorVisible = false
                editActions.editTask(title, description)
            },
            navigateBack = { isTaskEditorVisible = false }
        )
    }

    if (loaders.isEditLoading || loaders.isDeleteLoading || loaders.isPromoteLoading) {
        LoadingDialog()
    }
}

@Preview(showBackground = true)
@Composable
fun CommonTaskScreenPreview() = TaigaMobileTheme {
    CompositionLocalProvider(
        LocalFilePicker provides object : FilePicker() {}
    ) {
        CommonTaskScreenContent(
            commonTaskType = CommonTaskType.UserStory,
            toolbarTitle = "Userstory #99",
            commonTask = null, // TODO left it null for now since I do not really use this preivew
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
                    createdDate = LocalDateTime.now(),
                    title = "Very cool story",
                    ref = 100,
                    status = Status(
                        id = (0..2).random().toLong(),
                        name = "In progress",
                        color = "#729fcf",
                        type = StatusType.Status
                    ),
                    assignee = null,
                    projectInfo = Project(0, "", ""),
                    taskType = CommonTaskType.UserStory,
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
                    postDateTime = LocalDateTime.now(),
                    deleteDate = null
                )
            }
        )
    }
}
