# Echo

A modern Android music player for playing and managing local music files. Echo is designed to be clean, fast, and pleasant, providing a focused experience for music stored directly on your device.

## Overview

Echo aims to provide a high-quality local music playback experience with a modern user interface built using Jetpack Compose. It focuses on simplicity and performance, avoiding unnecessary bloat and prioritizing user privacy.

## Features

The following features are planned or currently being implemented:

*   **Local Music Discovery**: Automatically scan and index audio files on the device.
*   **Library Management**: Browse by songs, albums, artists, and genres.
*   **Modern UI**: A clean interface following Material 3 guidelines.
*   **Playback Controls**: Play, pause, skip, and seek functionality.
*   **Queue Management**: Easily manage what plays next.
*   **Dark & Light Themes**: Full support for system-wide theme preferences.

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
    git clone https://github.com/ChristianAceves/echo.git
    ```
2.  Open the project in Android Studio.
3.  Sync the project with Gradle files.
4.  Run the application on an emulator or physical device.

## Development

### Project Structure

The project follows a standard Android multi-module structure (currently a single `:app` module):

*   `app/`: The main application module containing the UI and business logic.
    *   `src/main/java/com/acevflow/echo/`: Kotlin source files.
    *   `src/main/res/`: Android resources.
*   `gradle/`: Gradle configuration files and Version Catalog (`libs.versions.toml`).

### Built With

*   [Kotlin](https://kotlinlang.org/) - Programming language.
*   [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern UI toolkit.
*   [Material 3](https://m3.material.io/) - Design system.
*   [Gradle](https://gradle.org/) - Build system.

## Roadmap

*   [ ] Local music discovery
*   [ ] Music library browsing
*   [ ] Basic playback engine
*   [ ] Playback controls and notification
*   [ ] Background playback support
*   [ ] Media session integration
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
