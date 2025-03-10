package com.monopoly.game.ui.game.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.monopoly.game.R

@Composable
fun GameSetupScreen(
    isHost: Boolean,
    onStartGame: (playerName: String, gameCode: String?) -> Unit
) {
    var playerName by remember { mutableStateOf("") }
    var gameCode by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = if (isHost) {
                stringResource(R.string.create_game)
            } else {
                stringResource(R.string.join_game)
            },
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Player name input
        OutlinedTextField(
            value = playerName,
            onValueChange = { playerName = it },
            label = { Text(stringResource(R.string.player_name)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            )
        )

        // Game code input (only for joining)
        if (!isHost) {
            OutlinedTextField(
                value = gameCode,
                onValueChange = { 
                    // Convert to uppercase and limit to 6 characters
                    gameCode = it.uppercase().take(6)
                },
                label = { Text(stringResource(R.string.join_game_code)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                )
            )
        }

        // Error message
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Start/Join button
        Button(
            onClick = {
                when {
                    playerName.isBlank() -> {
                        error = "Please enter your name"
                    }
                    !isHost && gameCode.length != 6 -> {
                        error = "Please enter a valid game code"
                    }
                    else -> {
                        error = null
                        onStartGame(playerName, if (isHost) null else gameCode)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary
            )
        ) {
            Text(
                text = if (isHost) {
                    stringResource(R.string.create_game)
                } else {
                    stringResource(R.string.join_game)
                },
                color = MaterialTheme.colors.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
} 