# Monopoly Game

A fully-featured digital implementation of the classic Monopoly board game built with Kotlin and Jetpack Compose for Android.

## Features

### Core Game Mechanics
- Complete implementation of official Monopoly rules
- Real-time multiplayer support via Firebase
- Turn-based gameplay with dice rolling
- Property ownership and rent collection
- Money management system
- Bankruptcy handling

### Property System
- Visual property cards with color coding
- Detailed property information display
  - Purchase prices
  - Rent values for different development levels
  - Current ownership status
- House and hotel building system
- Mortgage management
- Monopoly set detection

### Trading System
- Player-to-player trading interface
- Property and money exchange
- Trade offer creation and negotiation
- Real-time trade updates

### Auction System
- Timed property auctions (30-second countdown)
- Multi-player bidding
- Real-time bid updates
- Automatic winner determination

### Chance and Community Chest
- Complete set of cards with various effects
- Movement cards
- Money collection/payment cards
- Get Out of Jail cards
- Special action cards

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox (2021.3.1) or newer
- JDK 11 or newer
- Android SDK 21 or higher
- Firebase account (for multiplayer features)

### Installation Steps

1. Clone the repository:
```bash
git clone https://github.com/yourusername/monopoly.git
cd monopoly
```

2. Open the project in Android Studio:
- Launch Android Studio
- Select "Open an Existing Project"
- Navigate to the cloned repository and click "OK"

3. Firebase Setup:
- Create a new Firebase project at [Firebase Console](https://console.firebase.google.com)
- Add an Android app to your Firebase project
- Download the `google-services.json` file
- Place the file in the `app/` directory

4. Build and Run:
- Connect an Android device or start an emulator
- Click the "Run" button in Android Studio
- Select your target device and click "OK"

## Playing the Game

1. **Starting a Game**:
- Launch the app
- Choose "New Game" or "Join Game"
- For new games, select the number of players and configure game settings
- For joining, enter the game code provided by the host

2. **Gameplay**:
- Players take turns rolling dice and moving around the board
- Land on properties to buy them or pay rent
- Build houses and hotels on owned properties
- Trade with other players
- Draw Chance and Community Chest cards
- Manage your money and avoid bankruptcy

3. **Winning**:
- The game continues until all but one player are bankrupt
- The last player with assets is declared the winner

## Technical Details

### Architecture
- MVVM architecture pattern
- Repository pattern for data management
- Clean Architecture principles

### Technologies Used
- Kotlin
- Jetpack Compose for UI
- Firebase Realtime Database
- Firebase Authentication
- Kotlin Coroutines
- Hilt for dependency injection

### Key Components
- `GameActivity`: Main game lifecycle management
- `GameViewModel`: Game state and player interactions
- `PropertyCard`: Property UI and interactions
- `TradeDialog`: Trading interface
- `AuctionDialog`: Auction system
- `Card`: Chance and Community Chest implementation

## Contributing

We welcome contributions! Please feel free to submit pull requests.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Based on the classic Monopoly board game by Hasbro
- Built with modern Android development tools and practices
- Special thanks to the open-source community

## Support

For support, please open an issue in the GitHub repository or contact the development team. 