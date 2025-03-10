package com.monopoly.game.ui.game

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.monopoly.game.data.model.*
import com.monopoly.game.data.repository.GameRepository
import com.monopoly.game.domain.GameEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val gameEngine: GameEngine
) : ViewModel() {

    // Game setup state
    private val _isHost = mutableStateOf(false)
    val isHost: State<Boolean> = _isHost

    private val _gameStarted = mutableStateOf(false)
    val gameStarted: State<Boolean> = _gameStarted

    private val _gameCode = mutableStateOf("")
    val gameCode: State<String> = _gameCode

    // Game state
    private val _gameState = MutableStateFlow<GameState>(GameState.Loading)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _currentPlayer = mutableStateOf<Player?>(null)
    val currentPlayer: State<Player?> = _currentPlayer

    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players.asStateFlow()

    private val _dice = MutableStateFlow(Pair(1, 1))
    val dice: StateFlow<Pair<Int, Int>> = _dice.asStateFlow()

    private val _gameLog = MutableStateFlow<List<GameEvent>>(emptyList())
    val gameLog: StateFlow<List<GameEvent>> = _gameLog.asStateFlow()

    private val _properties = MutableStateFlow<List<Property>>(emptyList())
    val properties: StateFlow<List<Property>> = _properties.asStateFlow()

    private val _selectedProperty = mutableStateOf<Property?>(null)
    val selectedProperty: State<Property?> = _selectedProperty

    fun setIsHost(isHost: Boolean) {
        _isHost.value = isHost
    }

    fun startGame(playerName: String, gameCode: String? = null) {
        viewModelScope.launch {
            _gameState.value = GameState.Loading
            
            try {
                if (isHost.value) {
                    // Host is creating a new game
                    val generatedCode = generateGameCode()
                    _gameCode.value = generatedCode
                    
                    // Initialize the game state
                    gameRepository.createGame(generatedCode, playerName)
                    
                    // Initialize game board
                    val gameBoard = gameEngine.initializeGame()
                    _properties.value = gameBoard.properties
                    
                    // Add the host as the first player
                    val hostPlayer = Player(
                        id = UUID.randomUUID().toString(),
                        name = playerName,
                        position = 0,
                        money = 1500,
                        properties = emptyList(),
                        inJail = false,
                        bankrupt = false,
                        color = PlayerColor.values()[0]
                    )
                    _players.value = listOf(hostPlayer)
                    _currentPlayer.value = hostPlayer
                    
                } else {
                    // Player is joining an existing game
                    gameCode?.let { code ->
                        _gameCode.value = code
                        gameRepository.joinGame(code, playerName)
                        
                        // Get the game state from the repository
                        val gameData = gameRepository.getGameData(code)
                        _properties.value = gameData.properties
                        _players.value = gameData.players + Player(
                            id = UUID.randomUUID().toString(),
                            name = playerName,
                            position = 0,
                            money = 1500,
                            properties = emptyList(),
                            inJail = false,
                            bankrupt = false,
                            color = PlayerColor.values()[_players.value.size % PlayerColor.values().size]
                        )
                    }
                }
                
                // Subscribe to game events
                gameRepository.subscribeToGameEvents(gameCode.value)
                
                _gameStarted.value = true
                _gameState.value = GameState.Waiting
                
            } catch (e: Exception) {
                _gameState.value = GameState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun rollDice() {
        if (gameState.value != GameState.PlayerTurn) return
        
        viewModelScope.launch {
            val diceRoll = gameEngine.rollDice()
            _dice.value = diceRoll
            
            // Move the player
            val currentPosition = currentPlayer.value?.position ?: 0
            val diceSum = diceRoll.first + diceRoll.second
            val newPosition = (currentPosition + diceSum) % 40
            
            currentPlayer.value?.let { player ->
                // Check if passing GO
                if (newPosition < currentPosition) {
                    // Player passed GO
                    val updatedPlayer = player.copy(
                        position = newPosition,
                        money = player.money + 200
                    )
                    _currentPlayer.value = updatedPlayer
                    updatePlayerInList(updatedPlayer)
                    
                    // Add to game log
                    addToGameLog(GameEvent.PassedGo(player.name))
                } else {
                    // Normal movement
                    val updatedPlayer = player.copy(position = newPosition)
                    _currentPlayer.value = updatedPlayer
                    updatePlayerInList(updatedPlayer)
                }
                
                // Handle the space the player landed on
                handleLandedSpace(newPosition)
                
                // Sync the game state
                syncGameState()
            }
        }
    }
    
    private fun handleLandedSpace(position: Int) {
        val property = _properties.value.find { it.position == position }
        
        property?.let {
            _selectedProperty.value = it
            
            if (it.owner == null) {
                // Property is not owned
                _gameState.value = GameState.PropertyAvailable
            } else if (it.owner != currentPlayer.value?.id) {
                // Property is owned by another player
                payRent(it)
            }
        } ?: run {
            // Special spaces (GO, Jail, Tax, etc.)
            when (position) {
                0 -> {} // GO space
                10 -> {} // Just visiting jail
                20 -> {} // Free parking
                30 -> sendToJail()
                4, 38 -> payTax(position)
                2, 17, 33 -> drawCommunityChest()
                7, 22, 36 -> drawChance()
            }
            
            // Move to next player turn if not waiting for user action
            if (gameState.value !is GameState.WaitingForUserAction) {
                _gameState.value = GameState.EndingTurn
            }
        }
    }
    
    private fun payRent(property: Property) {
        viewModelScope.launch {
            currentPlayer.value?.let { player ->
                val owner = _players.value.find { it.id == property.owner }
                
                owner?.let {
                    // Calculate rent
                    val rent = gameEngine.calculateRent(property, _properties.value, _dice.value)
                    
                    // Check if player can pay
                    if (player.money >= rent) {
                        // Pay rent
                        val updatedPlayer = player.copy(money = player.money - rent)
                        val updatedOwner = it.copy(money = it.money + rent)
                        
                        _currentPlayer.value = updatedPlayer
                        updatePlayerInList(updatedPlayer)
                        updatePlayerInList(updatedOwner)
                        
                        // Add to game log
                        addToGameLog(GameEvent.PaidRent(player.name, it.name, rent, property.name))
                    } else {
                        // Player can't pay and is bankrupt
                        handleBankruptcy(player, it)
                    }
                }
                
                // Move to ending turn
                _gameState.value = GameState.EndingTurn
            }
        }
    }
    
    private fun handleBankruptcy(player: Player, creditor: Player) {
        // Transfer all properties to the creditor
        val updatedProperties = _properties.value.map { property ->
            if (property.owner == player.id) {
                property.copy(owner = creditor.id)
            } else {
                property
            }
        }
        
        // Update player statuses
        val bankruptPlayer = player.copy(bankrupt = true, money = 0, properties = emptyList())
        val updatedCreditor = creditor.copy(
            properties = creditor.properties + player.properties
        )
        
        // Update state
        _properties.value = updatedProperties
        updatePlayerInList(bankruptPlayer)
        updatePlayerInList(updatedCreditor)
        
        // Add to game log
        addToGameLog(GameEvent.PlayerBankrupt(player.name, creditor.name))
        
        // Check if game is over
        val activePlayers = _players.value.filter { !it.bankrupt }
        if (activePlayers.size == 1) {
            // Game is over
            _gameState.value = GameState.GameOver(activePlayers.first())
        }
    }
    
    private fun sendToJail() {
        currentPlayer.value?.let {
            val updatedPlayer = it.copy(position = 10, inJail = true)
            _currentPlayer.value = updatedPlayer
            updatePlayerInList(updatedPlayer)
            
            // Add to game log
            addToGameLog(GameEvent.SentToJail(it.name))
        }
    }
    
    private fun payTax(position: Int) {
        currentPlayer.value?.let {
            val taxAmount = if (position == 4) 200 else 100 // Income Tax vs Luxury Tax
            
            val updatedPlayer = if (it.money >= taxAmount) {
                it.copy(money = it.money - taxAmount)
            } else {
                // Player can't pay and is bankrupt
                it.copy(bankrupt = true, money = 0)
            }
            
            _currentPlayer.value = updatedPlayer
            updatePlayerInList(updatedPlayer)
            
            // Add to game log
            val taxType = if (position == 4) "Income Tax" else "Luxury Tax"
            addToGameLog(GameEvent.PaidTax(it.name, taxAmount, taxType))
        }
    }
    
    private fun drawCommunityChest() {
        // Implement community chest card logic
        // For simplicity, just a placeholder for now
        addToGameLog(GameEvent.DrewCard("Community Chest", "Card effect placeholder"))
    }
    
    private fun drawChance() {
        // Implement chance card logic
        // For simplicity, just a placeholder for now
        addToGameLog(GameEvent.DrewCard("Chance", "Card effect placeholder"))
    }
    
    fun buyProperty() {
        viewModelScope.launch {
            val property = _selectedProperty.value
            val player = _currentPlayer.value
            
            if (property != null && player != null && gameState.value == GameState.PropertyAvailable) {
                if (player.money >= property.price) {
                    // Update the player's money and properties
                    val updatedPlayer = player.copy(
                        money = player.money - property.price,
                        properties = player.properties + property.id
                    )
                    
                    // Update the property owner
                    val updatedProperty = property.copy(owner = player.id)
                    val updatedProperties = _properties.value.map {
                        if (it.id == property.id) updatedProperty else it
                    }
                    
                    // Update state
                    _currentPlayer.value = updatedPlayer
                    updatePlayerInList(updatedPlayer)
                    _properties.value = updatedProperties
                    _selectedProperty.value = updatedProperty
                    
                    // Add to game log
                    addToGameLog(GameEvent.BoughtProperty(player.name, property.name, property.price))
                    
                    // Move to ending turn
                    _gameState.value = GameState.EndingTurn
                }
            }
        }
    }
    
    fun endTurn() {
        viewModelScope.launch {
            // Find the next player
            val activePlayers = _players.value.filter { !it.bankrupt }
            val currentPlayerIndex = activePlayers.indexOfFirst { it.id == currentPlayer.value?.id }
            val nextPlayerIndex = (currentPlayerIndex + 1) % activePlayers.size
            
            _currentPlayer.value = activePlayers[nextPlayerIndex]
            _gameState.value = GameState.PlayerTurn
            
            // Sync the game state
            syncGameState()
        }
    }
    
    private fun updatePlayerInList(updatedPlayer: Player) {
        _players.value = _players.value.map {
            if (it.id == updatedPlayer.id) updatedPlayer else it
        }
    }
    
    private fun addToGameLog(event: GameEvent) {
        _gameLog.value = _gameLog.value + event
    }
    
    private fun syncGameState() {
        viewModelScope.launch {
            // In a real implementation, this would sync with Firebase
            gameRepository.updateGameState(
                gameCode.value,
                GameData(
                    players = _players.value,
                    properties = _properties.value,
                    currentPlayerId = _currentPlayer.value?.id ?: "",
                    gameEvents = _gameLog.value
                )
            )
        }
    }
    
    private fun generateGameCode(): String {
        // Generate a random 6-character code
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        return (1..6).map { chars.random() }.joinToString("")
    }
    
    fun cleanupResources() {
        viewModelScope.launch {
            gameRepository.unsubscribeFromGameEvents()
        }
    }
}

sealed class GameState {
    object Loading : GameState()
    object Waiting : GameState()
    object PlayerTurn : GameState()
    object PropertyAvailable : GameState()
    object EndingTurn : GameState()
    sealed class WaitingForUserAction : GameState()
    data class Error(val message: String) : GameState()
    data class GameOver(val winner: Player) : GameState()
} 