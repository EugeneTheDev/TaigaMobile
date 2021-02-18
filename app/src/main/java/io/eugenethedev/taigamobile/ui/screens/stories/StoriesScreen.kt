package io.eugenethedev.taigamobile.ui.screens.stories

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.compose.navigate
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.utils.clickableUnindicated

@Composable
fun StoriesScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {},
) {
    val viewModel: StoriesViewModel = viewModel()
    remember {
        viewModel.onScreenOpen()
        null
    }

    StoriesScreenContent(
        projectName = viewModel.projectName,
        onTitleClick = { navController.navigate(Routes.projectsSelector) }
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
                modifier = Modifier.clickableUnindicated(onClick = onTitleClick)
            ) {
                Text(
                    text = projectName.takeIf { it.isNotEmpty() } ?: stringResource(R.string.choose_project_title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.preferredWidthIn(max = 250.dp)
                )
                Icon(
                    imageVector = vectorResource(R.drawable.ic_arrow_down),
                    contentDescription = null
                )
            }
        },
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp
    )
}

@Preview(showBackground = true)
@Composable
fun StoriesScreenPreview() = TaigaMobileTheme {
    StoriesScreenContent("")
}