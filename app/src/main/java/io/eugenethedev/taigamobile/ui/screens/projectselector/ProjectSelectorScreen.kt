package io.eugenethedev.taigamobile.ui.screens.projectselector

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Project
import io.eugenethedev.taigamobile.ui.components.ContainerBox
import io.eugenethedev.taigamobile.ui.components.SlideAnimView
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.utils.Status
import timber.log.Timber

@Composable
fun ProjectSelectorScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: ProjectSelectorViewModel = viewModel()
    remember {
        viewModel.onScreenOpen()
        null
    }

    val projectsResult by viewModel.projectsResult.observeAsState()
    projectsResult?.takeIf { it.status == Status.ERROR }?.let { onError(it.message!!) }
    val projects by viewModel.projects.observeAsState()
    val isProjectSelected by viewModel.isProjectSelected.observeAsState()

    var queryInput by remember { mutableStateOf(TextFieldValue()) }

    SlideAnimView(navigateBack = navController::popBackStack) {
        if (isProjectSelected!!) {
            it()
        }

        ProjectSelectorScreenContent(
            projects = projects!!,
            navigateBack = it,
            isLoading = projectsResult?.status == Status.LOADING,
            query = queryInput,
            onQueryChanged = { queryInput = it },
            loadMore = { viewModel.loadData(queryInput.text) },
            selectProject = { viewModel.onProjectSelected(it) }
        )
    }
}


@Composable
fun ProjectSelectorScreenContent(
    projects: Set<Project>,
    isLoading: Boolean = false,
    query: TextFieldValue = TextFieldValue(),
    onQueryChanged: (TextFieldValue) -> Unit = {},
    navigateBack: () -> Unit = {},
    loadMore: () -> Unit = {},
    selectProject: (Project) -> Unit  = {}
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.text.isEmpty()) {
                    Text(
                        text = stringResource(R.string.search_projects_hint),
                        style = MaterialTheme.typography.body1,
                        color = Color.Gray
                    )
                }

                BasicTextField(
                    value = query,
                    onValueChange = onQueryChanged,
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    textStyle = MaterialTheme.typography.body1.merge(TextStyle(color = MaterialTheme.colors.onSurface)),
                    cursorColor = MaterialTheme.colors.onSurface,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    onImeActionPerformed = {
                        if (it == ImeAction.Search) {
                            loadMore()
                        }
                    }
                )
            }
        },
        navigationIcon = {
            val interactionState = remember { InteractionState() }

            Image(
                imageVector = vectorResource(R.drawable.ic_arrow_back),
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    if (Interaction.Pressed in interactionState.value) {
                        MaterialTheme.colors.primaryVariant
                    } else {
                        MaterialTheme.colors.primary
                    }
                ),
                modifier = Modifier
                    .size(36.dp)
                    .padding(start = 8.dp)
                    .clickable(
                        interactionState = interactionState,
                        indication = null,
                        onClick = navigateBack
                    )
            )
        },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 1.dp
    )

    if (isLoading && projects.isEmpty()) {
        Loader()
    }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(projects.toList()) { index, item ->
            ItemProject(
                projectName = item.name,
                onClick = { selectProject(item) }
            )

            if (index < projects.size - 1) {
                Divider(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = Color.LightGray
                )
            }

            if (index == projects.size - 1) {
                if (isLoading) {
                    Loader()
                }
                Spacer(Modifier.height(6.dp))
            }

            onActive {
                if (index == projects.size - 1) {
                    loadMore()
                }
            }
        }
    }
}

@Composable
private fun ItemProject(
    projectName: String,
    onClick: () -> Unit = {}
) = ContainerBox(onClick = onClick) {
    Text(
        text = projectName,
        style = MaterialTheme.typography.body1,
    )
}

@Composable
private fun Loader() = CircularProgressIndicator(Modifier
    .size(36.dp)
    .padding(4.dp))


@Preview(showBackground = true)
@Composable
fun ProjectSelectorScreenPreview() = TaigaMobileTheme {
    ProjectSelectorScreenContent(
        setOf(
            Project(0, "Cool"),
            Project(1, "Cooler")
        )
    )
}

