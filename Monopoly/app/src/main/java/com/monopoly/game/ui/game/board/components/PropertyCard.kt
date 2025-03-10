package com.monopoly.game.ui.game.board.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.monopoly.game.data.model.Property
import com.monopoly.game.data.model.PropertyGroup
import com.monopoly.game.data.model.PropertyType

@Composable
fun PropertyCard(
    property: Property,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colors.primary else Color.Black
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Property color strip
            if (property.type == PropertyType.PROPERTY) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .background(getPropertyColor(property.group))
                )
            }

            // Property name
            Text(
                text = property.name,
                style = MaterialTheme.typography.caption,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(4.dp)
            )

            // Price
            if (property.price > 0) {
                Text(
                    text = "$${property.price}",
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Houses/Hotels
            if (property.houses > 0) {
                val buildingText = if (property.houses == 5) "🏨" else "🏠".repeat(property.houses)
                Text(
                    text = buildingText,
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Center
                )
            }

            // Owner indicator
            property.owner?.let {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.Red) // Replace with actual player color
                )
            }
        }
    }
}

@Composable
fun getPropertyColor(group: PropertyGroup): Color {
    return when (group) {
        PropertyGroup.BROWN -> Color(0xFF955436)
        PropertyGroup.LIGHT_BLUE -> Color(0xFFAAE0FA)
        PropertyGroup.PINK -> Color(0xFFD93A96)
        PropertyGroup.ORANGE -> Color(0xFFF7941D)
        PropertyGroup.RED -> Color(0xFFED1B24)
        PropertyGroup.YELLOW -> Color(0xFFFEF200)
        PropertyGroup.GREEN -> Color(0xFF1FB25A)
        PropertyGroup.DARK_BLUE -> Color(0xFF0072BB)
        else -> Color.Transparent
    }
}

@Composable
fun PropertyDetails(
    property: Property,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Title with color strip for properties
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (property.type == PropertyType.PROPERTY) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(getPropertyColor(property.group))
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Text(
                text = property.name,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )
        }

        // Price
        if (property.price > 0) {
            Text(
                text = "Price: $${property.price}",
                style = MaterialTheme.typography.body1
            )
        }

        // Rent information
        if (property.type == PropertyType.PROPERTY) {
            Text(
                text = "RENT",
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.Bold
            )
            
            Text("Base Rent: $${property.rent[0]}")
            Text("With Color Set: $${property.rent[0] * 2}")
            Text("With 1 House: $${property.rent[1]}")
            Text("With 2 Houses: $${property.rent[2]}")
            Text("With 3 Houses: $${property.rent[3]}")
            Text("With 4 Houses: $${property.rent[4]}")
            Text("With Hotel: $${property.rent[5]}")
        } else if (property.type == PropertyType.RAILROAD) {
            Text(
                text = "RENT",
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.Bold
            )
            
            Text("1 Railroad: $25")
            Text("2 Railroads: $50")
            Text("3 Railroads: $100")
            Text("4 Railroads: $200")
        } else if (property.type == PropertyType.UTILITY) {
            Text(
                text = "RENT",
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.Bold
            )
            
            Text("One utility: 4 times dice roll")
            Text("Both utilities: 10 times dice roll")
        }

        // Mortgage value
        if (property.price > 0) {
            Text(
                text = "Mortgage Value: $${property.price / 2}",
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.secondary
            )
        }
    }
} 