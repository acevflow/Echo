# Echo

A modern Android music player for playing and managing local music files. Echo is designed to be clean, fast, and pleasant, providing a focused experience for music stored directly on your device.

## Overview

Echo aims to provide a high-quality local music playback experience with a modern user interface built using Jetpack Compose. It focuses on simplicity and performance, avoiding unnecessary bloat and prioritizing user privacy.

## Features

The following features are implemented:

*   **Local Music Discovery**: Automatically scans audio files on the device using the MediaStore API.
*   **Audio Playback**: Core playback engine integrated using Jetpack Media3 (ExoPlayer).
*   **Background Playback**: Supported via a Foreground Service, allowing music to continue playing when the app is minimized.
*   **Media Session Integration**: Enables system-level playback control and Bluetooth/peripheral support.
*   **In-App Controls**: A persistent **MiniPlayer** bar provides instant Play/Pause controls and song information across the app.
*   **Navigation System**: Foundation established using Navigation Compose for future multi-screen expansion.
*   **Modern UI**: A clean interface following Material 3 guidelines, displaying a reactive list of songs.
*   **Permission Handling**: Automatically requests storage and notification permissions based on the Android version.
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
    *   `data/`: Repository implementations and data sources (MediaStore).
    *   `domain/`: Core data models and repository interfaces.
    *   `di/`: Dependency injection configuration (Hilt).
    *   `media/`: Media3 service implementation and session management.
    *   `ui/`: Composable screens, ViewModels, and navigation logic.
        *   `navigation/`: App routing and NavGraph.
        *   `components/`: Shared UI elements like the MiniPlayer.
*   `gradle/`: Version Catalog (`libs.versions.toml`) and wrapper configuration.

### Built With

*   [Kotlin](https://kotlinlang.org/) - Programming language.
*   [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern UI toolkit.
*   [Material 3](https://m3.material.io/) - Design system.
*   [Jetpack Media3](https://developer.android.com/guide/topics/media/media3) - Media playback and session APIs.
*   [Navigation Compose](https://developer.android.com/jetpack/compose/navigation) - Declarative navigation.
*   [Dagger Hilt](https://dagger.dev/hilt/) - Dependency injection.
*   [KSP](https://github.com/google/ksp) - Kotlin Symbol Processing.
*   [Gradle](https://gradle.org/) - Build system.

## Roadmap

*   [x] Local music discovery
*   [ ] Music library browsing (Albums/Artists)
*   [x] Basic playback engine
*   [x] In-app playback controls (MiniPlayer)
*   [x] Background playback support
*   [x] Media session integration
*   [ ] Navigation to full player screen
*   [ ] Queue management
*   [ ] Shuffle and repeat modes
*   [ ] Search functionality
*   [ ] Favorites and Playlists
*   [ ] Album artwork retrieval
*   [ ] Customizable themes

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for details on how to get involved.

## Security

For information on reporting security vulnerabilities, please refer to [SECURITY.md](SECURITY.md).

## License

Echo is released under the [MIT License](LICENSE).
