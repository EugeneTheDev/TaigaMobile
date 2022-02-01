package io.eugenethedev.taigamobile.ui.screens.commontask.components

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

fun LazyListScope.CommonTaskWatchers(
    watchers: List<User>,
    editActions: EditActions,
    showWatchersSelector: () -> Unit
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
            onRemoveClick = { editActions.editWatchers.removeItem(item) }
        )

        if (index < watchers.lastIndex) {
            Spacer(Modifier.height(6.dp))
        }
    }

    // add watcher & loader
    item {
        if (editActions.editWatchers.isResultLoading) {
            DotsLoader()
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            AddButton(
                text = stringResource(R.string.add_watchers),
                onClick = { showWatchersSelector() }
            )

            Spacer(modifier = Modifier.width(16.dp))

            val buttonText: Int
            val buttonIcon: Int

            if (!editActions.isWatchingByMe) {
                buttonText = R.string.watch
                buttonIcon = R.drawable.ic_eye
            }
            else {
                buttonText = R.string.not_watch
                buttonIcon = R.drawable.ic_not_watch
            }

            TextButton(
                text = stringResource(buttonText),
                icon = buttonIcon,
                onClick = {
                    if (!editActions.isWatchingByMe) editActions.watch.select() else editActions.watch.remove()
                    editActions.isWatchingByMe = !editActions.isWatchingByMe
                }
            )
        }
    }
}

