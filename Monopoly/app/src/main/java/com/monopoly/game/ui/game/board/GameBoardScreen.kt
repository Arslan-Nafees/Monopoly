package com.monopoly.game.ui.game.board

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.monopoly.game.R
import com.monopoly.game.data.model.GameState
import com.monopoly.game.data.model.Player
import com.monopoly.game.data.model.Property
import com.monopoly.game.ui.game.GameViewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun GameBoardScreen(
    viewModel: GameViewModel,
    onExitGame: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    val currentPlayer by viewModel.currentPlayer
    val players by viewModel.players.collectAsState()
    val properties by viewModel.properties.collectAsState()
    val selectedProperty by viewModel.selectedProperty
    val dice by viewModel.dice.collectAsState()
    val gameLog by viewModel.gameLog.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left panel - Player info and controls
            PlayerPanel(
                currentPlayer = currentPlayer,
                players = players,
                gameState = gameState,
                onRollDice = viewModel::rollDice,
                onEndTurn = viewModel::endTurn,
                onExitGame = onExitGame,
                modifier = Modifier
                    .width(250.dp)
                    .fillMaxHeight()
                    .padding(16.dp)
            )

            // Center - Game board
            GameBoard(
                properties = properties,
                players = players,
                selectedProperty = selectedProperty,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(16.dp)
            )

            // Right panel - Property info and game log
            InfoPanel(
                selectedProperty = selectedProperty,
                currentPlayer = currentPlayer,
                gameState = gameState,
                gameLog = gameLog,
                onBuyProperty = viewModel::buyProperty,
                modifier = Modifier
                    .width(250.dp)
                    .fillMaxHeight()
                    .padding(16.dp)
            )
        }

        // Overlay for game state messages
        when (gameState) {
            is GameState.Loading -> LoadingOverlay()
            is GameState.Error -> ErrorOverlay((gameState as GameState.Error).message)
            is GameState.GameOver -> GameOverOverlay((gameState as GameState.GameOver).winner)
            else -> {}
        }
    }
}

@Composable
private fun PlayerPanel(
    currentPlayer: Player?,
    players: List<Player>,
    gameState: GameState,
    onRollDice: () -> Unit,
    onEndTurn: () -> Unit,
    onExitGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colors.surface, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Current player info
        currentPlayer?.let { player ->
            Text(
                text = stringResource(R.string.your_turn),
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.primary
            )
            
            Text(
                text = player.name,
                style = MaterialTheme.typography.subtitle1
            )
            
            Text(
                text = stringResource(R.string.cash, player.money),
                style = MaterialTheme.typography.body1
            )
        }

        Divider()

        // Other players
        players.filter { it.id != currentPlayer?.id }.forEach { player ->
            Text(
                text = "${player.name}: $${player.money}",
                style = MaterialTheme.typography.body2
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Game controls
        if (gameState == GameState.PlayerTurn) {
            Button(
                onClick = onRollDice,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.roll_dice))
            }
        }

        if (gameState == GameState.EndingTurn) {
            Button(
                onClick = onEndTurn,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.end_turn))
            }
        }

        OutlinedButton(
            onClick = onExitGame,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.exit))
        }
    }
}

@Composable
private fun GameBoard(
    properties: List<Property>,
    players: List<Player>,
    selectedProperty: Property?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFFCAE7DF))
            .border(2.dp, MaterialTheme.colors.primary)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw the board grid
            val cellSize = size.width / 11
            
            // Draw horizontal lines
            for (i in 0..11) {
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, i * cellSize),
                    end = Offset(size.width, i * cellSize),
                    strokeWidth = 1f
                )
            }
            
            // Draw vertical lines
            for (i in 0..11) {
                drawLine(
                    color = Color.Black,
                    start = Offset(i * cellSize, 0f),
                    end = Offset(i * cellSize, size.height),
                    strokeWidth = 1f
                )
            }
        }

        // Draw properties and players
        // This would be implemented with more detailed property and player rendering
    }
}

@Composable
private fun InfoPanel(
    selectedProperty: Property?,
    currentPlayer: Player?,
    gameState: GameState,
    gameLog: List<Any>,
    onBuyProperty: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colors.surface, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Property info
        selectedProperty?.let { property ->
            Text(
                text = property.name,
                style = MaterialTheme.typography.h6
            )
            
            if (property.price > 0) {
                Text(
                    text = "Price: $${property.price}",
                    style = MaterialTheme.typography.body1
                )
                
                if (gameState == GameState.PropertyAvailable && 
                    currentPlayer?.money ?: 0 >= property.price) {
                    Button(
                        onClick = onBuyProperty,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.buy_property))
                    }
                }
            }
        }

        Divider()

        // Game log
        Text(
            text = "Game Log",
            style = MaterialTheme.typography.subtitle1
        )
        
        // Display last 10 game events
        Column {
            gameLog.takeLast(10).forEach { event ->
                Text(
                    text = event.toString(),
                    style = MaterialTheme.typography.caption
                )
            }
        }
    }
}

@Composable
private fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorOverlay(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color.White,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(32.dp)
        )
    }
}

@Composable
private fun GameOverOverlay(winner: Player) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Game Over!",
                color = Color.White,
                style = MaterialTheme.typography.h4
            )
            Text(
                text = "${winner.name} wins with $${winner.money}!",
                color = Color.White,
                style = MaterialTheme.typography.h6
            )
        }
    }
} 