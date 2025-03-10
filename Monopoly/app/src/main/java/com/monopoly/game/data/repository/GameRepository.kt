package com.monopoly.game.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.monopoly.game.data.model.GameData
import com.monopoly.game.data.model.GameEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) {
    private var gameEventListener: ValueEventListener? = null

    suspend fun createGame(gameCode: String, hostName: String) {
        val gameRef = firebaseDatabase.getReference("games/$gameCode")
        gameRef.setValue(mapOf(
            "status" to "waiting",
            "host" to hostName,
            "createdAt" to System.currentTimeMillis()
        )).await()
    }

    suspend fun joinGame(gameCode: String, playerName: String) {
        val gameRef = firebaseDatabase.getReference("games/$gameCode")
        val playersRef = gameRef.child("players")
        
        // Add player to the game
        playersRef.push().setValue(mapOf(
            "name" to playerName,
            "joinedAt" to System.currentTimeMillis()
        )).await()
    }

    suspend fun getGameData(gameCode: String): GameData {
        val gameRef = firebaseDatabase.getReference("games/$gameCode")
        val snapshot = gameRef.get().await()
        return snapshot.toGameData()
    }

    fun subscribeToGameEvents(gameCode: String): Flow<GameEvent> = callbackFlow {
        val gameRef = firebaseDatabase.getReference("games/$gameCode/events")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.lastOrNull()?.let { eventSnapshot ->
                    val event = eventSnapshot.toGameEvent()
                    trySend(event)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        gameEventListener = listener
        gameRef.addValueEventListener(listener)

        awaitClose {
            gameRef.removeEventListener(listener)
            gameEventListener = null
        }
    }

    suspend fun updateGameState(gameCode: String, gameData: GameData) {
        val gameRef = firebaseDatabase.getReference("games/$gameCode")
        gameRef.setValue(gameData.toMap()).await()
    }

    suspend fun unsubscribeFromGameEvents() {
        gameEventListener?.let { listener ->
            firebaseDatabase.reference.removeEventListener(listener)
            gameEventListener = null
        }
    }

    private fun DataSnapshot.toGameData(): GameData {
        // Implementation of converting Firebase snapshot to GameData
        // This would need to be implemented based on your Firebase data structure
        TODO("Implement conversion from Firebase snapshot to GameData")
    }

    private fun DataSnapshot.toGameEvent(): GameEvent {
        // Implementation of converting Firebase snapshot to GameEvent
        // This would need to be implemented based on your Firebase data structure
        TODO("Implement conversion from Firebase snapshot to GameEvent")
    }

    private fun GameData.toMap(): Map<String, Any> {
        return mapOf(
            "players" to players.map { player ->
                mapOf(
                    "id" to player.id,
                    "name" to player.name,
                    "position" to player.position,
                    "money" to player.money,
                    "properties" to player.properties,
                    "inJail" to player.inJail,
                    "bankrupt" to player.bankrupt,
                    "color" to player.color.name
                )
            },
            "properties" to properties.map { property ->
                mapOf(
                    "id" to property.id,
                    "name" to property.name,
                    "position" to property.position,
                    "type" to property.type.name,
                    "price" to property.price,
                    "rent" to property.rent,
                    "group" to property.group.name,
                    "owner" to property.owner,
                    "houses" to property.houses,
                    "isMortgaged" to property.isMortgaged
                )
            },
            "currentPlayerId" to currentPlayerId,
            "events" to gameEvents.map { event ->
                when (event) {
                    is GameEvent.PlayerJoined -> mapOf(
                        "type" to "PLAYER_JOINED",
                        "playerName" to event.playerName
                    )
                    // Add other event types as needed
                    else -> mapOf("type" to "UNKNOWN")
                }
            }
        )
    }
} 