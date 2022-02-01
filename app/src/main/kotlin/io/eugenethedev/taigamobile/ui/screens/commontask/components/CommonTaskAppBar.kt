package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.ui.components.dialogs.ConfirmActionDialog
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithBackButton
import io.eugenethedev.taigamobile.ui.screens.commontask.EditActions
import io.eugenethedev.taigamobile.ui.screens.commontask.NavigationActions
import io.eugenethedev.taigamobile.ui.theme.dialogTonalElevation
import io.eugenethedev.taigamobile.ui.utils.surfaceColorAtElevation

@Composable
fun CommonTaskAppBar(
    toolbarTitle: String,
    toolbarSubtitle: String,
    commonTaskType: CommonTaskType,
    showTaskEditor: () -> Unit,
    editActions: EditActions,
    navigationActions: NavigationActions
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    AppBarWithBackButton(
        title = {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = toolbarTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = toolbarSubtitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Light,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize
                )
            }
        },
        actions = {
            Box {
                IconButton(onClick = { isMenuExpanded = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_options),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
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
                        onDismiss = { isDeleteAlertVisible = false },
                        iconId = R.drawable.ic_delete
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
                        onDismiss = { isPromoteAlertVisible = false },
                        iconId = R.drawable.ic_arrow_upward
                    )
                }

                DropdownMenu(
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(dialogTonalElevation)
                    ),
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
                            style = MaterialTheme.typography.bodyLarge
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
                            style = MaterialTheme.typography.bodyLarge
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
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        },
        navigateBack = navigationActions.navigateBack
    )
}
