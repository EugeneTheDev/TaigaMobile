package io.eugenethedev.taigamobile.ui.screens.commontask

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.Sprint
import io.eugenethedev.taigamobile.domain.entities.Status
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.ui.components.ContainerBox
import io.eugenethedev.taigamobile.ui.components.UserItem
import io.eugenethedev.taigamobile.ui.components.editors.SelectorList
import io.eugenethedev.taigamobile.ui.components.texts.TitleWithIndicators
import java.text.SimpleDateFormat

@ExperimentalAnimationApi
@Composable
fun Selectors(
    editStatus: EditAction<Status>,
    isStatusSelectorVisible: Boolean,
    hideStatusSelector: () -> Unit,
    editSprint: EditAction<Sprint?>,
    isSprintSelectorVisible: Boolean,
    hideSprintSelector: () -> Unit,
    editEpics: EditAction<CommonTask>,
    isEpicsSelectorVisible: Boolean,
    hideEpicsSelector: () -> Unit,
    editAssignees: EditAction<User>,
    isAssigneesSelectorVisible: Boolean,
    hideAssigneesSelector: () -> Unit,
    editWatchers: EditAction<User>,
    isWatchersSelectorVisible: Boolean,
    hideWatchersSelector: () -> Unit
) {
    // status editor
    SelectorList(
        titleHint = stringResource(R.string.choose_status),
        items = editStatus.items,
        isVisible = isStatusSelectorVisible,
        isLoading = editStatus.isItemsLoading,
        isSearchable = false,
        loadData = editStatus.loadItems,
        navigateBack = hideStatusSelector
    ) {
        StatusItem(
            status = it,
            onClick = {
                editStatus.selectItem(it)
                hideStatusSelector()
            }
        )
    }

    // sprint editor
    SelectorList(
        titleHint = stringResource(R.string.choose_sprint),
        items = editSprint.items,
        isVisible = isSprintSelectorVisible,
        isLoading = editSprint.isItemsLoading,
        isSearchable = false,
        loadData = editSprint.loadItems,
        navigateBack = hideSprintSelector
    ) {
        SprintItem(
            sprint = it,
            onClick = {
                editSprint.selectItem(it)
                hideSprintSelector()
            }
        )
    }

    // sprint editor
    SelectorList(
        titleHint = stringResource(R.string.search_epics),
        items = editEpics.items,
        isVisible = isEpicsSelectorVisible,
        isLoading = editEpics.isItemsLoading,
        loadData = editEpics.loadItems,
        navigateBack = hideEpicsSelector
    ) {
        EpicItem(
            epic = it,
            onClick = {
                editEpics.selectItem(it)
                hideEpicsSelector()
            }
        )
    }


    // assignees editor
    SelectorList(
        titleHint = stringResource(R.string.search_members),
        items = editAssignees.items,
        isVisible = isAssigneesSelectorVisible,
        isLoading = editAssignees.isItemsLoading,
        loadData = editAssignees.loadItems,
        navigateBack = hideAssigneesSelector
    ) {
        MemberItem(
            member = it,
            onClick = {
                editAssignees.selectItem(it)
                hideAssigneesSelector()
            }
        )
    }

    // watchers editor
    SelectorList(
        titleHint = stringResource(R.string.search_members),
        items = editWatchers.items,
        isVisible = isWatchersSelectorVisible,
        isLoading = editWatchers.isItemsLoading,
        loadData = editWatchers.loadItems,
        navigateBack = hideWatchersSelector
    ) {
        MemberItem(
            member = it,
            onClick = {
                editWatchers.selectItem(it)
                hideWatchersSelector()
            }
        )
    }
}

@Composable
private fun StatusItem(
    status: Status,
    onClick: () -> Unit
) = ContainerBox(
    verticalPadding = 16.dp,
    onClick = onClick
) {
    Text(
        text = status.name,
        color = Color(android.graphics.Color.parseColor(status.color))
    )
}

@Composable
private fun SprintItem(
    sprint: Sprint?,
    onClick: () -> Unit
) = ContainerBox(
    verticalPadding = 16.dp,
    onClick = onClick
) {
    val dateFormatter = remember { SimpleDateFormat.getDateInstance() }

    sprint?.also {
        Surface(
            contentColor = if (it.isClosed) Color.Gray else MaterialTheme.colors.onSurface
        ) {
            Column {
                Text(
                    if (it.isClosed) {
                        stringResource(R.string.closed_sprint_name_template).format(it.name)
                    } else {
                        it.name
                    }
                )

                Text(
                    text = stringResource(R.string.sprint_dates_template).format(
                        dateFormatter.format(it.start),
                        dateFormatter.format(it.finish)
                    ),
                    style = MaterialTheme.typography.body2
                )
            }
        }
    } ?: run {
        Text(
            text = stringResource(R.string.move_to_backlog),
            color = MaterialTheme.colors.primary
        )
    }
}

@Composable
private fun MemberItem(
    member: User,
    onClick: () -> Unit
) = ContainerBox(
    verticalPadding = 16.dp,
    onClick = onClick
) {
    UserItem(member)
}

@Composable
private fun EpicItem(
    epic: CommonTask,
    onClick: () -> Unit
) = ContainerBox(
    verticalPadding = 16.dp,
    onClick = onClick
) {
   TitleWithIndicators(
       ref = epic.ref,
       title = epic.title,
       indicatorColorsHex = epic.colors
   )
}