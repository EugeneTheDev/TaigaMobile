package io.eugenethedev.taigamobile.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.flowlayout.FlowRow
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.*
import io.eugenethedev.taigamobile.ui.components.buttons.TextButton
import io.eugenethedev.taigamobile.ui.components.badges.Badge
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.taigaGray
import io.eugenethedev.taigamobile.ui.utils.clickableUnindicated
import io.eugenethedev.taigamobile.ui.utils.toColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun Filters(
    selected: FiltersData,
    onSelect: (FiltersData) -> Unit,
    data: FiltersData
) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxWidth()
) {
    val unselectedFilters = data - selected

    val space = 6.dp
    val coroutineScope = rememberCoroutineScope()

    // compose version of BottomSheetDialog (from Dialog and ModalBottomSheetLayout)
    val bottomSheetState =  remember { ModalBottomSheetState( ModalBottomSheetValue.Expanded) } // fix to handle dialog closed state properly
    var isVisible by remember { mutableStateOf(false) }

    TextButton(
        onClick = {
            coroutineScope.launch {
                isVisible = true
                bottomSheetState.show()
            }
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.ic_filter),
                contentDescription = null
            )

            Spacer(Modifier.width(space))

            Text(stringResource(R.string.show_filters))

            selected.filtersNumber.takeIf { it > 0 }?.let {
                Spacer(Modifier.width(space))
                Badge(it.toString())
            }
        }
    }

    Spacer(Modifier.height(space))

    if (isVisible) {
        Dialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = {
                coroutineScope.launch {
                    bottomSheetState.hide()
                    isVisible = false
                }
            }
        ) {
            if (bottomSheetState.currentValue == ModalBottomSheetValue.Hidden && bottomSheetState.targetValue == ModalBottomSheetValue.Hidden) {
                isVisible = false
            }

            ModalBottomSheetLayout(
                modifier = Modifier.fillMaxSize(),
                sheetState = bottomSheetState,
                sheetShape = MaterialTheme.shapes.medium,
                scrimColor = Color.Transparent,
                content = {},
                sheetContent = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(space)
                    ) {
                        Text(
                            text = stringResource(R.string.filters),
                            style = MaterialTheme.typography.h5
                        )

                        Spacer(Modifier.height(space))

                        Column(modifier = Modifier.fillMaxWidth()) {
                            FlowRow {
                                selected.types.forEach {
                                    FilterChip(
                                        filter = it,
                                        onRemoveClick = { onSelect(selected.copy(types = selected.types - it)) }
                                    )
                                }

                                selected.severities.forEach {
                                    FilterChip(
                                        filter = it,
                                        onRemoveClick = { onSelect(selected.copy(severities = selected.severities - it)) }
                                    )
                                }

                                selected.priorities.forEach {
                                    FilterChip(
                                        filter = it,
                                        onRemoveClick = { onSelect(selected.copy(priorities = selected.priorities - it)) }
                                    )
                                }

                                selected.statuses.forEach {
                                    FilterChip(
                                        filter = it,
                                        onRemoveClick = { onSelect(selected.copy(statuses = selected.statuses - it)) }
                                    )
                                }

                                selected.tags.forEach {
                                    FilterChip(
                                        filter = it,
                                        onRemoveClick = { onSelect(selected.copy(tags = selected.tags - it)) }
                                    )
                                }

                                selected.assignees.forEach {
                                    FilterChip(
                                        filter = it,
                                        onRemoveClick = { onSelect(selected.copy(assignees = selected.assignees - it)) }
                                    )
                                }

                                selected.roles.forEach {
                                    FilterChip(
                                        filter = it,
                                        onRemoveClick = { onSelect(selected.copy(roles = selected.roles - it)) }
                                    )
                                }

                                selected.createdBy.forEach {
                                    FilterChip(
                                        filter = it,
                                        onRemoveClick = { onSelect(selected.copy(createdBy = selected.createdBy - it)) }
                                    )
                                }
                            }

                            if (selected.filtersNumber > 0) {
                                Spacer(Modifier.height(space))
                            }

                            val sectionsSpace = 4.dp

                            unselectedFilters.types.ifHasData {
                                Section(
                                    titleId = R.string.type_title,
                                    filters = it,
                                    onSelect = { onSelect(selected.copy(types = selected.types + it)) }
                                )
                                Spacer(Modifier.height(sectionsSpace))
                            }

                            unselectedFilters.severities.ifHasData {
                                Section(
                                    titleId = R.string.severity_title,
                                    filters = it,
                                    onSelect = { onSelect(selected.copy(severities = selected.severities + it)) }
                                )
                                Spacer(Modifier.height(sectionsSpace))
                            }

                            unselectedFilters.priorities.ifHasData {
                                Section(
                                    titleId = R.string.priority_title,
                                    filters = it,
                                    onSelect = { onSelect(selected.copy(priorities = selected.priorities + it)) }
                                )
                                Spacer(Modifier.height(sectionsSpace))
                            }

                            unselectedFilters.statuses.ifHasData {
                                Section(
                                    titleId = R.string.status_title,
                                    filters = it,
                                    onSelect = { onSelect(selected.copy(statuses = selected.statuses + it)) }
                                )
                                Spacer(Modifier.height(sectionsSpace))
                            }

                            unselectedFilters.tags.ifHasData {
                                Section(
                                    titleId = R.string.tags_title,
                                    filters = it,
                                    onSelect = { onSelect(selected.copy(tags = selected.tags + it)) }
                                )
                                Spacer(Modifier.height(sectionsSpace))
                            }

                            unselectedFilters.assignees.ifHasData {
                                Section(
                                    titleId = R.string.assignees_title,
                                    filters = it,
                                    onSelect = { onSelect(selected.copy(assignees = selected.assignees + it)) }
                                )
                                Spacer(Modifier.height(sectionsSpace))
                            }

                            unselectedFilters.roles.ifHasData {
                                Section(
                                    titleId = R.string.role_title,
                                    filters = it,
                                    onSelect = { onSelect(selected.copy(roles = selected.roles + it)) }
                                )
                            }

                            unselectedFilters.createdBy.ifHasData {
                                Section(
                                    titleId = R.string.created_by_title,
                                    filters = it,
                                    onSelect = { onSelect(selected.copy(createdBy = selected.createdBy + it)) }
                                )
                            }
                        }

                        Spacer(Modifier.height(space))
                    }
                }
            )

        }
    }
}

private inline fun <T : Filter> List<T>.ifHasData(action: (List<T>) -> Unit) =
    takeIf { it.hasData() }?.let(action)

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun <T : Filter> Section(
    @StringRes titleId: Int,
    filters: List<T>,
    onSelect: (T) -> Unit
) = Column(
    modifier = Modifier.fillMaxWidth()
) {
    var isExpanded by remember { mutableStateOf(false) }

    val transitionState = remember { MutableTransitionState(isExpanded) }
    transitionState.targetState = isExpanded

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickableUnindicated { isExpanded = !isExpanded }
    ) {
        val arrowRotation by updateTransition(
            transitionState,
            label = "arrow"
        ).animateFloat { if (it) 0f else -90f }
        Icon(
            painter = painterResource(R.drawable.ic_arrow_down),
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.rotate(arrowRotation),
            contentDescription = null
        )

        Text(
            text = stringResource(titleId),
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }

    AnimatedVisibility(visible = isExpanded) {
        FlowRow(modifier = Modifier.padding(vertical = 2.dp, horizontal = 6.dp)) {
            filters.forEach {
                FilterChip(
                    filter = it,
                    onClick = { onSelect(it) }
                )
            }
        }
    }

}

@Composable
private fun FilterChip(
    filter: Filter,
    onClick: () -> Unit = {},
    onRemoveClick: (() -> Unit)? = null
) = Chip(
    onClick = onClick,
    color = filter.color?.toColor() ?: taigaGray,
    modifier = Modifier.padding(end = 4.dp, bottom = 6.dp)
) {
    val space = 6.dp

    Row(verticalAlignment = Alignment.CenterVertically) {
        onRemoveClick?.let {
            IconButton(
                onClick = it,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_remove),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(space))
        }

        Text(filter.name.takeIf { it.isNotEmpty() } ?: stringResource(R.string.unassigned))

        Spacer(Modifier.width(space))

        Badge(
            text = filter.count.toString(),
            isActive = false
        )
    }
}


@Preview(showBackground = true)
@Composable
fun FiltersPreview() = TaigaMobileTheme {
    var selected by remember { mutableStateOf(FiltersData()) }

    Column {
        Text("test")

        Filters(
            selected = selected,
            onSelect = { selected = it },
            data = FiltersData(
                assignees = listOf(
                    UsersFilter(null, "", 2),
                    *List(10) { UsersFilter(it.toLong(), "Human $it", it % 3) }.toTypedArray()
                ),
                roles = listOf(
                    RolesFilter(0, "UX", 1),
                    RolesFilter(1, "Developer", 4),
                    RolesFilter(2, "Stakeholder", 0),
                ),
                tags = listOf(
                    *List(10) {
                        listOf(
                            TagsFilter("#7E57C2", "tag ${it * 3}", 3),
                            TagsFilter("#F57C00", "tag ${it * 3 + 1}", 4),
                            TagsFilter("#C62828", "tag ${it * 3 + 2}", 0),
                        )
                    }.flatten().toTypedArray()
                ),
                statuses = listOf(
                    StatusesFilter(0, "#B0BEC5", "Backlog", 2),
                    StatusesFilter(1, "#1E88E5", "In progress", 1),
                    StatusesFilter(2, "#43A047", "Done", 3),
                ),
                priorities = listOf(
                    StatusesFilter(0, "#29B6F6", "Low", 2),
                    StatusesFilter(1, "#43A047", "Normal", 1),
                    StatusesFilter(2, "#FBC02D", "High", 2),
                ),
                severities = listOf(
                    StatusesFilter(0, "#29B6F6", "Minor", 2),
                    StatusesFilter(1, "#43A047", "Normal", 1),
                    StatusesFilter(2, "#FBC02D", "Major", 2),
                    StatusesFilter(0, "#29B6F6", "Minor", 2),
                    StatusesFilter(1, "#43A047", "Normal", 1),
                    StatusesFilter(2, "#FBC02D", "Major", 2)
                ),
                types = listOf(
                    StatusesFilter(0, "#F44336", "Bug", 2),
                    StatusesFilter(1, "#C8E6C9", "Question", 1),
                    StatusesFilter(2, "#C8E6C9", "Enhancement", 2),
                )
            )
        )

        Text("Text")
    }
}
