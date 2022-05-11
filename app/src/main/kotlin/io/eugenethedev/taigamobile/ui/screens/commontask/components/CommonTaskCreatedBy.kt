package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTaskExtended
import io.eugenethedev.taigamobile.domain.entities.User
import io.eugenethedev.taigamobile.ui.components.lists.UserItem

@Suppress("FunctionName")
fun LazyListScope.CommonTaskCreatedBy(
    creator: User,
    commonTask: CommonTaskExtended,
    navigateToProfile: (userId: Long) -> Unit
) {
    item {
        Text(
            text = stringResource(R.string.created_by),
            style = MaterialTheme.typography.titleMedium
        )

        UserItem(
            user = creator,
            dateTime = commonTask.createdDateTime,
            onUserItemClick = { navigateToProfile(creator.id) }
        )
    }
}
