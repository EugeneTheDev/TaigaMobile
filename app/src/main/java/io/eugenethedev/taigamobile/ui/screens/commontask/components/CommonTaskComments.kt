package io.eugenethedev.taigamobile.ui.screens.commontask.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.eugenethedev.taigamobile.R
import io.eugenethedev.taigamobile.domain.entities.Comment
import io.eugenethedev.taigamobile.ui.components.loaders.DotsLoader
import io.eugenethedev.taigamobile.ui.components.texts.SectionTitle
import io.eugenethedev.taigamobile.ui.screens.commontask.EditActions

fun LazyListScope.CommonTaskComments(
    comments: List<Comment>,
    editActions: EditActions
) {
    item {
        SectionTitle(stringResource(R.string.comments_template).format(comments.size))
    }

    itemsIndexed(comments) { index, item ->
        CommentItem(
            comment = item,
            onDeleteClick = { editActions.editComments.deleteComment(item) }
        )

        if (index < comments.lastIndex) {
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.LightGray
            )
        }
    }

    item {
        if (editActions.editComments.isResultLoading) {
            DotsLoader()
        }
    }
}
