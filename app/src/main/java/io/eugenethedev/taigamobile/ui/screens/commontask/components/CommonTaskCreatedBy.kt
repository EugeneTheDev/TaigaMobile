package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.res.stringResource
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTaskExtended
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.ui.components.lists.UserItem

fun LazyListScope.CommonTaskCreatedBy(
    creator: User,
    commonTask: CommonTaskExtended
) {
    item {
        Text(
            text = stringResource(R.string.created_by),
            style = MaterialTheme.typography.subtitle1
        )

        UserItem(
            user = creator,
            dateTime = commonTask.createdDateTime
        )
    }
}
