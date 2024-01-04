package com.android.maxclub.firenotes.feature.auth.presentation

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Note
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.maxclub.firenotes.R

@Composable
fun SignInScreen(onSignIn: () -> Unit) {
    val configuration = LocalConfiguration.current

    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            Row {
                AppLogo(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
                SignInSection(
                    onSignIn = onSignIn,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
            }
        }

        else -> {
            Column {
                AppLogo(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                SignInSection(
                    onSignIn = onSignIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }
}

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Icon(
            imageVector = Icons.Default.Note,
            contentDescription = stringResource(id = R.string.app_name),
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
fun SignInSection(
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Button(
            onClick = onSignIn,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(imageVector = Icons.Default.Login, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.sign_in_text))
        }

        Image(
            painter = painterResource(id = R.drawable.built_with_firebase_logo),
            contentDescription = null,
            modifier = Modifier
                .padding(16.dp)
                .width(100.dp)
                .align(Alignment.BottomCenter)
        )
    }
}