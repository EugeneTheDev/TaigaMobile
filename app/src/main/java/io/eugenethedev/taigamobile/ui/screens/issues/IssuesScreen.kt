package io.eugenethedev.taigamobile.ui.screens.issues

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import io.eugenethedev.taigamobile.ui.components.lists.CommonTaskItem
import io.eugenethedev.taigamobile.ui.components.PlusButton
import io.eugenethedev.taigamobile.ui.components.appbars.ProjectAppBar
import io.eugenethedev.taigamobile.ui.components.loaders.CircularLoader
import io.eugenethedev.taigamobile.ui.components.loaders.DotsLoader
import io.eugenethedev.taigamobile.ui.components.texts.NothingToSeeHereText
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.utils.*

@Composable
fun IssuesScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: IssuesViewModel = viewModel()
    remember {
        viewModel.start()
        null
    }

    val issues by viewModel.issues.observeAsState()
    issues?.subscribeOnError(onError)

    IssuesScreenContent(
        projectName = viewModel.projectName,
        onTitleClick = {
            navController.navigate(Routes.projectsSelector)
            viewModel.reset()
        },
        navigateToCreateTask = { navController.navigateToCreateTaskScreen(CommonTaskType.ISSUE) },
        isLoading = issues?.resultStatus == ResultStatus.LOADING && issues?.data.isNullOrEmpty(),
        issues = issues?.data.orEmpty(),
        isIssuesLoading = issues?.resultStatus == ResultStatus.LOADING,
        navigateToTask = navController::navigateToTaskScreen,
        loadIssues = viewModel::loadIssues
    )
}

@Composable
fun IssuesScreenContent(
    projectName: String,
    onTitleClick: () -> Unit = {},
    navigateToCreateTask: () -> Unit = {},
    isLoading: Boolean = false,
    issues: List<CommonTask> = emptyList(),
    isIssuesLoading: Boolean = false,
    navigateToTask: NavigateToTask = { _, _, _ -> },
    loadIssues: () -> Unit = {}
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    ProjectAppBar(
        projectName = projectName,
        actions = { PlusButton(onClick = navigateToCreateTask) },
        onTitleClick = onTitleClick
    )

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularLoader()
            }
        }
        issues.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                NothingToSeeHereText()
            }
        }
        else -> {
            LazyColumn(Modifier.fillMaxWidth()) {
                itemsIndexed(issues) { index, item ->
                    CommonTaskItem(
                        commonTask = item,
                        navigateToTask = navigateToTask
                    )

                    if (index < issues.lastIndex) {
                        Divider(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.LightGray
                        )
                    }
                }

                item {
                    if (isIssuesLoading) {
                        DotsLoader()
                    }

                    LaunchedEffect(issues.size) {
                        loadIssues()
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun IssuesScreenPreview() = TaigaMobileTheme {
    IssuesScreenContent(
        projectName = "Cool project"
    )
}
