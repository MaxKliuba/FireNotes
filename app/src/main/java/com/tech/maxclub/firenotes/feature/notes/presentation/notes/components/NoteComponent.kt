package com.tech.maxclub.firenotes.feature.notes.presentation.notes.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tech.maxclub.firenotes.R
import com.tech.maxclub.firenotes.core.utils.formatDate
import com.tech.maxclub.firenotes.feature.notes.domain.models.NoteWithItemsPreview
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteComponent(
    note: NoteWithItemsPreview,
    isDragging: Boolean,
    onExpandedChange: (String, Boolean) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember {
        mutableStateOf(note.expanded)
    }

    val cardColors = if (isDragging) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    } else {
        CardDefaults.cardColors()
    }

    Card(
        onClick = { onEdit(note.id) },
        colors = cardColors,
        modifier = modifier.padding(8.dp)
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, top = 16.dp, end = 48.dp, bottom = 16.dp)
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(12.dp))

                val itemsCountText = pluralStringResource(
                    id = R.plurals.item_plural,
                    count = note.itemsCount,
                    note.itemsCount,
                )
                Text(
                    text = "$itemsCountText   |   ${formatDate(Date(note.timestamp))}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            if (isDragging) {
                Icon(
                    imageVector = Icons.Default.DragIndicator,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(12.dp)
                )
            } else {
                IconButton(
                    onClick = { onDelete(note.id) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 4.dp)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.delete_note_button)
                    )
                }

                if (note.previewItems.isNotEmpty()) {
                    IconToggleButton(
                        checked = expanded,
                        onCheckedChange = {
                            expanded = it
                            onExpandedChange(note.id, it)
                        },
                        colors = IconButtonDefaults.iconToggleButtonColors(
                            checkedContentColor = LocalContentColor.current
                        ),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 4.dp, end = 4.dp)
                            .size(40.dp)
                    ) {
                        if (expanded) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropUp,
                                contentDescription = stringResource(R.string.shrink_preview_button),
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = stringResource(R.string.expand_preview_button),
                            )
                        }
                    }
                }
            }
        }

        val isPreviewVisible = note.previewItems.isNotEmpty() && expanded

        if (isPreviewVisible) {
            Divider(
                color = LocalContentColor.current,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        AnimatedVisibility(visible = isPreviewVisible) {
            NoteItemsPreviewList(
                noteItems = note.previewItems,
                isEllipsis = note.previewItems.size < note.itemsCount,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 14.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
            )
        }
    }
}