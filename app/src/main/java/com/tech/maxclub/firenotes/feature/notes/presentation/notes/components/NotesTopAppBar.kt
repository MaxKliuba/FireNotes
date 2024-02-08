package com.tech.maxclub.firenotes.feature.notes.presentation.notes.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.tech.maxclub.firenotes.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesTopAppBar(
    userPhotoUrl: String?,
    onClickUserProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = {}) {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_logo),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        title = {
            Text(text = stringResource(id = R.string.app_name))
        },
        actions = {
            IconButton(onClick = onClickUserProfile) {
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
                    contentDescription = stringResource(R.string.profile_button),
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
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        modifier = modifier
    )
}