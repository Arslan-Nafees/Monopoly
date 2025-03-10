package com.monopoly.game.ui.game.auction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.monopoly.game.data.model.Player
import com.monopoly.game.data.model.Property
import com.monopoly.game.ui.game.board.components.PropertyDetails
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AuctionDialog(
    property: Property,
    players: List<Player>,
    onAuctionComplete: (winnerId: String, amount: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var currentBid by remember { mutableStateOf(0) }
    var currentBidder by remember { mutableStateOf<Player?>(null) }
    var timeLeft by remember { mutableStateOf(30) }
    var bidInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Timer effect
    LaunchedEffect(timeLeft) {
        if (timeLeft > 0) {
            delay(1000)
            timeLeft--
        } else if (currentBidder != null) {
            onAuctionComplete(currentBidder!!.id, currentBid)
        } else {
            onDismiss()
        }
    }

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
                // Title
                Text(
                    text = "Property Auction",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Property details
                PropertyDetails(property = property)

                Spacer(modifier = Modifier.height(16.dp))

                // Current bid info
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Current Bid: $${currentBid}",
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold
                    )

                    currentBidder?.let {
                        Text(
                            text = "Highest Bidder: ${it.name}",
                            style = MaterialTheme.typography.subtitle1
                        )
                    }

                    Text(
                        text = "Time Left: ${timeLeft}s",
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.secondary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bidding interface
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(players.filter { !it.bankrupt }) { player ->
                        BidderCard(
                            player = player,
                            currentBid = currentBid,
                            onPlaceBid = { bid ->
                                if (bid > currentBid && bid <= player.money) {
                                    currentBid = bid
                                    currentBidder = player
                                    timeLeft = 30 // Reset timer on new bid
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
                        Text("Cancel Auction")
                    }

                    if (currentBidder != null) {
                        Button(
                            onClick = {
                                currentBidder?.let {
                                    onAuctionComplete(it.id, currentBid)
                                }
                            }
                        ) {
                            Text("End Auction")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BidderCard(
    player: Player,
    currentBid: Int,
    onPlaceBid: (Int) -> Unit
) {
    var bidAmount by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.subtitle1
                    )
                    Text(
                        text = "Cash: $${player.money}",
                        style = MaterialTheme.typography.body2
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = bidAmount,
                        onValueChange = { bidAmount = it.filter { c -> c.isDigit() } },
                        label = { Text("Bid Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(120.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            bidAmount.toIntOrNull()?.let { amount ->
                                if (amount > currentBid && amount <= player.money) {
                                    onPlaceBid(amount)
                                    bidAmount = ""
                                }
                            }
                        },
                        enabled = bidAmount.toIntOrNull()?.let { it > currentBid && it <= player.money } ?: false
                    ) {
                        Text("Bid")
                    }
                }
            }
        }
    }
} 