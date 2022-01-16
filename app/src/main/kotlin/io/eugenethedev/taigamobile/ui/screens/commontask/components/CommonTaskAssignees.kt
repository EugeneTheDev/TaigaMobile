package io.eugenethedev.taigamobile.ui.screens.commontask.components

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
import io.eugenethedev.taigamobile.ui.components.lists.UserItemWithAction
import io.eugenethedev.taigamobile.ui.components.loaders.DotsLoader
import io.eugenethedev.taigamobile.ui.screens.commontask.EditActions

fun LazyListScope.CommonTaskAssignees(
    assignees: List<User>,
    editActions: EditActions,
    showAssigneesSelector: () -> Unit
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
            onRemoveClick = { editActions.editAssignees.removeItem(item) }
        )

        if (index < assignees.lastIndex) {
            Spacer(Modifier.height(6.dp))
        }
    }

    // add assignee & loader
    item {
        if (editActions.editAssignees.isResultLoading) {
            DotsLoader()
        }
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            AddButton(
                text = stringResource(R.string.add_user),
                onClick = { showAssigneesSelector() }
            )
            AddButton(
                text = stringResource(R.string.assign_to_me),
                onClick = {  }
            )
        }
    }
}
