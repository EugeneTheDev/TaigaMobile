package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.ui.components.buttons.AddButton
import io.eugenethedev.taigamobile.ui.components.buttons.TextButton
import io.eugenethedev.taigamobile.ui.components.lists.UserItemWithAction
import io.eugenethedev.taigamobile.ui.components.loaders.DotsLoader
import io.eugenethedev.taigamobile.ui.screens.commontask.EditActions

@Suppress("FunctionName")
fun LazyListScope.CommonTaskAssignees(
    assignees: List<User>,
    isAssignedToMe: Boolean,
    editActions: EditActions,
    showAssigneesSelector: () -> Unit,
    navigateToProfile: (userId: Long) -> Unit
) {
    item {
        // assigned to
        Text(
            text = stringResource(R.string.assigned_to),
            style = MaterialTheme.typography.titleMedium
        )
    }

    itemsIndexed(assignees) { index, item ->
        UserItemWithAction(
            user = item,
            onRemoveClick = { editActions.editAssignees.remove(item) },
            onUserItemClick = { navigateToProfile(item.id) }
        )

        if (index < assignees.lastIndex) {
            Spacer(Modifier.height(6.dp))
        }
    }

    // add assignee & loader
    item {
        if (editActions.editAssignees.isLoading) {
            DotsLoader()
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            AddButton(
                text = stringResource(R.string.add_assignee),
                onClick = { showAssigneesSelector() }
            )

            Spacer(modifier = Modifier.width(16.dp))

            val (@StringRes buttonText: Int, @DrawableRes buttonIcon: Int) = if (isAssignedToMe) {
                R.string.unassign to R.drawable.ic_unassigned
            } else {
                R.string.assign_to_me to R.drawable.ic_assignee_to_me
            }

            TextButton(
                text = stringResource(buttonText),
                icon = buttonIcon,
                onClick = {
                    if (isAssignedToMe) {
                        editActions.editAssign.remove(Unit)
                    } else {
                        editActions.editAssign.select(Unit)
                    }
                }
            )
        }
    }
}

