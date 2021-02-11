package io.eugenethedev.taigamobile.ui.screens.stories

import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.compose.navigate
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.screens.main.Routes

@Composable
fun StoriesScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: StoriesViewModel = viewModel()
    val projectName by viewModel.projectName.observeAsState()

    StoriesScreenContent(
        projectName = projectName!!,
        onTitleClick = {
            navController.navigate(Routes.projectsSelector) {

            }
        }
    )
}

@Composable
fun StoriesScreenContent(
    projectName: String,
    onTitleClick: () -> Unit = {}
) = Column(Modifier.fillMaxSize()) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(
                    onClick = onTitleClick,
                    interactionState = remember { InteractionState() },
                    indication = null
                )
            ) {
                Text(projectName.takeIf { it.isNotEmpty() } ?: stringResource(R.string.choose_project_title))
                Icon(imageVector = vectorResource(R.drawable.ic_arrow_down), contentDescription = "")
            }
        },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
    )
}

@Preview(showBackground = true)
@Composable
fun StoriesScreenContentPreview() = TaigaMobileTheme {
    StoriesScreenContent("")
}