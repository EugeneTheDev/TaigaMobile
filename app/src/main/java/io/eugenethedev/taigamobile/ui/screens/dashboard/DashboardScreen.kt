package io.eugenethedev.taigamobile.ui.screens.dashboard

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import io.eugenethedev.taigamobile.ui.components.HorizontalTabbedPager
import io.eugenethedev.taigamobile.ui.components.Tab
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithBackButton
import io.eugenethedev.taigamobile.ui.components.lists.SimpleTasksListWithTitle
import io.eugenethedev.taigamobile.ui.components.loaders.CircularLoader
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.commonVerticalMargin
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
import io.eugenethedev.taigamobile.ui.utils.navigateToTaskScreen
import io.eugenethedev.taigamobile.ui.utils.subscribeOnError

@ExperimentalPagerApi
@Composable
fun DashboardScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: DashboardViewModel = viewModel()
    remember {
        viewModel.start()
    }

    val workingOn by viewModel.workingOn.observeAsState()
    workingOn?.subscribeOnError(onError)

    val watching by viewModel.watching.observeAsState()
    watching?.subscribeOnError(onError)

    DashboardScreenContent(
        isLoading = workingOn?.resultStatus == ResultStatus.LOADING || watching?.resultStatus == ResultStatus.LOADING,
        workingOn = workingOn?.data.orEmpty(),
        watching = watching?.data.orEmpty(),
        navigateToTask = {
            viewModel.changeCurrentProject(it)
            navController.navigateToTaskScreen(it.id, it.taskType, it.ref)
        }
    )

}

@ExperimentalPagerApi
@Composable
fun DashboardScreenContent(
    isLoading: Boolean = false,
    workingOn: List<CommonTask> = emptyList(),
    watching: List<CommonTask> = emptyList(),
    navigateToTask: (CommonTask) -> Unit = {_ -> }
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    AppBarWithBackButton(title = { Text(stringResource(R.string.dashboard)) })

    if (isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularLoader()
        }
    } else {
        HorizontalTabbedPager(
            tabs = Tabs.values(),
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (Tabs.values()[page]) {
                Tabs.WorkingOn -> TabContent(
                    commonTasks = workingOn,
                    navigateToTask = navigateToTask
                )
                Tabs.Watching -> TabContent(
                    commonTasks = watching,
                    navigateToTask = navigateToTask
                )
            }
        }
    }

}

private enum class Tabs(@StringRes override val titleId: Int) : Tab {
    WorkingOn(R.string.working_on),
    Watching(R.string.watching)
}

@Composable
private fun TabContent(
    commonTasks: List<CommonTask>,
    navigateToTask: (CommonTask) -> Unit,
) = LazyColumn(Modifier.fillMaxSize()) {
    SimpleTasksListWithTitle(
        bottomMargin = commonVerticalMargin,
        horizontalPadding = mainHorizontalScreenPadding,
        showExtendedTaskInfo = true,
        commonTasks = commonTasks,
        navigateToTask = { id, _, _ -> navigateToTask(commonTasks.find { it.id == id }!!)  },
    )
}

@ExperimentalPagerApi
@Preview(showBackground = true)
@Composable
fun DashboardPreview() = TaigaMobileTheme {
    DashboardScreenContent()
}
