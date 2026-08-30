# Echo

A modern Android music player for playing and managing local music files. Echo is designed to be clean, fast, and pleasant, providing a focused experience for music stored directly on your device.

## Overview

Echo aims to provide a high-quality local music playback experience with a modern user interface built using Jetpack Compose. It focuses on simplicity and performance, avoiding unnecessary bloat and prioritizing user privacy.

## Features

The following features are implemented:

*   **Personalization**: Customizable app appearance with **Light**, **Dark**, and **System** theme modes, plus **Dynamic Color** support (Android 12+).
*   **Interactive Queue**: View and manage upcoming tracks via a dedicated **Queue Bottom Sheet**; support for "Play Next", "Add to Queue", and **Drag-and-Drop Reordering**.
*   **Smart Collections**: Automated "Most Played" and "Recently Added" lists generated from your listening habits and device storage.
*   **Listening History**: Automatically tracks recently played songs, allowing users to browse and clear their activity history.
*   **Custom Playlists**: Create, manage, and play personalized song collections with full relational persistence.
*   **Real-time Search**: Instant, library-wide search for songs, albums, and artists with immediate playback support.
*   **Playback Intelligence**: Support for **Shuffle** and **Repeat** modes (Off, All, One) with real-time UI synchronization.
*   **Settings Persistence**: Integrated Jetpack DataStore to persist user preferences like playback modes across app restarts.
*   **Library Hierarchy**: Organized browsing by **Songs**, **Albums**, and **Artists** with a dedicated navigation bar.
*   **Detail Views**: Explore tracks within a specific album or view all albums from an artist.
*   **Local Persistence**: Integrated Room database to persist user data and preferences.
*   **Favorites Support**: Allows users to mark songs as favorites, with status synced across the entire UI.
*   **Local Music Discovery**: Automatically scans audio files on the device using the MediaStore API.
*   **Audio Playback**: Core playback engine integrated using Jetpack Media3 (ExoPlayer).
*   **Full-screen Player**: A dedicated screen for detailed playback control and metadata display.
*   **Background Playback**: Supported via a Foreground Service, allowing music to continue playing when the app is minimized.
*   **Media Session Integration**: Enables system-level playback control and Bluetooth/peripheral support.
*   **Queue Management**: Automatically manages a playback queue based on the current library view.
*   **Skip Controls**: Fully functional "Next" and "Previous" controls in both the full player and system media UI.
*   **Continuous Playback**: Automatically advances to the next song in the queue when a track ends.
*   **Interactive Seeking**: Real-time progress tracking and scrubbing through tracks using an interactive slider.
*   **Album Artwork**: High-quality artwork retrieval and display using Coil for both the player and library views.
*   **In-App Controls**: A persistent **MiniPlayer** bar provides instant Play/Pause controls across the app.
*   **Navigation System**: Robust multi-screen navigation using Navigation Compose.
*   **Modern UI**: A clean interface following Material 3 guidelines.
*   **Clean Architecture**: Solid foundation with separate data, domain, media, and UI layers.

## Screenshots

*Screenshots will be added as the application UI matures.*

## Requirements

*   **Android Device**: Running Android 7.0 (API level 24) or higher.
*   **Storage Access**: Permission to read audio files from the device's storage.

## Getting Started

### Prerequisites

*   Android Studio Ladybug (or newer)
*   Android SDK 37

### Building from Source

1.  Clone the repository:
    ```bash
    git clone https://github.com/acevflow/Echo.git
    ```
2.  Open the project in Android Studio.
3.  Sync the project with Gradle files.
4.  Run the application on an emulator or physical device.

## Development

### Project Structure

The project follows a standard Android structure within the `:app` module:

*   `app/src/main/java/com/acevflow/echo/`:
    *   `data/`:
        *   `local/`: Room database, DAOs, and entities (Favorites, Playlists, History).
        *   `preferences/`: DataStore implementation for app settings.
        *   `repository/`: Repository implementations (MediaStore, Room, and DataStore).
    *   `domain/`: Core data models and repository interfaces.
        *   `util/`: General utilities like time formatting.
    *   `di/`: Dependency injection configuration (Hilt).
    *   `media/`: Media3 service implementation and session management.
    *   `ui/`: Composable screens, ViewModels, and navigation logic.
        *   `navigation/`: App routing and NavGraph.
        *   `library/`: Categorized views for Songs, Albums, Artists, and Recently Played.
        *   `playlists/`: Custom collection management.
        *   `search/`: Real-time filtering and discovery UI.
        *   *   `player/`: Full-screen playback interface.
        *   `queue/`: Interactive playback queue management (Bottom Sheet).
        *   `details/`: Detail screens for Albums, Artists, and Playlists.
        *   `settings/`: App configuration and personalization UI.
        *   `components/`: Shared UI elements like the MiniPlayer.
*   `gradle/`: Version Catalog (`libs.versions.toml`) and wrapper configuration.

### Built With

*   [Kotlin](https://kotlinlang.org/) - Programming language.
*   [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern UI toolkit.
*   [Material 3](https://m3.material.io/) - Design system.
*   [Jetpack Media3](https://developer.android.com/guide/topics/media/media3) - Media playback and session APIs.
*   [Room Database](https://developer.android.com/training/data-storage/room) - Local data persistence.
*   [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) - Lightweight settings persistence.
*   [Coil](https://coil-kt.github.io/coil/) - Asynchronous image loading.
*   [Navigation Compose](https://developer.android.com/jetpack/compose/navigation) - Declarative navigation.
*   [Dagger Hilt](https://dagger.dev/hilt/) - Dependency injection.
*   [Reorderable Compose](https://github.com/Calvin-Sh/Reorderable) - Drag-and-drop list interactions.
*   [KSP](https://github.com/google/ksp) - Kotlin Symbol Processing.
*   [Gradle](https://gradle.org/) - Build system.

## Roadmap

*   [x] Local music discovery
*   [x] Music library browsing (Albums/Artists)
*   [x] Basic playback engine
*   [x] In-app playback controls (MiniPlayer)
*   [x] Full-screen player UI
*   [x] Background playback support
*   [x] Media session integration
*   [x] Playback progress and seeking
*   [x] Album artwork retrieval
*   [x] Queue management & skip controls
*   [x] Favorites support
*   [x] Shuffle and repeat modes
*   [x] Search functionality
*   [x] Custom playlists creation & management
*   [x] Recently played history
*   [x] Smart collections (Most Played, Recently Added)
*   [x] Advanced queue management (Play Next, Add to Queue, Reordering)
*   [x] Customizable themes (Light/Dark/Dynamic)
*   [ ] Audio quality settings

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for details on how to get involved.

## Security

For information on reporting security vulnerabilities, please refer to [SECURITY.md](SECURITY.md).

## License

Echo is released under the [MIT License](LICENSE).
