package com.monopoly.game.ui.game.property

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.monopoly.game.data.model.Player
import com.monopoly.game.data.model.Property
import com.monopoly.game.data.model.PropertyGroup
import com.monopoly.game.data.model.PropertyType
import com.monopoly.game.ui.game.board.components.PropertyDetails

@Composable
fun PropertyManagementDialog(
    player: Player,
    properties: List<Property>,
    onBuildHouse: (Property) -> Unit,
    onBuildHotel: (Property) -> Unit,
    onMortgage: (Property) -> Unit,
    onUnmortgage: (Property) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Text(
                    text = "Property Management",
                    style = MaterialTheme.typography.h6
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Group properties by color
                val groupedProperties = properties
                    .filter { it.owner == player.id }
                    .groupBy { it.group }

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    groupedProperties.forEach { (group, props) ->
                        item {
                            PropertyGroup(
                                group = group,
                                properties = props,
                                allProperties = properties,
                                player = player,
                                onBuildHouse = onBuildHouse,
                                onBuildHotel = onBuildHotel,
                                onMortgage = onMortgage,
                                onUnmortgage = onUnmortgage
                            )
                        }
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
private fun PropertyGroup(
    group: PropertyGroup,
    properties: List<Property>,
    allProperties: List<Property>,
    player: Player,
    onBuildHouse: (Property) -> Unit,
    onBuildHotel: (Property) -> Unit,
    onMortgage: (Property) -> Unit,
    onUnmortgage: (Property) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Group header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "Hide" else "Show")
                }
            }

            // Property details when expanded
            if (expanded) {
                properties.forEach { property ->
                    PropertyManagementCard(
                        property = property,
                        hasMonopoly = hasMonopoly(property, allProperties),
                        canBuildHouse = canBuildHouse(property, properties),
                        canBuildHotel = canBuildHotel(property, properties),
                        playerMoney = player.money,
                        onBuildHouse = { onBuildHouse(property) },
                        onBuildHotel = { onBuildHotel(property) },
                        onMortgage = { onMortgage(property) },
                        onUnmortgage = { onUnmortgage(property) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PropertyManagementCard(
    property: Property,
    hasMonopoly: Boolean,
    canBuildHouse: Boolean,
    canBuildHotel: Boolean,
    playerMoney: Int,
    onBuildHouse: () -> Unit,
    onBuildHotel: () -> Unit,
    onMortgage: () -> Unit,
    onUnmortgage: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            PropertyDetails(property = property)

            Spacer(modifier = Modifier.height(8.dp))

            // Building controls
            if (property.type == PropertyType.PROPERTY && hasMonopoly) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (property.houses < 4) {
                        Button(
                            onClick = onBuildHouse,
                            enabled = canBuildHouse && playerMoney >= 50
                        ) {
                            Text("Build House ($50)")
                        }
                    }

                    if (property.houses == 4) {
                        Button(
                            onClick = onBuildHotel,
                            enabled = canBuildHotel && playerMoney >= 50
                        ) {
                            Text("Build Hotel ($50)")
                        }
                    }
                }
            }

            // Mortgage controls
            if (property.price > 0) {
                Button(
                    onClick = if (property.isMortgaged) onUnmortgage else onMortgage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    enabled = property.houses == 0
                ) {
                    if (property.isMortgaged) {
                        Text("Unmortgage (${property.price / 2})")
                    } else {
                        Text("Mortgage (${property.price / 2})")
                    }
                }
            }
        }
    }
}

private fun hasMonopoly(property: Property, allProperties: List<Property>): Boolean {
    return allProperties
        .filter { it.group == property.group }
        .all { it.owner == property.owner }
}

private fun canBuildHouse(property: Property, groupProperties: List<Property>): Boolean {
    if (property.houses >= 4) return false
    
    // Ensure even building
    val minHouses = groupProperties.minOf { it.houses }
    return property.houses == minHouses
}

private fun canBuildHotel(property: Property, groupProperties: List<Property>): Boolean {
    if (property.houses != 4) return false
    
    // All properties in the group must have 4 houses
    return groupProperties.all { it.houses == 4 }
} 