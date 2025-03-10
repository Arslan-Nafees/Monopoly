package com.monopoly.game.domain

import com.monopoly.game.data.model.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class GameEngine @Inject constructor() {

    fun initializeGame(): GameBoard {
        return GameBoard(
            properties = createProperties()
        )
    }

    fun rollDice(): Pair<Int, Int> {
        return Pair(
            Random.nextInt(1, 7),
            Random.nextInt(1, 7)
        )
    }

    fun calculateRent(
        property: Property,
        allProperties: List<Property>,
        diceRoll: Pair<Int, Int>? = null
    ): Int {
        return when (property.type) {
            PropertyType.PROPERTY -> calculatePropertyRent(property, allProperties)
            PropertyType.RAILROAD -> calculateRailroadRent(property.owner!!, allProperties)
            PropertyType.UTILITY -> calculateUtilityRent(property.owner!!, allProperties, diceRoll!!)
            else -> 0
        }
    }

    private fun calculatePropertyRent(property: Property, allProperties: List<Property>): Int {
        if (property.isMortgaged) return 0

        // Get base rent based on number of houses
        val baseRent = property.rent[property.houses]

        // Check if player owns all properties in the group (monopoly)
        val hasMonopoly = allProperties
            .filter { it.group == property.group }
            .all { it.owner == property.owner }

        // Double rent if player has monopoly and no houses
        return if (hasMonopoly && property.houses == 0) {
            baseRent * 2
        } else {
            baseRent
        }
    }

    private fun calculateRailroadRent(ownerId: String, allProperties: List<Property>): Int {
        // Count how many railroads the owner has
        val railroadCount = allProperties
            .filter { it.type == PropertyType.RAILROAD && it.owner == ownerId }
            .count()

        // Railroad rent is 25 * 2^(n-1) where n is the number of railroads owned
        return 25 * (1 shl (railroadCount - 1))
    }

    private fun calculateUtilityRent(ownerId: String, allProperties: List<Property>, diceRoll: Pair<Int, Int>): Int {
        // Count how many utilities the owner has
        val utilityCount = allProperties
            .filter { it.type == PropertyType.UTILITY && it.owner == ownerId }
            .count()

        val diceSum = diceRoll.first + diceRoll.second
        
        // If owner has both utilities, rent is 10x dice roll
        // If owner has one utility, rent is 4x dice roll
        return diceSum * if (utilityCount == 2) 10 else 4
    }

    private fun createProperties(): List<Property> {
        return listOf(
            // Brown properties
            createProperty("Mediterranean Avenue", 1, PropertyGroup.BROWN, 60, listOf(2, 10, 30, 90, 160, 250)),
            createProperty("Baltic Avenue", 3, PropertyGroup.BROWN, 60, listOf(4, 20, 60, 180, 320, 450)),

            // Light Blue properties
            createProperty("Oriental Avenue", 6, PropertyGroup.LIGHT_BLUE, 100, listOf(6, 30, 90, 270, 400, 550)),
            createProperty("Vermont Avenue", 8, PropertyGroup.LIGHT_BLUE, 100, listOf(6, 30, 90, 270, 400, 550)),
            createProperty("Connecticut Avenue", 9, PropertyGroup.LIGHT_BLUE, 120, listOf(8, 40, 100, 300, 450, 600)),

            // Pink properties
            createProperty("St. Charles Place", 11, PropertyGroup.PINK, 140, listOf(10, 50, 150, 450, 625, 750)),
            createProperty("States Avenue", 13, PropertyGroup.PINK, 140, listOf(10, 50, 150, 450, 625, 750)),
            createProperty("Virginia Avenue", 14, PropertyGroup.PINK, 160, listOf(12, 60, 180, 500, 700, 900)),

            // Orange properties
            createProperty("St. James Place", 16, PropertyGroup.ORANGE, 180, listOf(14, 70, 200, 550, 750, 950)),
            createProperty("Tennessee Avenue", 18, PropertyGroup.ORANGE, 180, listOf(14, 70, 200, 550, 750, 950)),
            createProperty("New York Avenue", 19, PropertyGroup.ORANGE, 200, listOf(16, 80, 220, 600, 800, 1000)),

            // Red properties
            createProperty("Kentucky Avenue", 21, PropertyGroup.RED, 220, listOf(18, 90, 250, 700, 875, 1050)),
            createProperty("Indiana Avenue", 23, PropertyGroup.RED, 220, listOf(18, 90, 250, 700, 875, 1050)),
            createProperty("Illinois Avenue", 24, PropertyGroup.RED, 240, listOf(20, 100, 300, 750, 925, 1100)),

            // Yellow properties
            createProperty("Atlantic Avenue", 26, PropertyGroup.YELLOW, 260, listOf(22, 110, 330, 800, 975, 1150)),
            createProperty("Ventnor Avenue", 27, PropertyGroup.YELLOW, 260, listOf(22, 110, 330, 800, 975, 1150)),
            createProperty("Marvin Gardens", 29, PropertyGroup.YELLOW, 280, listOf(24, 120, 360, 850, 1025, 1200)),

            // Green properties
            createProperty("Pacific Avenue", 31, PropertyGroup.GREEN, 300, listOf(26, 130, 390, 900, 1100, 1275)),
            createProperty("North Carolina Avenue", 32, PropertyGroup.GREEN, 300, listOf(26, 130, 390, 900, 1100, 1275)),
            createProperty("Pennsylvania Avenue", 34, PropertyGroup.GREEN, 320, listOf(28, 150, 450, 1000, 1200, 1400)),

            // Dark Blue properties
            createProperty("Park Place", 37, PropertyGroup.DARK_BLUE, 350, listOf(35, 175, 500, 1100, 1300, 1500)),
            createProperty("Boardwalk", 39, PropertyGroup.DARK_BLUE, 400, listOf(50, 200, 600, 1400, 1700, 2000)),

            // Railroads
            createRailroad("Reading Railroad", 5),
            createRailroad("Pennsylvania Railroad", 15),
            createRailroad("B. & O. Railroad", 25),
            createRailroad("Short Line", 35),

            // Utilities
            createUtility("Electric Company", 12),
            createUtility("Water Works", 28),

            // Special spaces
            createSpecialSpace("GO", 0),
            createSpecialSpace("Community Chest", 2),
            createSpecialSpace("Income Tax", 4),
            createSpecialSpace("Chance", 7),
            createSpecialSpace("Jail", 10),
            createSpecialSpace("Community Chest", 17),
            createSpecialSpace("Free Parking", 20),
            createSpecialSpace("Chance", 22),
            createSpecialSpace("Go to Jail", 30),
            createSpecialSpace("Community Chest", 33),
            createSpecialSpace("Chance", 36),
            createSpecialSpace("Luxury Tax", 38)
        )
    }

    private fun createProperty(
        name: String,
        position: Int,
        group: PropertyGroup,
        price: Int,
        rent: List<Int>
    ): Property {
        return Property(
            id = UUID.randomUUID().toString(),
            name = name,
            position = position,
            type = PropertyType.PROPERTY,
            price = price,
            rent = rent,
            group = group,
            owner = null,
            houses = 0,
            isMortgaged = false
        )
    }

    private fun createRailroad(name: String, position: Int): Property {
        return Property(
            id = UUID.randomUUID().toString(),
            name = name,
            position = position,
            type = PropertyType.RAILROAD,
            price = 200,
            rent = listOf(25, 50, 100, 200),
            group = PropertyGroup.RAILROAD,
            owner = null,
            houses = 0,
            isMortgaged = false
        )
    }

    private fun createUtility(name: String, position: Int): Property {
        return Property(
            id = UUID.randomUUID().toString(),
            name = name,
            position = position,
            type = PropertyType.UTILITY,
            price = 150,
            rent = listOf(4, 10),
            group = PropertyGroup.UTILITY,
            owner = null,
            houses = 0,
            isMortgaged = false
        )
    }

    private fun createSpecialSpace(name: String, position: Int): Property {
        return Property(
            id = UUID.randomUUID().toString(),
            name = name,
            position = position,
            type = PropertyType.SPECIAL,
            price = 0,
            rent = emptyList(),
            group = PropertyGroup.SPECIAL,
            owner = null,
            houses = 0,
            isMortgaged = false
        )
    }
} 