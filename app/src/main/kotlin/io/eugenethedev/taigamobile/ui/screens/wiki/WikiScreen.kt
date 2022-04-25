package io.eugenethedev.taigamobile.ui.screens.wiki

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithSearch
import io.eugenethedev.taigamobile.ui.components.dialogs.ConfirmActionDialog
import io.eugenethedev.taigamobile.ui.screens.main.Routes
import io.eugenethedev.taigamobile.ui.theme.dialogTonalElevation
import io.eugenethedev.taigamobile.ui.utils.surfaceColorAtElevation

@Composable
fun WikiScreen(
    navController: NavController,
    showMessage: (message: Int) -> Unit = {},
) {
    val viewModel: WikiViewModel = viewModel()

    WikiContentScreen(
        pageName = "Some page",
        onTitleClick = { navController.navigate(Routes.projectsSelector) },
        navigateBack = navController::popBackStack
    )
}

@Composable
fun WikiContentScreen(
    pageName: String,
    onTitleClick: () -> Unit = {},
    navigateBack: () -> Unit = {},
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.Start
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    AppBarWithSearch(
        projectName = pageName,
        actions = {
            IconButton(onClick = { isMenuExpanded = true }) {
                Icon(
                    painter = painterResource(R.drawable.ic_options),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            var isDeleteAlertVisible by remember { mutableStateOf(false) }
            if (isDeleteAlertVisible) {
                ConfirmActionDialog(
                    title = stringResource(R.string.delete_wiki_title),
                    text = stringResource(R.string.delete_wiki_text),
                    onConfirm = {
                        isDeleteAlertVisible = false
                        // delete action
                    },
                    onDismiss = { isDeleteAlertVisible = false },
                    iconId = R.drawable.ic_delete
                )
            }
            DropdownMenu(
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.surfaceColorAtElevation(dialogTonalElevation)
                ),
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        isMenuExpanded = false
                        isDeleteAlertVisible = true
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.delete),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                )
            }
        },
        onTitleClick = onTitleClick,
        navigateBack = navigateBack
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun WikiScreenPreview() {
    WikiContentScreen(
        pageName = "Some page"
    )
}

