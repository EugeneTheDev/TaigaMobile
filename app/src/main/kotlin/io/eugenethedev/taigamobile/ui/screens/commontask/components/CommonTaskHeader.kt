package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTaskExtended
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.ui.components.badges.ClickableBadge
import io.eugenethedev.taigamobile.ui.components.pickers.ColorPicker
import io.eugenethedev.taigamobile.ui.screens.commontask.EditActions
import io.eugenethedev.taigamobile.ui.theme.taigaRed
import io.eugenethedev.taigamobile.ui.utils.toColor
import io.eugenethedev.taigamobile.ui.utils.toHex

fun LazyListScope.CommonTaskHeader(
    commonTask: CommonTaskExtended,
    editActions: EditActions,
    showStatusSelector: () -> Unit,
    showSprintSelector: () -> Unit,
    showTypeSelector: () -> Unit,
    showSeveritySelector: () -> Unit,
    showPrioritySelector: () -> Unit,
    showSwimlaneSelector: () -> Unit
) {
    val badgesPadding = 8.dp

    item {

        commonTask.blockedNote?.trim()?.let {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(taigaRed, MaterialTheme.shapes.medium)
                    .padding(8.dp)
            ) {
                val space = 4.dp

                Icon(
                    painter = painterResource(R.drawable.ic_lock),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.width(space))

                Text(stringResource(R.string.blocked))

                Spacer(Modifier.width(space))

                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }

            Spacer(Modifier.height(badgesPadding))
        }

        FlowRow(
            crossAxisAlignment = FlowCrossAxisAlignment.Center,
            crossAxisSpacing = badgesPadding,
            mainAxisSpacing = badgesPadding
        ) {
            // epic color
            if (commonTask.taskType == CommonTaskType.Epic) {
                ColorPicker(
                    size = 32.dp,
                    color = commonTask.color.orEmpty().toColor(),
                    onColorPicked = { editActions.editEpicColor.select(it.toHex()) }
                )

            }

            // status
            ClickableBadge(
                text = commonTask.status.name,
                colorHex = commonTask.status.color,
                onClick = { showStatusSelector() },
                isLoading = editActions.editStatus.isResultLoading
            )

            // sprint
            if (commonTask.taskType != CommonTaskType.Epic) {
                ClickableBadge(
                    text = commonTask.sprint?.name ?: stringResource(R.string.no_sprint),
                    color = commonTask.sprint?.let { MaterialTheme.colorScheme.primary }
                        ?: MaterialTheme.colorScheme.outline,
                    onClick = { showSprintSelector() },
                    isLoading = editActions.editSprint.isResultLoading,
                    isClickable = commonTask.taskType != CommonTaskType.Task
                )
            }

            // swimlane
            if (commonTask.taskType == CommonTaskType.UserStory) {
                ClickableBadge(
                    text = commonTask.swimlane?.name ?: stringResource(R.string.unclassifed),
                    color = commonTask.swimlane?.let { MaterialTheme.colorScheme.primary }
                        ?: MaterialTheme.colorScheme.outline,
                    isLoading = editActions.editSwimlane.isResultLoading,
                    onClick = { showSwimlaneSelector() }
                )
            }

            if (commonTask.taskType == CommonTaskType.Issue) {
                // type
                ClickableBadge(
                    text = commonTask.type!!.name,
                    colorHex = commonTask.type.color,
                    onClick = { showTypeSelector() },
                    isLoading = editActions.editType.isResultLoading
                )

                // severity
                ClickableBadge(
                    text = commonTask.severity!!.name,
                    colorHex = commonTask.severity.color,
                    onClick = { showSeveritySelector() },
                    isLoading = editActions.editSeverity.isResultLoading
                )

                // priority
                ClickableBadge(
                    text = commonTask.priority!!.name,
                    colorHex = commonTask.priority.color,
                    onClick = { showPrioritySelector() },
                    isLoading = editActions.editPriority.isResultLoading
                )
            }
        }

        // title
        Text(
            text = commonTask.title,
            style = MaterialTheme.typography.headlineSmall.let {
                if (commonTask.isClosed) {
                    it.merge(
                        SpanStyle(
                            color = MaterialTheme.colorScheme.outline,
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
