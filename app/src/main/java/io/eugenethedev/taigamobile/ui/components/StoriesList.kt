package io.eugenethedev.taigamobile.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Status
import io.eugenethedev.taigamobile.domain.entities.Story
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.clickableUnindicated
import java.text.SimpleDateFormat
import java.util.*

/**
 * View for displaying list of stories or tasks
 */
@ExperimentalAnimationApi
fun LazyListScope.StoriesList(
    statuses: List<Status>,
    stories: List<Story>,
    loadData: (Status) -> Unit = {},
    loadingStatusIds: List<Long> = emptyList(),
    visibleStatusIds: List<Long> = emptyList(),
    onStatusClick: (Long) -> Unit = {}
) {
    if (statuses.isNotEmpty()) {
        statuses.map { st -> st to stories.filter { it.status.id == st.id } }.forEach { (status, stories) ->
            val isCategoryVisible = status.id in visibleStatusIds
            val isCategoryLoading = status.id in loadingStatusIds

            item {
                Surface(
                    modifier = Modifier
                        .padding(horizontal = mainHorizontalScreenPadding)
                        .padding(top = 12.dp),
                    contentColor = Color(android.graphics.Color.parseColor(status.color))
                ) {
                    val transitionState = remember { MutableTransitionState(isCategoryVisible) }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickableUnindicated {
                            transitionState.targetState = !isCategoryVisible
                            onStatusClick(status.id)
                        }
                    ) {

                        Text(
                            text = status.name.toUpperCase(Locale.getDefault()),
                            style = MaterialTheme.typography.subtitle1
                        )

                        val arrowRotation by updateTransition(transitionState).animateFloat { if (it) -180f else 0f }

                        Image(
                            imageVector = vectorResource(R.drawable.ic_arrow_down),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(AmbientContentColor.current),
                            modifier = Modifier.rotate(arrowRotation)
                        )
                    }
                }

                AnimateExpandVisibility(
                    visible = isCategoryVisible && !isCategoryLoading && stories.isEmpty()
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.nothing_to_see),
                            color = Color.Gray
                        )
                    }
                }
            }

            itemsIndexed(stories.toList()) { index, story ->
                AnimateExpandVisibility(
                    visible = isCategoryVisible
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        StoryItem(story)

                        Divider(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.LightGray
                        )
                    }
                }

                if (index == stories.size - 1) {
                    onActive {
                        loadData(status)
                    }
                }
            }

            item {
                AnimateExpandVisibility(visible = isCategoryVisible && isCategoryLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(Modifier.size(36.dp))
                    }
                }
                if (isCategoryVisible) {
                    Spacer(Modifier.height(12.dp))
                }
            }
        }

    } else {
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.nothing_to_see),
                    color = Color.Gray
                )
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun AnimateExpandVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) = AnimatedVisibility(
    visible = visible,
    enter = expandVertically(),
    exit = shrinkOut(shrinkTowards = Alignment.TopStart),
    modifier = modifier,
    content = content
)


private val dateFormatter = SimpleDateFormat.getDateInstance()

@Composable
fun StoryItem(
    story: Story
) = ContainerBox {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = story.status.name,
                color = Color(android.graphics.Color.parseColor(story.status.color)),
                style = MaterialTheme.typography.body2
            )

            Text(
                text = dateFormatter.format(story.createdDate),
                color = Color.Gray,
                style = MaterialTheme.typography.body2
            )
        }

        Text(
            text = story.title,
            style = MaterialTheme.typography.subtitle1,
        )

        Text(
            text = story.assignee?.fullName?.let { stringResource(R.string.assignee_pattern).format(it) } ?: stringResource(R.string.unassigned),
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.body2
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StoryItemPreview() = TaigaMobileTheme {
    StoryItem(
        Story(
            id = 0L,
            createdDate = Date(),
            title = "Very cool story",
            status = Status(
                id = 0L,
                name = "In progress",
                color = "#729fcf"
            ),
            assignee = Story.Assignee(
                id = 0,
                fullName = "Name Name"
            )
        )
    )
}

@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun StoriesListPreview() = TaigaMobileTheme {
    var visibleStatusIds by remember { mutableStateOf(listOf<Long>()) }

    LazyColumn {
        StoriesList(
            statuses = List(3) {
                Status(
                    id = it.toLong(),
                    name = "In progress",
                    color = "#729fcf"
                )
            },
            stories = List(10) {
                Story(
                    id = it.toLong(),
                    createdDate = Date(),
                    title = "Very cool story",
                    status = Status(
                        id = (0..2).random().toLong(),
                        name = "In progress",
                        color = "#729fcf"
                    ),
                    assignee = Story.Assignee(
                        id = it.toLong(),
                        fullName = "Name Name"
                    )
                )
            },
            visibleStatusIds = visibleStatusIds,
            onStatusClick = {
                visibleStatusIds = if (it in visibleStatusIds) {
                    visibleStatusIds - it
                } else {
                    visibleStatusIds + it
                }
            }

        )
    }
}