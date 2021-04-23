package io.eugenethedev.taigamobile.ui.screens.issues

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.isFocused
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTask
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.ui.commons.ResultStatus
import io.eugenethedev.taigamobile.ui.components.lists.CommonTaskItem
import io.eugenethedev.taigamobile.ui.components.buttons.PlusButton
import io.eugenethedev.taigamobile.ui.components.appbars.ProjectAppBar
import io.eugenethedev.taigamobile.ui.components.loaders.DotsLoader
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.theme.mainHorizontalScreenPadding
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
        isLoading = issues?.resultStatus == ResultStatus.LOADING,
        issues = issues?.data.orEmpty(),
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
    navigateToTask: NavigateToTask = { _, _, _ -> },
    loadIssues: (query: String) -> Unit = {}
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    ProjectAppBar(
        projectName = projectName,
        actions = { PlusButton(onClick = navigateToCreateTask) },
        onTitleClick = onTitleClick
    )

    var query by remember { mutableStateOf(TextFieldValue()) }

    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = mainHorizontalScreenPadding, vertical = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                val textPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)

                if (query.text.isEmpty()) {
                    Text(
                        text = stringResource(R.string.issues_search_hint),
                        style = MaterialTheme.typography.body1,
                        color = Color.Gray,
                        modifier = Modifier.padding(textPadding)
                    )
                }

                val primaryColor = MaterialTheme.colors.primary
                var outlineColor by remember { mutableStateOf(Color.Gray) }

                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth()
                        .onFocusChanged { outlineColor = if (it.isFocused) primaryColor else Color.Gray }
                        .border(width = 2.dp, color = outlineColor, shape = MaterialTheme.shapes.medium )
                        .padding(textPadding),
                    textStyle = MaterialTheme.typography.body1.merge(TextStyle(color = MaterialTheme.colors.onSurface)),
                    cursorBrush = SolidColor(MaterialTheme.colors.onSurface),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { loadIssues(query.text) })
                )
            }
        }

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
            if (isLoading) {
                DotsLoader()
            }

            LaunchedEffect(issues.size) {
                loadIssues(query.text)
            }
            Spacer(Modifier.height(16.dp))
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
