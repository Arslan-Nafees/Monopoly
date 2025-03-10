package com.monopoly.game.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.monopoly.game.R
import com.monopoly.game.ui.game.GameActivity
import com.monopoly.game.ui.theme.MonopolyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MonopolyTheme {
                MainScreen(
                    onStartNewGame = { showGameSetup(isHost = true) },
                    onJoinGame = { showGameSetup(isHost = false) }
                )
            }
        }
    }

    private fun showGameSetup(isHost: Boolean) {
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra(GameActivity.EXTRA_IS_HOST, isHost)
        }
        startActivity(intent)
    }
}

@Composable
fun MainScreen(
    onStartNewGame: () -> Unit,
    onJoinGame: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Logo and title
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.h2.copy(
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(60.dp))
            
            // Menu buttons
            MainMenuButton(
                text = stringResource(R.string.start_new_game),
                onClick = onStartNewGame
            )
            
            MainMenuButton(
                text = stringResource(R.string.join_game),
                onClick = onJoinGame
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Version info
            Text(
                text = "Version 1.0",
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun MainMenuButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 32.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary
        )
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
} 