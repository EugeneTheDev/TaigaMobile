package io.eugenethedev.taigamobile.ui.screens.story

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.ui.components.*
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import java.util.*

@Composable
fun StoryScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {},
) {

}

@Composable
fun StoryScreenContent(
    toolbarTitle: String,
    status: Status,
    sprintName: String?,
    storyTitle: String,
    epics: List<Epic> = emptyList(),
    description: String,
    creationDateTime: Date,
    creator: User,
    assignees: List<User> = emptyList(),
    watchers: List<User> = emptyList(),
    tasks: List<CommonTask> = emptyList(),
    comments: List<Comment> = emptyList(),
    isLoading: Boolean = false,
    navigateBack: () -> Unit = {}
) = Column(Modifier.fillMaxSize()) {
    AppBarWithBackButton(
        title = {
            Text(
                text = toolbarTitle,
                maxLines = 1
            )
        },
        navigateBack = navigateBack
    )

    if (isLoading) {
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
                    DropdownSelector(text = status.name, colorHex = status.color)
                    Spacer(Modifier.width(8.dp))
                    sprintName?.also {
                        DropdownSelector(text = it, color = MaterialTheme.colors.primary)
                    } ?: run {
                        DropdownSelector(text = stringResource(R.string.no_sprint), color = Color.Gray)
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
                    Spacer(Modifier.height(6.dp))
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

                items(tasks) {
                    CommonTaskItem(
                        commonTask = it,
                        horizontalPadding = 0.dp,
                        verticalPadding = 4.dp
                    )

                    Spacer(Modifier.height(6.dp))
                }
            }

            if (comments.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(sectionsMargin * 2))

                    Text(
                        text = stringResource(R.string.comments),
                        style = MaterialTheme.typography.h6
                    )
                }

                items(comments) {
                    CommentItem(it)
                    Spacer(Modifier.height(6.dp))
                }
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
private fun CommentItem(
    comment: Comment
) = Column {
    UserItem(
        user = comment.author,
        dateTime = comment.postDateTime
    )

    Text(comment.text)
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun StoryScreenPreview() = TaigaMobileTheme {
    StoryScreenContent(
        toolbarTitle = "617 - User story #99",
        status = Status(
            id = 1L,
            name = "In progress",
            color = "#729fcf"
        ),
        sprintName = "0 sprint",
        storyTitle = "Very cool and important story. Need to do this quickly",
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
                )
            )
        },
        comments = List(4) {
            Comment(
                id = 0L,
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