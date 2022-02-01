package io.eugenethedev.taigamobile.ui.screens.commontask

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
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
import io.eugenethedev.taigamobile.ui.components.editors.TaskEditor
import io.eugenethedev.taigamobile.ui.components.lists.SimpleTasksListWithTitle
import io.eugenethedev.taigamobile.ui.components.loaders.CircularLoader
import io.eugenethedev.taigamobile.ui.components.dialogs.LoadingDialog
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
    LaunchedEffect(Unit) {
        viewModel.onOpen(commonTaskId, commonTaskType)
    }

    val commonTask by viewModel.commonTask.collectAsState()
    commonTask.subscribeOnError(onError)

    val creator by viewModel.creator.collectAsState()
    creator.subscribeOnError(onError)

    val assignees by viewModel.assignees.collectAsState()
    assignees.subscribeOnError(onError)

    val watchers by viewModel.watchers.collectAsState()
    watchers.subscribeOnError(onError)

    val userStories by viewModel.userStories.collectAsState()
    userStories.subscribeOnError(onError)

    val tasks by viewModel.tasks.collectAsState()
    tasks.subscribeOnError(onError)

    val comments by viewModel.comments.collectAsState()
    comments.subscribeOnError(onError)

    val statuses by viewModel.statuses.collectAsState()
    statuses.subscribeOnError(onError)
    val statusSelectResult by viewModel.statusSelectResult.collectAsState()
    statusSelectResult.subscribeOnError(onError)

    val swimlanes by viewModel.swimlanes.collectAsState()
    swimlanes.subscribeOnError(onError)

    val sprints = viewModel.sprints
    sprints.subscribeOnError(onError)
    val selectSprintResult by viewModel.selectSprintResult.collectAsState()
    selectSprintResult.subscribeOnError(onError)

    val epics = viewModel.epics
    epics.subscribeOnError(onError)
    val linkToEpicResult by viewModel.linkToEpicResult.collectAsState()
    linkToEpicResult.subscribeOnError(onError)

    val team by viewModel.team.collectAsState()
    team.subscribeOnError(onError)
    val teamSearched by viewModel.teamSearched.collectAsState()

    val customFields by viewModel.customFields.collectAsState()
    customFields.subscribeOnError(onError)

    val attachments by viewModel.attachments.collectAsState()
    attachments.subscribeOnError(onError)

    val tags by viewModel.tags.collectAsState()
    tags.subscribeOnError(onError)
    val tagsSearched by viewModel.tagsSearched.collectAsState()

    val colorResult by viewModel.colorResult.collectAsState()
    colorResult.subscribeOnError(onError)

    val dueDateResult by viewModel.dueDateResult.collectAsState()
    dueDateResult.subscribeOnError(onError)

    val editResult by viewModel.editResult.collectAsState()
    editResult.subscribeOnError(onError)

    val deleteResult by viewModel.deleteResult.collectAsState()
    deleteResult.subscribeOnError(onError)
    deleteResult.takeIf { it is SuccessResult }?.let {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
    }

    val promoteResult by viewModel.promoteResult.collectAsState()
    promoteResult.subscribeOnError(onError)
    promoteResult.takeIf { it is SuccessResult }?.data?.let {
        LaunchedEffect(Unit) {
            navController.popBackStack()
            navController.navigateToTaskScreen(it.id, CommonTaskType.UserStory, it.ref)
        }
    }

    fun makeEditStatusAction(statusType: StatusType) = EditAction(
        items = statuses.data?.get(statusType).orEmpty(),
        selectItem = viewModel::selectStatus,
        isResultLoading = statusSelectResult.let { (it as? LoadingResult)?.data == statusType }
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
        toolbarSubtitle = viewModel.getCurrentProjectName(),
        commonTask = commonTask.data,
        creator = creator.data,
        customFields = customFields.data?.fields.orEmpty(),
        attachments = attachments.data.orEmpty(),
        assignees = assignees.data.orEmpty(),
        watchers = watchers.data.orEmpty(),
        userStories = userStories.data.orEmpty(),
        tasks = tasks.data.orEmpty(),
        comments = comments.data.orEmpty(),
        editActions = EditActions(
            editStatus = makeEditStatusAction(StatusType.Status),
            editType = makeEditStatusAction(StatusType.Type),
            editSeverity = makeEditStatusAction(StatusType.Severity),
            editPriority = makeEditStatusAction(StatusType.Priority),
            editSwimlane = EditAction(
                items = swimlanes.data.orEmpty(),
                selectItem = viewModel::selectSwimlane,
                isResultLoading = swimlanes is LoadingResult
            ),
            editSprint = EditAction(
                itemsLazy = sprints,
                selectItem = viewModel::selectSprint,
                isResultLoading = selectSprintResult is LoadingResult
            ),
            editEpics = EditAction(
                itemsLazy = epics,
                searchItems = viewModel::searchEpics,
                selectItem = viewModel::linkToEpic,
                isResultLoading = linkToEpicResult is LoadingResult,
                removeItem = {
                    // Since epic structure in CommonTaskExtended differs from what is used in edit there is separate lambda
                }
            ),
            unlinkFromEpic = viewModel::unlinkFromEpic,
            editAttachments = EditAttachmentsAction(
                deleteAttachment = viewModel::deleteAttachment,
                addAttachment = viewModel::addAttachment,
                isResultLoading = attachments is LoadingResult
            ),
            editAssignees = EditAction(
                items = teamSearched,
                searchItems = viewModel::searchTeam,
                selectItem = viewModel::addAssignee,
                isResultLoading = assignees is LoadingResult,
                removeItem = viewModel::removeAssignee
            ),
            editWatchers = EditAction(
                items = teamSearched,
                searchItems = viewModel::searchTeam,
                selectItem = viewModel::addWatcher,
                isResultLoading = watchers is LoadingResult,
                removeItem = viewModel::removeWatcher
            ),
            editComments = EditCommentsAction(
                createComment = viewModel::createComment,
                deleteComment = viewModel::deleteComment,
                isResultLoading = comments is LoadingResult
            ),
            editTask = viewModel::editTask,
            deleteTask = viewModel::deleteTask,
            promoteTask = viewModel::promoteToUserStory,
            editCustomField = viewModel::editCustomField,
            editTags = EditAction(
                items = tagsSearched,
                searchItems = viewModel::searchTags,
                selectItem = viewModel::addTag,
                removeItem = viewModel::deleteTag,
                isResultLoading = tags is LoadingResult
            ),
            editDueDate = EditSimple(
                select = viewModel::selectDueDate,
                isResultLoading = dueDateResult is LoadingResult
            ),
            editEpicColor = EditSimple(
                select = viewModel::selectEpicColor,
                isResultLoading = colorResult is LoadingResult
            ),
            assigneeMe = EditSimpleEmpty(
                select = viewModel::assigneeMe,
                isResultLoading = assignees is LoadingResult,
            ),
            watchMe = EditSimpleEmpty(
                select = viewModel::watchMe,
                isResultLoading = watchers is LoadingResult,
            ),
            checkAssigneeToMe = viewModel::checkAssigneeMe,
            checkWatchingMe = viewModel::checkWatchingMe
        ),
        loaders = Loaders(
            isLoading = commonTask is LoadingResult,
            isEditLoading = editResult is LoadingResult,
            isDeleteLoading = deleteResult is LoadingResult,
            isPromoteLoading = promoteResult is LoadingResult,
            isCustomFieldsLoading = customFields is LoadingResult
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
    toolbarSubtitle: String,
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
    var isSwimlaneSelectorVisible by remember { mutableStateOf(false) }

    var customFieldsValues by remember { mutableStateOf(emptyMap<Long, CustomFieldValue?>()) }
    customFieldsValues =
        customFields.map { it.id to (if (it.id in customFieldsValues) customFieldsValues[it.id] else it.value) }.toMap()

    Column(Modifier.fillMaxSize()) {
        CommonTaskAppBar(
            toolbarTitle = toolbarTitle,
            toolbarSubtitle = toolbarSubtitle,
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
                        showSwimlaneSelector = { isSwimlaneSelectorVisible = true }
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

                    if (commonTaskType != CommonTaskType.Epic) {
                        CommonTaskDueDate(
                            commonTask = commonTask,
                            editActions = editActions
                        )

                        item {
                            Spacer(Modifier.height(sectionsPadding))
                        }
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
                        Spacer(
                            Modifier
                                .navigationBarsWithImePadding()
                                .height(72.dp)
                        )
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
        ),
        swimlaneEntry = SelectorEntry(
            edit = editActions.editSwimlane,
            isVisible = isSwimlaneSelectorVisible,
            hide = { isSwimlaneSelectorVisible = false }
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
            toolbarSubtitle =  "Project #228",
            commonTask = null, // TODO left it null for now since I do not really use this preview
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
