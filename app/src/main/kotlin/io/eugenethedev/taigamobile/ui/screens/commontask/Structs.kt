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
 * Common fields when performing editing (choosing something from list)
 */
class EditAction<T : Any>(
    val items: List<T> = emptyList(),
    val itemsLazy: LazyPagingItems<T>? = null,
    val searchItems: (query: String) -> Unit = {},
    val selectItem: (item: T) -> Unit = {},
    val isResultLoading: Boolean = false,
    val removeItem: (item: T) -> Unit = {}
)

class EditCommentsAction(
    val createComment: (String) -> Unit = {},
    val deleteComment: (Comment) -> Unit = {},
    val isResultLoading: Boolean = false
)

class EditAttachmentsAction(
    val deleteAttachment: (Attachment) -> Unit = {},
    val addAttachment: (name: String, stream: InputStream) -> Unit = { _, _ -> },
    val isResultLoading: Boolean = false
)

class EditSimple<T>(
    val select: (T) -> Unit = {},
    val isResultLoading: Boolean = false
)

class EditSimpleEmpty(
    val select: () -> Unit = {},
    val remove: () -> Unit = {},
    val isResultLoading: Boolean = false
)

// all edit actions in one place
class EditActions(
    val editStatus: EditAction<Status> = EditAction(),
    val editType: EditAction<Status> = EditAction(),
    val editSeverity: EditAction<Status> = EditAction(),
    val editPriority: EditAction<Status> = EditAction(),
    val editSwimlane: EditAction<Swimlane> = EditAction(),
    val editSprint: EditAction<Sprint> = EditAction(),
    val editEpics: EditAction<CommonTask> = EditAction(),
    val unlinkFromEpic: (EpicShortInfo) -> Unit = {},
    val editAttachments: EditAttachmentsAction = EditAttachmentsAction(),
    val editAssignees: EditAction<User> = EditAction(),
    val editWatchers: EditAction<User> = EditAction(),
    val editComments: EditCommentsAction = EditCommentsAction(),
    val editTask: (title: String, description: String) -> Unit = { _, _ -> },
    val editCustomField: (CustomField, CustomFieldValue?) -> Unit = { _, _ -> },
    val editTags: EditAction<Tag> = EditAction(),
    val editDueDate: EditSimple<LocalDate?> = EditSimple(),
    val editEpicColor: EditSimple<String> = EditSimple(),
    val deleteTask: () -> Unit = {},
    val promoteTask: () -> Unit = {},
    val assign: EditSimpleEmpty = EditSimpleEmpty(),
    val watch: EditSimpleEmpty = EditSimpleEmpty(),
    var isAssignedToMe: Boolean = false,
    var isWatchedByMe: Boolean = false,
    val showMessage: (message: Int) -> Unit = {}
)

// all loading statuses in one place
class Loaders(
    val isLoading: Boolean = false,
    val isEditLoading: Boolean = false,
    val isDeleteLoading: Boolean = false,
    val isPromoteLoading: Boolean = false,
    val isCustomFieldsLoading: Boolean = false
)

// all navigate actions
class NavigationActions(
    val navigateBack: () -> Unit = {},
    val navigateToCreateTask: () -> Unit = {},
    val navigateToTask: NavigateToTask = { _, _, _ -> },
)
