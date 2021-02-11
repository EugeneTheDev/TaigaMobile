package io.eugenethedev.taigamobile.ui.screens.projectselector

import androidx.compose.foundation.Image
import androidx.compose.foundation.Interaction
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Project
import io.eugenethedev.taigamobile.ui.components.SlideAnimView
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.ui.utils.Status

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

    SlideAnimView(navigateBack = navController::popBackStack) {
        ProjectSelectorScreenContent(
            projects = projects!!,
            navigateBack = it,
            isLoading = projectsResult?.status == Status.LOADING,
            loadMore = { viewModel.loadData() }
        )
    }
}


@Composable
fun ProjectSelectorScreenContent(
    projects: Set<Project>,
    isLoading: Boolean = false,
    navigateBack: () -> Unit = {},
    loadMore: () -> Unit = {}
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    TopAppBar(
        title = {},
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
                modifier = Modifier.clickable(
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
        CircularProgressIndicator(Modifier.size(40.dp))
    }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(projects.toList()) { index, item ->
            Text(item.name)

            if (isLoading && index == projects.size - 1) {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.size(40.dp))
                }
            }

            onActive {
                if (index == projects.size - 1) {
                    loadMore()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginPreview() = TaigaMobileTheme {
    ProjectSelectorScreenContent(
        setOf(
            Project(0, "Cool"),
            Project(1, "Cooler")
        )
    )
}

