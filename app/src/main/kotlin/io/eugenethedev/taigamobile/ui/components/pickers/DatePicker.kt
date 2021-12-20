package io.eugenethedev.taigamobile.ui.components.pickers

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.google.android.material.datepicker.MaterialDatePicker
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.ui.utils.activity
import io.eugenethedev.taigamobile.ui.utils.clickableUnindicated
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Date picker with material dialog. Null passed to onDatePicked() means selection was cleared
 */

@Composable
fun DatePicker(
    date: LocalDate?,
    onDatePicked: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    @StringRes hintId: Int = R.string.date_hint,
    showClearButton: Boolean = true,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    onClose: () -> Unit = {},
    onOpen: () -> Unit = {}
) = Box {
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }

    val dialog = MaterialDatePicker.Builder
        .datePicker()
        .setTitleText(R.string.select_date)
        .setTheme(R.style.DatePicker)
        .setSelection(
            date?.atStartOfDay(ZoneOffset.UTC)
                ?.toInstant()
                ?.toEpochMilli()
        )
        .build()
        .apply {
            addOnDismissListener { onClose() }
            addOnPositiveButtonClickListener {
                onDatePicked(
                    Instant.ofEpochMilli(it)
                        .atOffset(ZoneOffset.UTC)
                        .toLocalDate()
                )
            }
        }

    val fragmentManager = LocalContext.current.activity.supportFragmentManager

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {

        Text(
            text = date?.format(dateFormatter) ?: stringResource(hintId),
            style = style,
            modifier = Modifier.clickableUnindicated {
                onOpen()
                dialog.show(fragmentManager, dialog.toString())
            },
            color = date?.let { MaterialTheme.colorScheme.onSurface } ?: MaterialTheme.colorScheme.outline
        )

        if (showClearButton && date != null) { // do not show clear button if there is no date (sounds right to me)
            Spacer(Modifier.width(4.dp))

            IconButton(
                onClick = { onDatePicked(null) },
                modifier = Modifier.size(22.dp).clip(CircleShape)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_remove),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
