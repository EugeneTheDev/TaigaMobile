package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTaskExtended
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.ui.components.buttons.AddButton
import io.eugenethedev.taigamobile.ui.components.loaders.DotsLoader
import io.eugenethedev.taigamobile.ui.screens.commontask.EditActions
import io.eugenethedev.taigamobile.ui.screens.commontask.NavigationActions

fun LazyListScope.CommonTaskBelongsTo(
    commonTask: CommonTaskExtended,
    navigationActions: NavigationActions,
    editActions: EditActions,
    showEpicsSelector: () -> Unit
) {
    // belongs to (epics)
    if (commonTask.taskType == CommonTaskType.UserStory) {
        item {
            Text(
                text = stringResource(R.string.belongs_to_epics),
                style = MaterialTheme.typography.subtitle1
            )
        }

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
                Text(
                    text = stringResource(R.string.belongs_to_story),
                    style = MaterialTheme.typography.subtitle1
                )

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
