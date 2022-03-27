package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTaskExtended
import io.eugenethedev.taigamobile.domain.entities.DueDateStatus
import io.eugenethedev.taigamobile.ui.components.pickers.DatePicker
import io.eugenethedev.taigamobile.ui.screens.commontask.EditActions
import io.eugenethedev.taigamobile.ui.theme.*
import io.eugenethedev.taigamobile.ui.utils.surfaceColorAtElevation

fun LazyListScope.CommonTaskDueDate(
    commonTask: CommonTaskExtended,
    editActions: EditActions
) {
    item {
        val background = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        val defaultIconBackground = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .background(background, shapes.medium)

        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .background(
                        color = when (commonTask.dueDateStatus) {
                            DueDateStatus.NotSet, DueDateStatus.NoLongerApplicable, null -> defaultIconBackground
                            DueDateStatus.Set -> taigaGreenPositive
                            DueDateStatus.DueSoon -> taigaOrange
                            DueDateStatus.PastDue -> taigaRed
                        }.takeUnless { editActions.editDueDate.isResultLoading } ?: defaultIconBackground,
                        shape = shapes.medium
                    )
                    .padding(4.dp)
            ) {
                if (editActions.editDueDate.isResultLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.fillMaxSize().padding(2.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_clock),
                        contentDescription = null,
                        tint = commonTask.dueDate?.let { MaterialTheme.colorScheme.onSurface } ?: MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            DatePicker(
                date = commonTask.dueDate,
                onDatePicked = { editActions.editDueDate.select(it) },
                hintId = R.string.no_due_date,
                modifier = Modifier.padding(6.dp)
            )
        }
    }
}
