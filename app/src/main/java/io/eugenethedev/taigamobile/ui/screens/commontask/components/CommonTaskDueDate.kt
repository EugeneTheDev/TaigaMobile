package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CommonTaskExtended
import io.eugenethedev.taigamobile.domain.entities.DueDateStatus
import io.eugenethedev.taigamobile.ui.components.pickers.DatePicker
import io.eugenethedev.taigamobile.ui.theme.*

fun LazyListScope.CommonTaskDueDate(
    commonTask: CommonTaskExtended
) {
    item {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .background(taigaGray, MaterialTheme.shapes.small)

        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .background(
                        color = when (commonTask.dueDateStatus) {
                            DueDateStatus.NotSet -> taigaDarkGray
                            DueDateStatus.Set -> taigaGreenPositive
                            DueDateStatus.DueSoon -> taigaOrange
                            DueDateStatus.PastDue -> taigaRed
                        },
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_clock),
                    contentDescription = null,
                    tint = commonTask.dueDate?.let { Color.White } ?: MaterialTheme.colors.primary,
                    modifier = Modifier.fillMaxSize()
                )
            }

            DatePicker(
                date = commonTask.dueDate,
                onDatePicked = {},
                hintId = R.string.no_due_date,
                modifier = Modifier.padding(6.dp)
            )

        }
    }
}
