package com.monopoly.game.ui.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.monopoly.game.ui.game.setup.GameSetupScreen
import com.monopoly.game.ui.game.board.GameBoardScreen
import com.monopoly.game.ui.theme.MonopolyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    companion object {
        const val EXTRA_IS_HOST = "extra_is_host"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val isHost = intent.getBooleanExtra(EXTRA_IS_HOST, false)
        viewModel.setIsHost(isHost)

        setContent {
            MonopolyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    if (!viewModel.gameStarted.value) {
                        // Show game setup screen
                        GameSetupScreen(
                            isHost = isHost,
                            onStartGame = { playerName, gameCode ->
                                viewModel.startGame(playerName, gameCode)
                            }
                        )
                    } else {
                        // Show game board screen
                        GameBoardScreen(
                            viewModel = viewModel,
                            onExitGame = { finish() }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        viewModel.cleanupResources()
        super.onDestroy()
    }
} 