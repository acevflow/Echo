# Echo

A modern, high-performance local music player for Android. Echo provides a clean and pleasant listening experience for music stored directly on your device, prioritizing speed, privacy, and Material 3 design.

## Overview

Echo is built from the ground up using Jetpack Compose and modern Android architecture. It aims to be a focused alternative to bloated media players, offering robust library management and professional audio customization without unnecessary tracking or cloud dependencies.

## Features

### Core Playback
*   **High-Fidelity Audio**: Powered by Jetpack Media3 (ExoPlayer) for reliable background playback.
*   **Media Session Integration**: Full support for system-level controls, Bluetooth peripherals, and Android Auto.
*   **Gapless-Ready Architecture**: Designed for continuous, uninterrupted listening.
*   **Interactive Controls**: Precision seeking, volume management, and instant skip controls.

### Library & Discovery
*   **Hierarchical Browsing**: Organize your collection by **Songs**, **Albums**, **Artists**, or **Folders**.
*   **Smart Discovery**: Dynamic "Most Played" and "Recently Added" collections that adapt to your habits.
*   **Instant Search**: Real-time filtering across your entire library with immediate playback support.
*   **User Collections**: Create and manage custom **Playlists** and keep track of your **Favorites**.
*   **Listening History**: Automatic tracking of your recently played tracks with manual management.

### Advanced Customization
*   **Interactive Queue**: Manage upcoming tracks via a dedicated Bottom Sheet with **drag-and-drop reordering**.
*   **Professional Audio**: Built-in **5-band Equalizer** with frequency adjustment and persistent audio profiles.
*   **Automation**: Integrated **Sleep Timer** with customizable intervals to automatically stop playback.
*   **Dynamic UI**: Full support for **Light/Dark/System** modes and **Material You Dynamic Color** (Android 12+).
*   **Home Screen Widget**: Control your music instantly using a modern Jetpack Glance widget.

## Screenshots

*Visual documentation will be added as the product design evolves.*

## Requirements

*   **Android Device**: Running Android 7.0 (API level 24) or higher.
*   **Storage Access**: Permission to read audio files from device storage.

## Getting Started

### Installation

1.  Clone the repository:
    ```bash
    git clone https://github.com/acevflow/Echo.git
    ```
2.  Open the project in Android Studio (Ladybug or newer).
3.  Sync Gradle and run the `:app` module on your device.

## Development

Echo is developed with a strict adherence to **Clean Architecture** principles, ensuring the codebase remains modular, testable, and maintainable.

### Technical Stack
*   **UI**: Jetpack Compose (Material 3)
*   **Logic**: Kotlin Coroutines & Flow
*   **DI**: Dagger Hilt
*   **Database**: Room (Relational persistence)
*   **Settings**: Jetpack DataStore (Reactive preferences)
*   **Media**: Jetpack Media3 (Audio engine & Session)
*   **Widget**: Jetpack Glance
*   **Image Loading**: Coil

## Contributing

Contributions are welcome! Please review our [CONTRIBUTING.md](CONTRIBUTING.md) to get started.

## Security

We take user privacy and security seriously. Please report vulnerabilities as described in [SECURITY.md](SECURITY.md).

## License

Echo is released under the [MIT License](LICENSE).
