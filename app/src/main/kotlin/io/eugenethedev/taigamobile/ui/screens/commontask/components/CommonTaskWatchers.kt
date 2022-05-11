package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
fun LazyListScope.CommonTaskWatchers(
    watchers: List<User>,
    isWatchedByMe: Boolean,
    editActions: EditActions,
    showWatchersSelector: () -> Unit,
    navigateToProfile: (userId: Long) -> Unit
) {
    item {
        // watchers
        Text(
            text = stringResource(R.string.watchers),
            style = MaterialTheme.typography.titleMedium
        )
    }

    itemsIndexed(watchers) { index, item ->
        UserItemWithAction(
            user = item,
            onRemoveClick = { editActions.editWatchers.remove(item) },
            onUserItemClick = { navigateToProfile(item.id) }
        )

        if (index < watchers.lastIndex) {
            Spacer(Modifier.height(6.dp))
        }
    }

    // add watcher & loader
    item {
        if (editActions.editWatchers.isLoading) {
            DotsLoader()
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            AddButton(
                text = stringResource(R.string.add_watcher),
                onClick = { showWatchersSelector() }
            )

            Spacer(modifier = Modifier.width(16.dp))

            val (@StringRes buttonText: Int, @DrawableRes buttonIcon: Int) = if (isWatchedByMe) {
                R.string.unwatch to R.drawable.ic_unwatch
            } else {
                R.string.watch to R.drawable.ic_watch
            }

            TextButton(
                text = stringResource(buttonText),
                icon = buttonIcon,
                onClick = {
                    if (isWatchedByMe) {
                        editActions.editWatch.remove(Unit)
                    } else {
                        editActions.editWatch.select(Unit)
                    }
                }
            )
        }
    }
}

