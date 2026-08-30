# Architecture

Echo follows **Clean Architecture** principles to ensure a modular, testable, and maintainable codebase. The project is divided into several layers, each with specific responsibilities.

## Layers

### 1. Data Layer (`data/`)
The data layer is responsible for retrieving and persisting data from various sources.
- **`local/`**: Contains the Room database implementation, including DAOs and entities for Favorites, Playlists, Playback History, and **Search History**.
- **`preferences/`**: Implements Jetpack DataStore for reactive storage of user settings (e.g., Theme, Shuffle/Repeat modes, Equalizer bands).
- **`repository/`**: Contains the implementations of the domain repositories.
    - `MediaStoreMusicRepository`: Discovers local music files using the Android MediaStore API and synchronizes them with the local database.

### 2. Domain Layer (`domain/`)
The domain layer contains the core business logic and data models. It is independent of any other layer.
- **`model/`**: Pure Kotlin data classes representing Songs, Albums, Artists, Folders, and Playlists.
- **`repository/`**: Interfaces that define how the data should be accessed.

### 3. Media Layer (`media/`)
The media layer handles everything related to audio playback and synchronization with the Android system.
- **`PlaybackService`**: A `MediaSessionService` that manages the `ExoPlayer` instance and handles background playback.
- **`MediaControllerManager`**: A singleton that coordinates communication between the UI and the playback service using a `MediaController`.

### 4. UI Layer (`ui/`)
The UI layer is built entirely with **Jetpack Compose** and follows the MVVM pattern.
- **`navigation/`**: Manages app routing using Navigation Compose.
- **`theme/`**: Defines the Material 3 design system, including colors, typography, and spacing.
- **Adaptive UI**: Dynamically branches between **Navigation Rail** (Tablets/Large screens) and **Bottom Bar** (Phones) layouts based on `WindowSizeClass`.
- **`components/`**: Reusable UI elements like the MiniPlayer and custom list items.
- **Screens**: Each feature (Library, Player, Search, Settings) has its own package containing the Composable screen and its associated ViewModel.

## Data Flow

1. The **UI** observes state from **ViewModels**.
2. **ViewModels** interact with **Repositories** (via interfaces) to request data or perform actions.
3. **Repositories** fetch data from **MediaStore** or the **Room database** and return it as reactive `Flow` streams.
4. For playback, **ViewModels** use the **MediaControllerManager** to send commands to the **PlaybackService**.
5. The **PlaybackService** updates the **MediaSession**, which is then observed by the UI to reflect the current playback state.
