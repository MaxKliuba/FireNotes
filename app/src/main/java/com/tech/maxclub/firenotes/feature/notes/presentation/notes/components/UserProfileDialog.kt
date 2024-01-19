package com.tech.maxclub.firenotes.feature.notes.presentation.notes.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.tech.maxclub.firenotes.BuildConfig
import com.tech.maxclub.firenotes.R
import com.tech.maxclub.firenotes.feature.auth.domain.models.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileDialog(
    user: User?,
    notesCount: Int,
    onSignOut: (Boolean) -> Unit,
    onDeleteAccount: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Surface(
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(PaddingValues(all = 24.dp))
            ) {
                Row {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = user?.photoUrl,
                            error = painterResource(id = R.drawable.ic_user_24),
                        ),
                        colorFilter = if (user?.photoUrl == null) {
                            ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
                        } else {
                            null
                        },
                        contentDescription = stringResource(R.string.profile_button),
                        modifier = Modifier
                            .size(86.dp)
                            .clip(CircleShape)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape,
                            ),
                        contentScale = ContentScale.Crop,
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user?.name.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(start = 20.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = pluralStringResource(
                                id = R.plurals.note_plural,
                                count = notesCount,
                                notesCount,
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 20.dp)
                        )

                        TextButton(
                            onClick = {
                                onDeleteAccount()
                                onDismiss()
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.delete_account_button),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = {
                        user?.let { onSignOut(it.isAnonymous) }
                        onDismiss()
                    }
                ) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.sign_out_button))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${stringResource(id = R.string.app_name)} v${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }
        }
    }
}