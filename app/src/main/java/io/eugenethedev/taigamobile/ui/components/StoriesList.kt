package io.eugenethedev.taigamobile.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
@Composable
fun StoriesList(
    statusesWithStories: Map<Status, List<Story>>
) = Box(
    modifier = Modifier.fillMaxWidth(),
    contentAlignment = Alignment.Center
) {
    var visibleStatusIds by remember { mutableStateOf(setOf<Long>()) }

    if (statusesWithStories.isNotEmpty()) {
        LazyColumn {
            statusesWithStories.forEach { (status, stories) ->
                val isCategoryVisible = status.id in visibleStatusIds

                item {
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = mainHorizontalScreenPadding)
                            .padding(top = 12.dp),
                        contentColor = Color(android.graphics.Color.parseColor(status.color))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val transitionState = remember { MutableTransitionState(isCategoryVisible) }

                            Text(
                                text = status.name.toUpperCase(Locale.getDefault()),
                                style = MaterialTheme.typography.subtitle1,
                                modifier = Modifier.clickableUnindicated {
                                    transitionState.targetState = !isCategoryVisible
                                    visibleStatusIds = if (isCategoryVisible) {
                                        visibleStatusIds - status.id
                                    } else {
                                        visibleStatusIds + status.id
                                    }
                                }

                            )

                            val arrowRotation by updateTransition(transitionState).animateFloat { if (it) 0f else -180f }

                            Image(
                                imageVector = vectorResource(R.drawable.ic_arrow_down),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(AmbientContentColor.current),
                                modifier = Modifier.rotate(arrowRotation)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedVisibility(
                            visible = isCategoryVisible && stories.isEmpty(),
                            enter = expandVertically(),
                            exit = shrinkOut(shrinkTowards = Alignment.TopStart)
                        ) {
                            Text(
                                text = stringResource(R.string.nothing_to_see),
                                color = Color.Gray
                            )
                        }
                    }
                }

                itemsIndexed(stories) { index, story ->
                    AnimatedVisibility(
                        visible = isCategoryVisible,
                        enter = expandVertically(),
                        exit = shrinkOut(shrinkTowards = Alignment.TopStart)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            StoryItem(story)

                            if (index < stories.size - 1) {
                                Divider(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        Text(
            text = stringResource(R.string.nothing_to_see),
            color = Color.Gray
        )
    }
}


private val storyDateFormatter = SimpleDateFormat.getDateInstance()

@Composable
fun StoryItem(
    story: Story
) = ContainerBox(verticalPadding = 6.dp) {
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
                text = storyDateFormatter.format(story.createdDate),
                color = Color.Gray,
                style = MaterialTheme.typography.body2
            )
        }

        Text(
            text = story.title,
            style = MaterialTheme.typography.subtitle2,
        )

        Text(
            text = story.assignee?.fullName ?: stringResource(R.string.unassigned),
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
    StoriesList(
        List(3) {
            Status(
                id = it.toLong(),
                name = "In progress",
                color = "#729fcf"
            ) to List(it) {
                Story(
                    id = it.toLong(),
                    createdDate = Date(),
                    title = "Very cool story",
                    status = Status(
                        id = it.toLong(),
                        name = "In progress",
                        color = "#729fcf"
                    ),
                    assignee = Story.Assignee(
                        id = it.toLong(),
                        fullName = "Name Name"
                    )
                )
            }
        }.toMap()

    )
}