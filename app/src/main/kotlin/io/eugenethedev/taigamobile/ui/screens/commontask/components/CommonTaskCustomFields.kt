package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.CustomField
import io.eugenethedev.taigamobile.domain.entities.CustomFieldValue
import io.eugenethedev.taigamobile.ui.components.loaders.DotsLoader
import io.eugenethedev.taigamobile.ui.components.texts.SectionTitle
import io.eugenethedev.taigamobile.ui.screens.commontask.EditActions
import io.eugenethedev.taigamobile.ui.screens.commontask.Loaders

fun LazyListScope.CommonTaskCustomFields(
    customFields: List<CustomField>,
    customFieldsValues: Map<Long, CustomFieldValue?>,
    onValueChange: (Long, CustomFieldValue?) -> Unit,
    editActions: EditActions,
    loaders: Loaders
) {
    item {
        SectionTitle(text = stringResource(R.string.custom_fields))
    }

    itemsIndexed(customFields) { index, item ->
        CustomField(
            customField = item,
            value = customFieldsValues[item.id],
            onValueChange = { onValueChange(item.id, it) },
            onSaveClick = { editActions.editCustomField(item, customFieldsValues[item.id]) }
        )

        if (index < customFields.lastIndex) {
            Divider(
                modifier = Modifier.padding(top = 16.dp, bottom = 12.dp),
                color = MaterialTheme.colorScheme.outline
            )
        }
    }

    item {
        if (loaders.isCustomFieldsLoading) {
            Spacer(Modifier.height(8.dp))
            DotsLoader()
        }
    }
}
