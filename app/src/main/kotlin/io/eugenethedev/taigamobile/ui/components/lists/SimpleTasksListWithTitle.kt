package io.eugenethedev.taigamobile.ui.components.lists

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.ui.components.loaders.DotsLoader
import io.eugenethedev.taigamobile.ui.components.texts.SectionTitle
import io.eugenethedev.taigamobile.ui.utils.NavigateToTask

/**
 * List of tasks with optional title
 */
fun LazyListScope.SimpleTasksListWithTitle(
    commonTasks: List<CommonTask>,
    navigateToTask: NavigateToTask,
    @StringRes titleText: Int? = null,
    topPadding: Dp = 0.dp,
    horizontalPadding: Dp = 0.dp,
    bottomPadding: Dp = 0.dp,
    isTasksLoading: Boolean = false,
    showExtendedTaskInfo: Boolean = false,
    navigateToCreateCommonTask: (() -> Unit)? = null,
    loadData: () -> Unit = {}
) {
    item {
        Spacer(Modifier.height(topPadding))

        titleText?.let {
            SectionTitle(
                text = stringResource(it),
                horizontalPadding = horizontalPadding,
                onAddClick = navigateToCreateCommonTask
            )
        }
    }

    itemsIndexed(commonTasks) { index, item ->
        CommonTaskItem(
            commonTask = item,
            horizontalPadding = horizontalPadding,
            navigateToTask = navigateToTask,
            showExtendedInfo = showExtendedTaskInfo
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
        }

        LaunchedEffect(commonTasks.size) {
            loadData()
        }

        Spacer(Modifier.height(bottomPadding))
    }
}
