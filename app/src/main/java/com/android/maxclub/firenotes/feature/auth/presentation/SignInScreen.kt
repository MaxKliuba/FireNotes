package com.android.maxclub.firenotes.feature.auth.presentation

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
fun SignInScreen(
    isSigningIn: Boolean,
    onSignIn: () -> Unit
) {
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
                    isSigningIn = isSigningIn,
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
                    isSigningIn = isSigningIn,
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
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_app_logo),
            contentDescription = stringResource(id = R.string.app_name),
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
        )
    }
}

@Composable
fun SignInSection(
    isSigningIn: Boolean,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Button(
            onClick = onSignIn,
            modifier = Modifier.align(Alignment.Center)
        ) {
            if (isSigningIn) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(imageVector = Icons.Default.Login, contentDescription = null)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.sign_in_button))
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