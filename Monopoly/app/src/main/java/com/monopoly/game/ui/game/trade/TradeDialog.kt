package com.monopoly.game.ui.game.trade

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.monopoly.game.data.model.Player
import com.monopoly.game.data.model.Property
import com.monopoly.game.ui.game.board.components.PropertyCard

@Composable
fun TradeDialog(
    currentPlayer: Player,
    otherPlayers: List<Player>,
    properties: List<Property>,
    onTrade: (TradeOffer) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedPlayer by remember { mutableStateOf<Player?>(null) }
    var selectedProperties by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedOtherProperties by remember { mutableStateOf<List<String>>(emptyList()) }
    var moneyOffered by remember { mutableStateOf("0") }
    var moneyRequested by remember { mutableStateOf("0") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                // Title
                Text(
                    text = "Trade Properties",
                    style = MaterialTheme.typography.h6
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Player selection
                if (selectedPlayer == null) {
                    PlayerSelection(
                        players = otherPlayers,
                        onPlayerSelected = { selectedPlayer = it }
                    )
                } else {
                    // Trade interface
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        // Current player's offer
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Your Offer",
                                style = MaterialTheme.typography.subtitle1
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Money offer
                            OutlinedTextField(
                                value = moneyOffered,
                                onValueChange = { moneyOffered = it.filter { c -> c.isDigit() } },
                                label = { Text("Money Offered") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Property selection
                            PropertyList(
                                title = "Your Properties",
                                properties = properties.filter { it.owner == currentPlayer.id },
                                selectedProperties = selectedProperties,
                                onPropertySelected = { propertyId ->
                                    selectedProperties = if (propertyId in selectedProperties) {
                                        selectedProperties - propertyId
                                    } else {
                                        selectedProperties + propertyId
                                    }
                                }
                            )
                        }

                        VerticalDivider()

                        // Other player's offer
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Request from ${selectedPlayer?.name}",
                                style = MaterialTheme.typography.subtitle1
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Money request
                            OutlinedTextField(
                                value = moneyRequested,
                                onValueChange = { moneyRequested = it.filter { c -> c.isDigit() } },
                                label = { Text("Money Requested") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Property selection
                            PropertyList(
                                title = "Their Properties",
                                properties = properties.filter { it.owner == selectedPlayer?.id },
                                selectedProperties = selectedOtherProperties,
                                onPropertySelected = { propertyId ->
                                    selectedOtherProperties = if (propertyId in selectedOtherProperties) {
                                        selectedOtherProperties - propertyId
                                    } else {
                                        selectedOtherProperties + propertyId
                                    }
                                }
                            )
                        }
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                selectedPlayer?.let { player ->
                                    onTrade(
                                        TradeOffer(
                                            initiatorId = currentPlayer.id,
                                            receiverId = player.id,
                                            propertiesOffered = selectedProperties,
                                            propertiesRequested = selectedOtherProperties,
                                            moneyOffered = moneyOffered.toIntOrNull() ?: 0,
                                            moneyRequested = moneyRequested.toIntOrNull() ?: 0
                                        )
                                    )
                                }
                            },
                            enabled = selectedPlayer != null &&
                                    (selectedProperties.isNotEmpty() || moneyOffered.toIntOrNull() ?: 0 > 0) &&
                                    (selectedOtherProperties.isNotEmpty() || moneyRequested.toIntOrNull() ?: 0 > 0)
                        ) {
                            Text("Propose Trade")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerSelection(
    players: List<Player>,
    onPlayerSelected: (Player) -> Unit
) {
    LazyColumn {
        items(players) { player ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onPlayerSelected(player) }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(player.name)
                    Text("$${player.money}")
                }
            }
        }
    }
}

@Composable
private fun PropertyList(
    title: String,
    properties: List<Property>,
    selectedProperties: List<String>,
    onPropertySelected: (String) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle2
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.12f))
        ) {
            items(properties) { property ->
                PropertyCard(
                    property = property,
                    isSelected = property.id in selectedProperties,
                    onClick = { onPropertySelected(property.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(4.dp)
                )
            }
        }
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.12f))
    )
}

data class TradeOffer(
    val initiatorId: String,
    val receiverId: String,
    val propertiesOffered: List<String>,
    val propertiesRequested: List<String>,
    val moneyOffered: Int,
    val moneyRequested: Int
) 