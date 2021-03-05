package io.eugenethedev.taigamobile.ui.screens.commontask

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.ui.components.*
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.NavigateToTask
import io.eugenethedev.taigamobile.ui.utils.navigateToTaskScreen
import io.eugenethedev.taigamobile.ui.utils.subscribeOnError
import java.util.*

@Composable
fun CommonTaskScreen(
    navController: NavController,
    commonTaskId: Long,
    commonTaskType: CommonTaskType,
    ref: Int,
    projectSlug: String,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: CommonTaskViewModel = viewModel()
    remember {
        viewModel.start(commonTaskId, commonTaskType)
    }

    val story by viewModel.story.observeAsState()
    story?.subscribeOnError(onError)
    val creator by viewModel.creator.observeAsState()
    creator?.subscribeOnError(onError)
    val assignees by viewModel.assignees.observeAsState()
    assignees?.subscribeOnError(onError)
    val watchers by viewModel.watchers.observeAsState()
    watchers?.subscribeOnError(onError)
    val tasks by viewModel.tasks.observeAsState()
    tasks?.subscribeOnError(onError)
    val comments by viewModel.comments.observeAsState()
    comments?.subscribeOnError(onError)

    val isLoading by viewModel.isLoading.observeAsState()

    story?.data.let {
        CommonTaskScreenContent(
            toolbarTitle = stringResource(
                when (commonTaskType) {
                    CommonTaskType.USERSTORY -> R.string.userstory_slug
                    CommonTaskType.TASK -> R.string.task_slug
                }
            ).format(ref),
            statusName = it?.status?.name ?: "",
            statusColorHex = it?.status?.color ?: "#000000",
            sprintName = it?.sprint?.name,
            storyTitle = it?.title ?: "",
            story = it?.userStoryShortInfo,
            epics = it?.epics.orEmpty(),
            description = it?.description ?: "",
            creationDateTime = it?.createdDateTime ?: Date(),
            creator = creator?.data,
            assignees = assignees?.data.orEmpty(),
            watchers = watchers?.data.orEmpty(),
            tasks = tasks?.data.orEmpty(),
            comments = comments?.data.orEmpty(),
            isLoading = isLoading == true,
            navigateBack = navController::popBackStack,
            navigateToTask = navController::navigateToTaskScreen
        )
    }

}

@Composable
fun CommonTaskScreenContent(
    toolbarTitle: String,
    statusName: String,
    statusColorHex: String,
    sprintName: String?,
    storyTitle: String,
    epics: List<Epic> = emptyList(),
    story: UserStoryShortInfo?,
    description: String,
    creationDateTime: Date,
    creator: User?,
    assignees: List<User> = emptyList(),
    watchers: List<User> = emptyList(),
    tasks: List<CommonTask> = emptyList(),
    comments: List<Comment> = emptyList(),
    isLoading: Boolean = false,
    navigateBack: () -> Unit = {},
    navigateToTask: NavigateToTask = { _, _, _, _ -> }
) = Column(Modifier.fillMaxSize()) {
    AppBarWithBackButton(
        title = {
            Text(
                text = toolbarTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigateBack = navigateBack
    )

    if (isLoading || creator == null) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Loader()
        }
    } else {
        val sectionsMargin = 8.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = mainHorizontalScreenPadding)
        ) {

            item {
                Row {
                    DropdownSelector(
                        text = statusName,
                        colorHex = statusColorHex,
                    )

                    Spacer(Modifier.width(8.dp))

                    sprintName?.also {
                        DropdownSelector(
                            text = it,
                            color = MaterialTheme.colors.primary
                        )
                    } ?: run {
                        DropdownSelector(
                            text = stringResource(R.string.no_sprint),
                            color = Color.Gray
                        )
                    }
                }

                Text(
                    text = storyTitle,
                    style = MaterialTheme.typography.h5
                )

                Spacer(Modifier.height(4.dp))
            }

            if (epics.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.belongs_to),
                        style = MaterialTheme.typography.subtitle1
                    )
                }

                items(epics) {
                    EpicItem(it)
                    Spacer(Modifier.height(2.dp))
                }
            }

            story?.let {
                item {
                    Text(
                        text = stringResource(R.string.belongs_to),
                        style = MaterialTheme.typography.subtitle1
                    )

                    UserStoryItem(story)
                }
            }

            item {
                Spacer(Modifier.height(sectionsMargin * 2))

                if (description.isNotEmpty()) {
                    Text(description)
                } else {
                    NothingToSeeHereText()
                }

                Spacer(Modifier.height(sectionsMargin * 2))

                Text(
                    text = stringResource(R.string.created_by),
                    style = MaterialTheme.typography.subtitle1
                )

                UserItem(
                    user = creator,
                    dateTime = creationDateTime
                )
            }

            if (assignees.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(sectionsMargin))

                    Text(
                        text = stringResource(R.string.assigned_to),
                        style = MaterialTheme.typography.subtitle1
                    )
                }

                items(assignees) {
                    UserItem(it)
                    Spacer(Modifier.height(6.dp))
                }
            }

            if (watchers.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(sectionsMargin))

                    Text(
                        text = stringResource(R.string.watchers),
                        style = MaterialTheme.typography.subtitle1
                    )
                }

                items(watchers) {
                    UserItem(it)
                    Spacer(Modifier.height(6.dp))
                }
            }

            if (tasks.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(sectionsMargin * 2))

                    Text(
                        text = stringResource(R.string.tasks),
                        style = MaterialTheme.typography.h6
                    )
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

            if (comments.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(sectionsMargin * 2))

                    Text(
                        text = stringResource(R.string.comments),
                        style = MaterialTheme.typography.h6
                    )

                    Spacer(Modifier.height(4.dp))
                }

                items(comments) {
                    CommentItem(it)
                    Spacer(Modifier.height(10.dp))
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun EpicItem(
    epic: Epic
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(bottom = 4.dp)
) {
    Text(
        text = stringResource(R.string.title_with_ref_pattern).format(epic.ref, epic.title),
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.subtitle1
    )
    Spacer(Modifier.width(4.dp))
    Text(
        text = stringResource(R.string.epic),
        color = Color.White,
        modifier = Modifier
            .background(
                color = Color(android.graphics.Color.parseColor(epic.color)),
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 2.dp)
    )
}

@Composable
private fun UserStoryItem(
    story: UserStoryShortInfo
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(bottom = 4.dp)
) {
    Text(
        text = stringResource(R.string.title_with_ref_pattern).format(story.ref, story.title),
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.subtitle1
    )
    Spacer(Modifier.width(4.dp))

    story.epicColor?.let {
        Spacer(
            Modifier.size(12.dp)
                .background(
                    color = Color(android.graphics.Color.parseColor(it)),
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun CommentItem(
    comment: Comment
) = Column {
    UserItem(
        user = comment.author,
        dateTime = comment.postDateTime
    )

    Text(
        text = comment.text,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CommonTaskScreenPreview() = TaigaMobileTheme {
    CommonTaskScreenContent(
        toolbarTitle = "617 - User story #99",
        statusName = "In progress",
        statusColorHex = "#729fcf",
        sprintName = "Very very very long sprint name",
        storyTitle = "Very cool and important story. Need to do this quickly",
        story = null,
        epics = List(2) {
            Epic(
                id = 1L,
                title = "Important epic",
                ref = 1,
                color = "#F2C94C"
            )
        },
        description = "Some description about this wonderful task",
        creationDateTime = Date(),
        creator = User(
            id = 0L,
            fullName = "Full Name",
            avatarUrl = null
        ),
        assignees = List(2) {
            User(
                id = 0L,
                fullName = "Full Name",
                avatarUrl = null
            )
        },
        watchers = List(2) {
            User(
                id = 0L,
                fullName = "Full Name",
                avatarUrl = null
            )
        },
        tasks = List(4) {
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
                taskType = CommonTaskType.USERSTORY
            )
        },
        comments = List(4) {
            Comment(
                id = "",
                author = User(
                    id = 0L,
                    fullName = "Full Name",
                    avatarUrl = null
                ),
                text = "This is comment text",
                postDateTime = Date()
            )
        }
    )
}