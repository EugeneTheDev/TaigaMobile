package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.ui.components.containers.ContainerBox
import io.eugenethedev.taigamobile.ui.components.lists.UserItem
import io.eugenethedev.taigamobile.ui.components.editors.SelectorList
import io.eugenethedev.taigamobile.ui.components.texts.CommonTaskTitle
import io.eugenethedev.taigamobile.ui.screens.commontask.CommonTaskViewModel
import io.eugenethedev.taigamobile.ui.screens.commontask.EditAction
import io.eugenethedev.taigamobile.ui.utils.toColor
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Bunch of common selectors
 */
@Composable
fun Selectors(
    statusEntry: SelectorEntry<Status> = SelectorEntry(),
    typeEntry: SelectorEntry<Status> = SelectorEntry(),
    severityEntry: SelectorEntry<Status> = SelectorEntry(),
    priorityEntry: SelectorEntry<Status> = SelectorEntry(),
    sprintEntry: SelectorEntry<Sprint> = SelectorEntry(),
    epicsEntry: SelectorEntry<CommonTask> = SelectorEntry(),
    assigneesEntry: SelectorEntry<User> = SelectorEntry(),
    watchersEntry: SelectorEntry<User> = SelectorEntry(),
    swimlaneEntry: SelectorEntry<Swimlane> = SelectorEntry()
) {
    // status editor
    SelectorList(
        titleHintId = R.string.choose_status,
        items = statusEntry.edit.items,
        isVisible = statusEntry.isVisible,
        isSearchable = false,
        searchData = statusEntry.edit.searchItems,
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
        isSearchable = false,
        searchData = typeEntry.edit.searchItems,
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
        isSearchable = false,
        searchData = severityEntry.edit.searchItems,
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
        isSearchable = false,
        searchData = priorityEntry.edit.searchItems,
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
        itemsLazy = sprintEntry.edit.itemsLazy,
        isVisible = sprintEntry.isVisible,
        isSearchable = false,
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
        itemsLazy = epicsEntry.edit.itemsLazy,
        isVisible = epicsEntry.isVisible,
        searchData = epicsEntry.edit.searchItems,
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
        searchData = assigneesEntry.edit.searchItems,
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
        searchData = watchersEntry.edit.searchItems,
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
    
    // swimlane editor
    SelectorList(
        titleHintId = R.string.choose_swimlane,
        items = swimlaneEntry.edit.items,
        isVisible = swimlaneEntry.isVisible,
        isSearchable = false,
        navigateBack = swimlaneEntry.hide
    ) {
        SwimlaneItem(
            swimlane = it,
            onClick = {
                swimlaneEntry.edit.selectItem(it)
                swimlaneEntry.hide()
            }
        )
    }
}

class SelectorEntry<T : Any> (
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
        color = status.color.toColor()
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
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }

    sprint.takeIf { it != CommonTaskViewModel.SPRINT_HEADER }?.also {
        Surface(
            contentColor = if (it.isClosed) Color.Gray else MaterialTheme.colorScheme.onSurface
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
                        it.start.format(dateFormatter),
                        it.end.format(dateFormatter)
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    } ?: run {
        Text(
            text = stringResource(R.string.move_to_backlog),
            color = MaterialTheme.colorScheme.primary
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
   CommonTaskTitle(
       ref = epic.ref,
       title = epic.title,
       indicatorColorsHex = epic.colors,
       isInactive = epic.isClosed
   )
}

@Composable
private fun SwimlaneItem(
    swimlane: Swimlane?,
    onClick: () -> Unit
) = ContainerBox(
    verticalPadding = 16.dp,
    onClick = onClick
) {
    fun getOrNull() = swimlane.takeIf { it != CommonTaskViewModel.SWIMLANE_HEADER }

    Text(
        text = getOrNull()?.name ?: stringResource(R.string.unclassifed),
        style = MaterialTheme.typography.bodyLarge,
        color = getOrNull()?.let { MaterialTheme.colorScheme.onSurface } ?: MaterialTheme.colorScheme.primary
    )
}
