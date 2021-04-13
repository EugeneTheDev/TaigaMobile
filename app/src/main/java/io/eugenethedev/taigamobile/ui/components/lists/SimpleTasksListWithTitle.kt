package io.eugenethedev.taigamobile.ui.components.lists

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.ui.components.PlusButton
import io.eugenethedev.taigamobile.ui.components.loaders.DotsLoader
import io.eugenethedev.taigamobile.ui.components.texts.NothingToSeeHereText
import io.eugenethedev.taigamobile.ui.utils.NavigateToTask

fun LazyListScope.SimpleTasksListWithTitle(
    @StringRes titleText: Int,
    commonTasks: List<CommonTask>,
    topMargin: Dp = 0.dp,
    horizontalPadding: Dp = 0.dp,
    bottomMargin: Dp = 0.dp,
    isTasksLoading: Boolean = false,
    navigateToTask: NavigateToTask,
    navigateToCreateCommonTask: (() -> Unit)? = null,
    loadData: () -> Unit = {}
) {
    item {
        Spacer(Modifier.height(topMargin))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = horizontalPadding)
        ) {
            Text(
                text = stringResource(titleText),
                style = MaterialTheme.typography.h6
            )

            navigateToCreateCommonTask?.let {
                PlusButton(onClick = it)
            }
        }
    }

    itemsIndexed(commonTasks) { index, item ->
        CommonTaskItem(
            commonTask = item,
            horizontalPadding = horizontalPadding,
            navigateToTask = navigateToTask
        )

        if (index < commonTasks.lastIndex) {
            Divider(
                modifier = Modifier.padding(vertical = 4.dp, horizontal = horizontalPadding),
                color = Color.LightGray
            )
        }
    }

    item {
        if (isTasksLoading) {
            DotsLoader()
        } else if (commonTasks.isEmpty()) {
            NothingToSeeHereText()
        }

        LaunchedEffect(commonTasks.size) {
            loadData()
        }
        Spacer(Modifier.height(bottomMargin))
    }
}
