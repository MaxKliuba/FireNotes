package com.tech.maxclub.firenotes.feature.notes.presentation.notes.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tech.maxclub.firenotes.R
import com.tech.maxclub.firenotes.feature.notes.domain.models.NoteItem

@Composable
fun NoteItemsPreviewList(
    noteItems: List<NoteItem>,
    isEllipsis: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        noteItems.forEach { noteItem ->
            when (noteItem) {
                is NoteItem.Text -> {
                    Text(
                        text = noteItem.content,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }

                is NoteItem.ToDo -> {
                    Row {
                        if (noteItem.checked) {
                            Icon(
                                imageVector = Icons.Default.CheckBox,
                                contentDescription = stringResource(R.string.checked_label),
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CheckBoxOutlineBlank,
                                contentDescription = stringResource(R.string.not_checked_label),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = noteItem.content,
                            style = MaterialTheme.typography.bodyMedium,
                            textDecoration = if (noteItem.checked) {
                                TextDecoration.LineThrough
                            } else {
                                TextDecoration.None
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (isEllipsis) {
            Text(
                text = stringResource(R.string.preview_ellipsis),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 2.dp, bottom = 8.dp)
            )
        }
    }
}