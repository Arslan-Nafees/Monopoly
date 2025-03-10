package com.monopoly.game.data.model

import java.io.Serializable

/**
 * Represents a player in the Monopoly game
 */
data class Player(
    val id: String,
    val name: String,
    val position: Int,
    val money: Int,
    val properties: List<String>,
    val inJail: Boolean,
    val bankrupt: Boolean,
    val color: PlayerColor
) : Serializable

/**
 * Available colors for player pieces
 */
enum class PlayerColor {
    RED, BLUE, GREEN, YELLOW, PURPLE, ORANGE, PINK, TEAL
}

/**
 * Represents a property on the Monopoly board
 */
data class Property(
    val id: String,
    val name: String,
    val position: Int,
    val type: PropertyType,
    val price: Int,
    val rent: List<Int>,
    val group: PropertyGroup,
    val owner: String?,
    val houses: Int,
    val isMortgaged: Boolean
) : Serializable

/**
 * Property types in Monopoly
 */
enum class PropertyType {
    PROPERTY, RAILROAD, UTILITY, TAX, SPECIAL
}

/**
 * Property color groups
 */
enum class PropertyGroup {
    BROWN, LIGHT_BLUE, PINK, ORANGE, RED, YELLOW, GREEN, DARK_BLUE,
    RAILROAD, UTILITY, TAX, SPECIAL
}

/**
 * Game events that can occur during gameplay
 */
sealed class GameEvent : Serializable {
    data class PlayerJoined(val playerName: String) : GameEvent()
    data class PlayerLeft(val playerName: String) : GameEvent()
    data class RolledDice(val playerName: String, val dice: Pair<Int, Int>) : GameEvent()
    data class PassedGo(val playerName: String) : GameEvent()
    data class BoughtProperty(val playerName: String, val propertyName: String, val price: Int) : GameEvent()
    data class PaidRent(val playerName: String, val ownerName: String, val amount: Int, val propertyName: String) : GameEvent()
    data class BuiltHouse(val playerName: String, val propertyName: String) : GameEvent()
    data class BuiltHotel(val playerName: String, val propertyName: String) : GameEvent()
    data class MortgagedProperty(val playerName: String, val propertyName: String, val amount: Int) : GameEvent()
    data class UnmortgagedProperty(val playerName: String, val propertyName: String, val amount: Int) : GameEvent()
    data class SentToJail(val playerName: String) : GameEvent()
    data class GetOutOfJail(val playerName: String, val method: String) : GameEvent()
    data class PaidTax(val playerName: String, val amount: Int, val taxType: String) : GameEvent()
    data class DrewCard(val cardType: String, val cardEffect: String) : GameEvent()
    data class Trade(
        val initiatorName: String,
        val receiverName: String,
        val propertiesGiven: List<String>,
        val propertiesReceived: List<String>,
        val moneyGiven: Int,
        val moneyReceived: Int
    ) : GameEvent()
    data class PlayerBankrupt(val playerName: String, val creditorName: String) : GameEvent()
    data class GameEnded(val winnerName: String) : GameEvent()
}

/**
 * Overall game state to be synchronized between players
 */
data class GameData(
    val players: List<Player>,
    val properties: List<Property>,
    val currentPlayerId: String,
    val gameEvents: List<GameEvent>
) : Serializable

/**
 * Represents the complete game board
 */
data class GameBoard(
    val properties: List<Property>
) : Serializable 