package io.eugenethedev.taigamobile.ui.screens.commontask


/**
 * Common fields when performing task editing (choosing something from list)
 */
class EditAction<T>(
    val items: List<T> = emptyList(),
    val loadItems: (query: String?) -> Unit = {},
    val isItemsLoading: Boolean = false,
    val selectItem: (item: T) -> Unit = {},
    val isResultLoading: Boolean = false
)
