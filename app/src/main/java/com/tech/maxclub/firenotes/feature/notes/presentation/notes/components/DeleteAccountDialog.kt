package com.tech.maxclub.firenotes.feature.notes.presentation.notes.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tech.maxclub.firenotes.R

@Composable
fun DeleteAccountDialog(
    isDeleting: Boolean,
    onDeleteAccount: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = null) },
        title = {
            Text(text = stringResource(R.string.delete_account_title))
        },
        text = {
            Text(text = stringResource(R.string.delete_account_text))
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.delete_account_cancel_button))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteAccount) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.error,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.width(24.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.delete_account_confirm_button),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        modifier = modifier
    )
}