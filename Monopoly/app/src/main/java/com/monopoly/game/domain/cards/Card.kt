package com.monopoly.game.domain.cards

import com.monopoly.game.data.model.Player
import com.monopoly.game.data.model.Property

sealed class Card(
    val title: String,
    val description: String
) {
    abstract fun execute(
        player: Player,
        allPlayers: List<Player>,
        properties: List<Property>
    ): CardEffect
}

sealed class ChanceCard(title: String, description: String) : Card(title, description)
sealed class CommunityChestCard(title: String, description: String) : Card(title, description)

// Chance Cards
class AdvanceToGo : ChanceCard(
    "Advance to GO",
    "Collect $200"
) {
    override fun execute(player: Player, allPlayers: List<Player>, properties: List<Property>) =
        CardEffect.MovePlayer(0, collectGo = true)
}

class AdvanceToProperty(
    private val propertyName: String,
    private val position: Int
) : ChanceCard(
    "Advance to $propertyName",
    "If you pass GO, collect $200"
) {
    override fun execute(player: Player, allPlayers: List<Player>, properties: List<Property>) =
        CardEffect.MovePlayer(position, collectGo = position < player.position)
}

class AdvanceToNearestRailroad : ChanceCard(
    "Advance to nearest Railroad",
    "Pay owner twice the rental. If unowned, you may buy it"
) {
    override fun execute(player: Player, allPlayers: List<Player>, properties: List<Property>): CardEffect {
        val railroadPositions = listOf(5, 15, 25, 35)
        val nextRailroad = railroadPositions.first { it > player.position } % 40
        return CardEffect.MovePlayer(nextRailroad, doubleRent = true)
    }
}

class BankPays(private val amount: Int) : ChanceCard(
    "Bank pays you",
    "Collect $$amount"
) {
    override fun execute(player: Player, allPlayers: List<Player>, properties: List<Property>) =
        CardEffect.CollectMoney(amount)
}

class PayEachPlayer(private val amount: Int) : ChanceCard(
    "Pay each player",
    "Pay $$amount to each player"
) {
    override fun execute(player: Player, allPlayers: List<Player>, properties: List<Property>) =
        CardEffect.PayEachPlayer(amount)
}

// Community Chest Cards
class CollectMoney(private val amount: Int, description: String) : CommunityChestCard(
    "Bank pays you",
    description
) {
    override fun execute(player: Player, allPlayers: List<Player>, properties: List<Property>) =
        CardEffect.CollectMoney(amount)
}

class PayMoney(private val amount: Int, description: String) : CommunityChestCard(
    "Pay money",
    description
) {
    override fun execute(player: Player, allPlayers: List<Player>, properties: List<Property>) =
        CardEffect.PayMoney(amount)
}

class GetOutOfJailCard : CommunityChestCard(
    "Get Out of Jail Free",
    "This card may be kept until needed or sold"
) {
    override fun execute(player: Player, allPlayers: List<Player>, properties: List<Property>) =
        CardEffect.GetOutOfJail
}

// Card Effects
sealed class CardEffect {
    data class MovePlayer(
        val position: Int,
        val collectGo: Boolean = false,
        val doubleRent: Boolean = false
    ) : CardEffect()
    
    data class CollectMoney(val amount: Int) : CardEffect()
    data class PayMoney(val amount: Int) : CardEffect()
    data class PayEachPlayer(val amount: Int) : CardEffect()
    object GetOutOfJail : CardEffect()
}

// Card Decks
object CardDecks {
    val chanceCards = listOf(
        AdvanceToGo(),
        AdvanceToProperty("Boardwalk", 39),
        AdvanceToProperty("Illinois Avenue", 24),
        AdvanceToProperty("St. Charles Place", 11),
        AdvanceToNearestRailroad(),
        BankPays(50),
        PayEachPlayer(50)
        // Add more chance cards
    )

    val communityChestCards = listOf(
        CollectMoney(200, "Bank error in your favor. Collect $200"),
        CollectMoney(50, "From sale of stock you get $50"),
        PayMoney(50, "Doctor's fee. Pay $50"),
        PayMoney(100, "Hospital fees. Pay $100"),
        GetOutOfJailCard()
        // Add more community chest cards
    )
} 