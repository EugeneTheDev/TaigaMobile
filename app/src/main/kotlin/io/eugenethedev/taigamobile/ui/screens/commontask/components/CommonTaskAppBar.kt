package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.ui.components.dialogs.ConfirmActionDialog
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithBackButton
import io.eugenethedev.taigamobile.ui.screens.commontask.EditActions
import io.eugenethedev.taigamobile.ui.screens.commontask.NavigationActions

@Composable
fun CommonTaskAppBar(
    toolbarTitle: String,
    commonTaskType: CommonTaskType,
    showTaskEditor: () -> Unit,
    editActions: EditActions,
    navigationActions: NavigationActions
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    AppBarWithBackButton(
        title = {
            Text(
                text = toolbarTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            Box {
                IconButton(onClick = { isMenuExpanded = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_options),
                        contentDescription = null,
                        tint = MaterialTheme.colors.primary
                    )
                }

                // delete alert dialog
                var isDeleteAlertVisible by remember { mutableStateOf(false) }
                if (isDeleteAlertVisible) {
                    ConfirmActionDialog(
                        title = stringResource(R.string.delete_task_title),
                        text = stringResource(R.string.delete_task_text),
                        onConfirm = {
                            isDeleteAlertVisible = false
                            editActions.deleteTask()
                        },
                        onDismiss = { isDeleteAlertVisible = false }
                    )
                }

                // promote alert dialog
                var isPromoteAlertVisible by remember { mutableStateOf(false) }
                if (isPromoteAlertVisible) {
                    ConfirmActionDialog(
                        title = stringResource(R.string.promote_title),
                        text = stringResource(R.string.promote_text),
                        onConfirm = {
                            isPromoteAlertVisible = false
                            editActions.promoteTask()
                        },
                        onDismiss = { isPromoteAlertVisible = false }
                    )
                }

                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    // edit
                    DropdownMenuItem(
                        onClick = {
                            isMenuExpanded = false
                            showTaskEditor()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.edit),
                            style = MaterialTheme.typography.body1
                        )
                    }

                    // delete
                    DropdownMenuItem(
                        onClick = {
                            isMenuExpanded = false
                            isDeleteAlertVisible = true
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.delete),
                            style = MaterialTheme.typography.body1
                        )
                    }

                    // promote
                    if (commonTaskType == CommonTaskType.Task || commonTaskType == CommonTaskType.Issue) {
                        DropdownMenuItem(
                            onClick = {
                                isMenuExpanded = false
                                isPromoteAlertVisible = true
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.promote_to_user_story),
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }
                }
            }
        },
        navigateBack = navigationActions.navigateBack
    )
}
