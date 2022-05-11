package io.eugenethedev.taigamobile.ui.screens.commontask

/**
 * Helper structs for CommonTaskScreen
 */

import androidx.paging.compose.LazyPagingItems
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.ui.utils.NavigateToTask
import java.io.InputStream
import java.time.LocalDate

/**
 * Generic edit action
 */
class EditAction<TItem : Any, TRemove>(
    val items: List<TItem> = emptyList(),
    val itemsLazy: LazyPagingItems<TItem>? = null,
    val searchItems: (query: String) -> Unit = {},
    val select: (item: TItem) -> Unit = {},
    val isLoading: Boolean = false,
    val remove: (item: TRemove) -> Unit = {}
)

/**
 * And some type aliases for certain cases
 */
typealias SimpleEditAction<TItem> = EditAction<TItem, TItem>
typealias EmptyEditAction = EditAction<Unit, Unit>

/**
 * All edit actions
 */
class EditActions(
    val editStatus: SimpleEditAction<Status> = SimpleEditAction(),
    val editType: SimpleEditAction<Status> = SimpleEditAction(),
    val editSeverity: SimpleEditAction<Status> = SimpleEditAction(),
    val editPriority: SimpleEditAction<Status> = SimpleEditAction(),
    val editSwimlane: SimpleEditAction<Swimlane> = SimpleEditAction(),
    val editSprint: SimpleEditAction<Sprint> = SimpleEditAction(),
    val editEpics: EditAction<CommonTask, EpicShortInfo> = EditAction(),
    val editAttachments: EditAction<Pair<String, InputStream>, Attachment> = EditAction(),
    val editAssignees: SimpleEditAction<User> = SimpleEditAction(),
    val editWatchers: SimpleEditAction<User> = SimpleEditAction(),
    val editComments: EditAction<String, Comment> = EditAction(),
    val editBasicInfo: SimpleEditAction<Pair<String, String>> = SimpleEditAction(),
    val editCustomField: SimpleEditAction<Pair<CustomField, CustomFieldValue?>> = SimpleEditAction(),
    val editTags: SimpleEditAction<Tag> = SimpleEditAction(),
    val editDueDate: EditAction<LocalDate, Unit> = EditAction(),
    val editEpicColor: SimpleEditAction<String> = SimpleEditAction(),
    val deleteTask: EmptyEditAction = EmptyEditAction(),
    val promoteTask: EmptyEditAction = EmptyEditAction(),
    val editAssign: EmptyEditAction = EmptyEditAction(),
    val editWatch: EmptyEditAction = EmptyEditAction()
)

/**
 * All navigation actions
 */
class NavigationActions(
    val navigateBack: () -> Unit = {},
    val navigateToCreateTask: () -> Unit = {},
    val navigateToTask: NavigateToTask = { _, _, _ -> },
)
