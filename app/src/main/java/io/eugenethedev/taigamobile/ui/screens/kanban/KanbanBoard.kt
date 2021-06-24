package io.eugenethedev.taigamobile.ui.screens.kanban

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.glide.rememberGlidePainter
import com.google.accompanist.insets.navigationBarsHeight
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.ui.components.buttons.PlusButton
import io.eugenethedev.taigamobile.ui.components.texts.TitleWithIndicators
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.veryLightGray
import io.eugenethedev.taigamobile.ui.utils.safeParseHexColor
import java.time.LocalDateTime

@Composable
fun KanbanBoard(
    statuses: List<Status>,
    stories: List<CommonTaskExtended> = emptyList(),
    team: List<User> = emptyList(),
    navigateToStory: (id: Long, ref: Int) -> Unit = { _, _ -> },
    navigateToCreateTask: (statusId: Long) -> Unit = { _ -> }
) {
    val cellOuterPadding = 8.dp
    val cellPadding = 8.dp
    val cellWidth = 280.dp
    val backgroundCellColor = veryLightGray

    Row(
        Modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState())
    ) {

       Spacer(Modifier.width(cellPadding))

        statuses.forEach { status ->
            val statusStories = stories.filter { it.status == status }

            Column {
                Header(
                    text = status.name,
                    storiesCount = statusStories.size,
                    cellWidth = cellWidth,
                    cellOuterPadding = cellOuterPadding,
                    stripeColor = safeParseHexColor(status.color),
                    backgroundColor = backgroundCellColor,
                    onAddClick = { navigateToCreateTask(status.id) }
                )

                LazyColumn(
                    Modifier
                        .fillMaxHeight()
                        .width(cellWidth)
                        .background(backgroundCellColor)
                        .padding(cellPadding)
                ) {
                    items(statusStories) {
                        StoryItem(
                            story = it,
                            assignees = it.assignedIds.mapNotNull { id -> team.find { it.id == id } },
                            onTaskClick = { navigateToStory(it.id, it.ref) }
                        )
                    }

                    item {
                        Spacer(Modifier.navigationBarsHeight(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(
    text: String,
    storiesCount: Int,
    cellWidth: Dp,
    cellOuterPadding: Dp,
    stripeColor: Color,
    backgroundColor: Color,
    onAddClick: () -> Unit
) = Row(
    modifier = Modifier
        .padding(end = cellOuterPadding, bottom = cellOuterPadding)
        .width(cellWidth)
        .background(
            color = backgroundColor.copy(alpha = 0.2f),
            shape = MaterialTheme.shapes.medium.copy(
                bottomStart = CornerSize(0.dp),
                bottomEnd = CornerSize(0.dp)
            )
        ),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
) {
    val textStyle = MaterialTheme.typography.subtitle1

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(0.8f, fill = false)
    ) {
        Spacer(
            Modifier
                .padding(start = 10.dp)
                .size(
                    width = 10.dp,
                    height = with(LocalDensity.current) { textStyle.fontSize.toDp() + 2.dp }
                )
                .background(stripeColor)
        )

        Text(
            text = stringResource(R.string.status_with_number_template).format(
                text.uppercase(), storiesCount
            ),
            style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(8.dp)
        )
    }

    PlusButton(
        tint = Color.Gray,
        onClick = onAddClick,
        modifier = Modifier.weight(0.2f)
    )
}

@Composable
private fun StoryItem(
    story: CommonTaskExtended,
    assignees: List<User>,
    onTaskClick: () -> Unit
) = Surface(
    modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp),
    shape = MaterialTheme.shapes.medium,
    elevation = 8.dp
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = true,
                    color = MaterialTheme.colors.primary
                ),
                onClick = onTaskClick
            )
            .padding(12.dp)
    ) {
        story.epicsShortInfo.forEach {
            val textStyle = MaterialTheme.typography.caption
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(
                    Modifier
                        .size(with(LocalDensity.current) { textStyle.fontSize.toDp() })
                        .background(safeParseHexColor(it.color), CircleShape)
                )

                Spacer(Modifier.width(4.dp))

                Text(
                    text = it.title,
                    style = textStyle
                )
            }

            Spacer(Modifier.height(4.dp))
        }

        Spacer(Modifier.height(4.dp))

        TitleWithIndicators(
            ref = story.ref,
            title = story.title,
            isInactive = story.isClosed
        )

        Spacer(Modifier.height(8.dp))

        FlowRow {
            assignees.forEach {
                Image(
                    painter = rememberGlidePainter(
                        request = it.avatarUrl ?: R.drawable.default_avatar,
                        fadeIn = true,
                        requestBuilder = { error(R.drawable.default_avatar) }
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(end = 4.dp, bottom = 4.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .weight(0.2f, fill = false)
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun KanbanBoardPreview() = TaigaMobileTheme {
    KanbanBoard(
        statuses = listOf(
            Status(
                id = 0,
                name = "New",
                color = "#70728F",
                type = StatusType.Status
            ),
            Status(
                id = 1,
                name = "In progress",
                color = "#E47C40",
                type = StatusType.Status
            ),
            Status(
                id = 1,
                name = "Done",
                color = "#A8E440",
                type = StatusType.Status
            ),
            Status(
                id = 1,
                name = "Archived",
                color = "#A9AABC",
                type = StatusType.Status
            ),
        ),
        stories = List(5) {
            CommonTaskExtended(
                id = 0,
                status = Status(
                    id = 1,
                    name = "In progress",
                    color = "#E47C40",
                    type = StatusType.Status
                ),
                createdDateTime = LocalDateTime.now(),
                sprint = null,
                assignedIds = List(10) { it.toLong() },
                watcherIds = emptyList(),
                creatorId = 0,
                ref = 1,
                title = "Sample title",
                isClosed = false,
                description = "",
                epicsShortInfo = List(3) { EpicShortInfo(0, "Some title", 1, "#A8E440") },
                projectSlug = "",
                userStoryShortInfo = null,
                version = 0,
                color = null,
                type = null,
                priority = null,
                severity = null
            )
        },
        team = List(10) {
            User(
                _id = it.toLong(),
                fullName = "Name Name",
                photo = "https://avatars.githubusercontent.com/u/36568187?v=4",
                bigPhoto = null,
                username = "username"
            )
        }
    )
}
