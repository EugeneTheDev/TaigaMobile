package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTaskExtended
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.domain.entities.StatusType
import io.eugenethedev.taigamobile.ui.components.ClickableBadge
import io.eugenethedev.taigamobile.ui.screens.commontask.EditActions
import io.eugenethedev.taigamobile.ui.utils.safeParseHexColor

fun LazyListScope.CommonTaskHeader(
    commonTask: CommonTaskExtended,
    editActions: EditActions,
    showStatusSelector: () -> Unit,
    showSprintSelector: () -> Unit,
    showTypeSelector: () -> Unit,
    showSeveritySelector: () -> Unit,
    showPrioritySelector: () -> Unit
) {
    val badgesPadding = 8.dp

    item {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // epic color
            if (commonTask.taskType == CommonTaskType.Epic) {
                Spacer(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = safeParseHexColor(commonTask.color.orEmpty()),
                            shape = MaterialTheme.shapes.small
                        )
                )

                Spacer(Modifier.width(badgesPadding))
            }

            // status
            ClickableBadge(
                text = commonTask.status.name,
                colorHex = commonTask.status.color,
                onClick = {
                    showStatusSelector()
                    editActions.loadStatuses(StatusType.Status)
                },
                isLoading = editActions.editStatus.isResultLoading
            )

            Spacer(Modifier.width(badgesPadding))

            // sprint
            if (commonTask.taskType != CommonTaskType.Epic) {
                ClickableBadge(
                    text = commonTask.sprint?.name ?: stringResource(R.string.no_sprint),
                    color = commonTask.sprint?.name?.let { MaterialTheme.colors.primary } ?: Color.Gray,
                    onClick = {
                        showSprintSelector()
                        editActions.editSprint.loadItems(null)
                    },
                    isLoading = editActions.editSprint.isResultLoading,
                    isClickable = commonTask.taskType != CommonTaskType.Task
                )
            }
        }

        if (commonTask.taskType == CommonTaskType.Issue) {
            Spacer(Modifier.height(badgesPadding))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // type
                ClickableBadge(
                    text = commonTask.type!!.name,
                    colorHex = commonTask.type.color,
                    onClick = {
                        showTypeSelector()
                        editActions.loadStatuses(StatusType.Type)
                    },
                    isLoading = editActions.editType.isResultLoading
                )

                Spacer(Modifier.width(badgesPadding))

                // severity
                ClickableBadge(
                    text = commonTask.severity!!.name,
                    colorHex = commonTask.severity.color,
                    onClick = {
                        showSeveritySelector()
                        editActions.loadStatuses(StatusType.Severity)
                    },
                    isLoading = editActions.editSeverity.isResultLoading
                )

                Spacer(Modifier.width(badgesPadding))

                // priority
                ClickableBadge(
                    text = commonTask.priority!!.name,
                    colorHex = commonTask.priority.color,
                    onClick = {
                        showPrioritySelector()
                        editActions.loadStatuses(StatusType.Priority)
                    },
                    isLoading = editActions.editPriority.isResultLoading
                )
            }
        }

        // title
        Text(
            text = commonTask.title,
            style = MaterialTheme.typography.h5.let {
                if (commonTask.isClosed) {
                    it.merge(
                        SpanStyle(
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough
                        )
                    )
                } else {
                    it
                }
            }
        )

        Spacer(Modifier.height(4.dp))
    }
}
