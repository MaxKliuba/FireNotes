package com.android.maxclub.firenotes.feature.notes.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.android.maxclub.firenotes.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteTopAppBar(
    userPhotoUrl: String?,
    onClickUserPhoto: () -> Unit,
    isDeleteIconVisible: Boolean,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                fontWeight = FontWeight.Bold,
            )
        },
        navigationIcon = {
            IconButton(onClick = onClickUserPhoto) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = userPhotoUrl,
                        error = painterResource(id = R.drawable.ic_user_24),
                    ),
                    colorFilter = if (userPhotoUrl == null) {
                        ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
                    } else {
                        null
                    },
                    contentDescription = stringResource(R.string.menu_title),
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                        ),
                    contentScale = ContentScale.Crop,
                )
            }
        },
        actions = {
            if (isDeleteIconVisible) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_note_text),
                    )
                }
            }
        },
        modifier = modifier
    )
}