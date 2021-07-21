package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.Sprint
import io.eugenethedev.taigamobile.domain.entities.Status
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.ui.components.containers.ContainerBox
import io.eugenethedev.taigamobile.ui.components.lists.UserItem
import io.eugenethedev.taigamobile.ui.components.editors.SelectorList
import io.eugenethedev.taigamobile.ui.components.texts.TitleWithIndicators
import io.eugenethedev.taigamobile.ui.screens.commontask.EditAction
import io.eugenethedev.taigamobile.ui.utils.safeParseHexColor
import java.text.SimpleDateFormat

/**
 * Bunch of common selectors
 */
@ExperimentalAnimationApi
@Composable
fun Selectors(
    statusEntry: SelectorEntry<Status> = SelectorEntry(),
    typeEntry: SelectorEntry<Status> = SelectorEntry(),
    severityEntry: SelectorEntry<Status> = SelectorEntry(),
    priorityEntry: SelectorEntry<Status> = SelectorEntry(),
    sprintEntry: SelectorEntry<Sprint?> = SelectorEntry(),
    epicsEntry: SelectorEntry<CommonTask> = SelectorEntry(),
    assigneesEntry: SelectorEntry<User> = SelectorEntry(),
    watchersEntry: SelectorEntry<User> = SelectorEntry()
) {
    // status editor
    SelectorList(
        titleHintId = R.string.choose_status,
        items = statusEntry.edit.items,
        isVisible = statusEntry.isVisible,
        isLoading = statusEntry.edit.isItemsLoading,
        isSearchable = false,
        loadData = statusEntry.edit.loadItems,
        navigateBack = statusEntry.hide
    ) {
        StatusItem(
            status = it,
            onClick = {
                statusEntry.edit.selectItem(it)
                statusEntry.hide()
            }
        )
    }

    // type editor
    SelectorList(
        titleHintId = R.string.choose_type,
        items = typeEntry.edit.items,
        isVisible = typeEntry.isVisible,
        isLoading = typeEntry.edit.isItemsLoading,
        isSearchable = false,
        loadData = typeEntry.edit.loadItems,
        navigateBack = typeEntry.hide
    ) {
        StatusItem(
            status = it,
            onClick = {
                typeEntry.edit.selectItem(it)
                typeEntry.hide()
            }
        )
    }
    
    // severity editor
    SelectorList(
        titleHintId = R.string.choose_severity,
        items = severityEntry.edit.items,
        isVisible = severityEntry.isVisible,
        isLoading = severityEntry.edit.isItemsLoading,
        isSearchable = false,
        loadData = severityEntry.edit.loadItems,
        navigateBack = severityEntry.hide
    ) {
        StatusItem(
            status = it,
            onClick = {
                severityEntry.edit.selectItem(it)
                severityEntry.hide()
            }
        )
    }
    
    // priority editor
    SelectorList(
        titleHintId = R.string.choose_priority,
        items = priorityEntry.edit.items,
        isVisible = priorityEntry.isVisible,
        isLoading = priorityEntry.edit.isItemsLoading,
        isSearchable = false,
        loadData = priorityEntry.edit.loadItems,
        navigateBack = priorityEntry.hide
    ) {
        StatusItem(
            status = it,
            onClick = {
                priorityEntry.edit.selectItem(it)
                priorityEntry.hide()
            }
        )
    }

    // sprint editor
    SelectorList(
        titleHintId = R.string.choose_sprint,
        items = sprintEntry.edit.items,
        isVisible = sprintEntry.isVisible,
        isLoading = sprintEntry.edit.isItemsLoading,
        isSearchable = false,
        loadData = sprintEntry.edit.loadItems,
        navigateBack = sprintEntry.hide
    ) {
        SprintItem(
            sprint = it,
            onClick = {
                sprintEntry.edit.selectItem(it)
                sprintEntry.hide()
            }
        )
    }

    // epics editor
    SelectorList(
        titleHintId = R.string.search_epics,
        items = epicsEntry.edit.items,
        isVisible = epicsEntry.isVisible,
        isLoading = epicsEntry.edit.isItemsLoading,
        loadData = epicsEntry.edit.loadItems,
        navigateBack = epicsEntry.hide
    ) {
        EpicItem(
            epic = it,
            onClick = {
                epicsEntry.edit.selectItem(it)
                epicsEntry.hide()
            }
        )
    }


    // assignees editor
    SelectorList(
        titleHintId = R.string.search_members,
        items = assigneesEntry.edit.items,
        isVisible = assigneesEntry.isVisible,
        isLoading = assigneesEntry.edit.isItemsLoading,
        loadData = assigneesEntry.edit.loadItems,
        navigateBack = assigneesEntry.hide
    ) {
        MemberItem(
            member = it,
            onClick = {
                assigneesEntry.edit.selectItem(it)
                assigneesEntry.hide()
            }
        )
    }

    // watchers editor
    SelectorList(
        titleHintId = R.string.search_members,
        items = watchersEntry.edit.items,
        isVisible = watchersEntry.isVisible,
        isLoading = watchersEntry.edit.isItemsLoading,
        loadData = watchersEntry.edit.loadItems,
        navigateBack = watchersEntry.hide
    ) {
        MemberItem(
            member = it,
            onClick = {
                watchersEntry.edit.selectItem(it)
                watchersEntry.hide()
            }
        )
    }
}

class SelectorEntry<T> (
    val edit: EditAction<T> = EditAction(),
    val isVisible: Boolean = false,
    val hide: () -> Unit = {}
)

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
        color = safeParseHexColor(status.color)
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
       indicatorColorsHex = epic.colors,
       isInactive = epic.isClosed
   )
}
