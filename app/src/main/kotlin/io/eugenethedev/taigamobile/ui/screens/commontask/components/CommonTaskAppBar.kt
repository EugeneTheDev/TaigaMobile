package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTaskType
import io.eugenethedev.taigamobile.ui.components.dialogs.ConfirmActionDialog
import io.eugenethedev.taigamobile.ui.components.appbars.AppBarWithBackButton
import io.eugenethedev.taigamobile.ui.components.editors.TextFieldWithHint
import io.eugenethedev.taigamobile.ui.screens.commontask.EditActions
import io.eugenethedev.taigamobile.ui.screens.commontask.NavigationActions
import io.eugenethedev.taigamobile.ui.theme.dialogTonalElevation
import io.eugenethedev.taigamobile.ui.utils.surfaceColorAtElevation

@Composable
fun CommonTaskAppBar(
    toolbarTitle: String,
    toolbarSubtitle: String,
    commonTaskType: CommonTaskType,
    isBlocked: Boolean,
    editActions: EditActions,
    navigationActions: NavigationActions,
    url: String,
    showTaskEditor: () -> Unit,
    showMessage: (message: Int) -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
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
                    style = MaterialTheme.typography.bodySmall
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
                            editActions.deleteTask.select(Unit)
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
                            editActions.promoteTask.select(Unit)
                        },
                        onDismiss = { isPromoteAlertVisible = false },
                        iconId = R.drawable.ic_arrow_upward
                    )
                }

                // block item dialog
                var isBlockDialogVisible by remember { mutableStateOf(false) }
                if (isBlockDialogVisible) {
                    BlockDialog(
                        onConfirm = {
                            editActions.editBlocked.select(it)
                            isBlockDialogVisible = false
                        },
                        onDismiss = { isBlockDialogVisible = false }
                    )
                }

                DropdownMenu(
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(dialogTonalElevation)
                    ),
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    // Copy link
                    DropdownMenuItem(
                        onClick = {
                            isMenuExpanded = false
                            clipboardManager.setText(
                                AnnotatedString(url)
                            )
                            showMessage(R.string.copy_link_successfully)
                        },
                        text = {
                            Text(
                                text = stringResource(R.string.copy_link),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    )

                    // edit
                    DropdownMenuItem(
                        onClick = {
                            isMenuExpanded = false
                            showTaskEditor()
                        },
                        text = {
                            Text(
                                text = stringResource(R.string.edit),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    )

                    // delete
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

                    // promote
                    if (commonTaskType == CommonTaskType.Task || commonTaskType == CommonTaskType.Issue) {
                        DropdownMenuItem(
                            onClick = {
                                isMenuExpanded = false
                                isPromoteAlertVisible = true
                            },
                            text = {
                                Text(
                                    text = stringResource(R.string.promote_to_user_story),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        )
                    }

                    DropdownMenuItem(
                        onClick = {
                            isMenuExpanded = false
                            if (isBlocked) {
                                editActions.editBlocked.remove(Unit)
                            } else {
                                isBlockDialogVisible = true
                            }
                        },
                        text = {
                            Text(
                                text = stringResource(if (isBlocked) R.string.unblock else R.string.block),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    )
                }
            }
        },
        navigateBack = navigationActions.navigateBack
    )
}

@Composable
private fun BlockDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var reason by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(reason.text) }) {
                Text(
                    text = stringResource(R.string.ok),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.block),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            TextFieldWithHint(
                hintId = R.string.block_reason,
                value = reason,
                onValueChange = { reason = it },
                minHeight = with(LocalDensity.current) { MaterialTheme.typography.bodyLarge.fontSize.toDp() * 4 },
                contentAlignment = Alignment.TopStart
            )
        }
    )
}