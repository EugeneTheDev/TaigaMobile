package io.eugenethedev.taigamobile.ui.screens.projectselector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.ui.components.SlideAnimView
import io.eugenethedev.taigamobile.ui.theme.TaigaMobileTheme

@Composable
fun ProjectSelectorScreen(
    navController: NavController,
    onError: @Composable (message: Int) -> Unit = {},
) {
    ProjectSelectorScreenContent(
        navigateBack = { navController.popBackStack() }
    )
}


@Composable
fun ProjectSelectorScreenContent(
    navigateBack: () -> Unit = {}
) = SlideAnimView(navigateBack = navigateBack) {
    Box(Modifier.fillMaxSize().background(Color.Yellow))
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() = TaigaMobileTheme {
    ProjectSelectorScreenContent()
}

