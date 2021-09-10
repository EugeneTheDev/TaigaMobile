package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTaskExtended
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.domain.entities.EpicShortInfo
import io.eugenethedev.taigamobile.domain.entities.UserStoryShortInfo
import io.eugenethedev.taigamobile.ui.components.dialogs.ConfirmActionDialog
import io.eugenethedev.taigamobile.ui.components.buttons.AddButton
import io.eugenethedev.taigamobile.ui.components.loaders.DotsLoader
import io.eugenethedev.taigamobile.ui.components.texts.TitleWithIndicators
import io.eugenethedev.taigamobile.ui.screens.commontask.EditActions
import io.eugenethedev.taigamobile.ui.screens.commontask.NavigationActions
import io.eugenethedev.taigamobile.ui.utils.clickableUnindicated

fun LazyListScope.CommonTaskBelongsTo(
    commonTask: CommonTaskExtended,
    navigationActions: NavigationActions,
    editActions: EditActions,
    showEpicsSelector: () -> Unit
) {
    // belongs to (epics)
    if (commonTask.taskType == CommonTaskType.UserStory) {
        items(commonTask.epicsShortInfo) {
            EpicItemWithAction(
                epic = it,
                onClick = { navigationActions.navigateToTask(it.id, CommonTaskType.Epic, it.ref) },
                onRemoveClick = { editActions.unlinkFromEpic(it) }
            )

            Spacer(Modifier.height(2.dp))
        }

        item {
            if (editActions.editEpics.isResultLoading) {
                DotsLoader()
            }

            AddButton(
                text = stringResource(R.string.link_to_epic),
                onClick = {
                    showEpicsSelector()
                    editActions.editEpics.loadItems(null)
                }
            )
        }
    }

    // belongs to (story)
    if (commonTask.taskType == CommonTaskType.Task) {
        commonTask.userStoryShortInfo?.let {
            item {
                UserStoryItem(
                    story = it,
                    onClick = {
                        navigationActions.navigateToTask(it.id, CommonTaskType.UserStory, it.ref)
                    }
                )
            }
        }
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
        ConfirmActionDialog(
            title = stringResource(R.string.unlink_epic_title),
            text = stringResource(R.string.unlink_epic_text),
            onConfirm = {
                isAlertVisible = false
                onRemoveClick()
            },
            onDismiss = { isAlertVisible = false }
        )
    }

    TitleWithIndicators(
        ref = epic.ref,
        title = epic.title,
        textColor = MaterialTheme.colors.primary,
        indicatorColorsHex = listOf(epic.color),
        modifier = Modifier
            .weight(1f)
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
