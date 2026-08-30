# Echo

A modern, high-performance local music player for Android. Echo provides a clean and pleasant listening experience for music stored directly on your device, prioritizing speed, privacy, and Material 3 design.

## Overview

Echo is built from the ground up using Jetpack Compose and modern Android architecture. It aims to be a focused alternative to bloated media players, offering robust library management and professional audio customization without unnecessary tracking or cloud dependencies.

## Features (v1.0.0)

### Core Playback
*   **High-Fidelity Audio**: Powered by Jetpack Media3 (ExoPlayer) for reliable background playback.
*   **Media Session Integration**: Full support for system-level controls, Bluetooth peripherals, and Android Auto.
*   **Interactive Controls**: Precision seeking, volume management, and instant skip controls.

### Library & Discovery
*   **Hierarchical Browsing**: Organize your collection by **Songs**, **Albums**, **Artists**, or **Folders**.
*   **Smart Discovery**: Dynamic "Most Played" and "Recently Added" collections that adapt to your habits.
*   **Instant Search**: Real-time filtering across your entire library with immediate playback support.
*   **Search Intelligence**: Persistent **Search History** with interactive suggestions and one-tap filtering.
*   **User Collections**: Create and manage custom **Playlists** and keep track of your **Favorites**.
*   **Listening History**: Automatic tracking of your recently played tracks with manual management.
*   **Metadata Editor**: In-app support for editing song information (Title, Artist, Album) and personalizing **Album Artwork**.

### Advanced Customization
*   **Adaptive UI**: Responsive layouts optimized for **Phones**, **Tablets**, and **Foldables** using Window Size Classes.
*   **Interactive Queue**: Manage upcoming tracks via a dedicated Bottom Sheet with **drag-and-drop reordering**.
*   **Professional Audio**: Built-in **5-band Equalizer** with frequency adjustment and persistent audio profiles.
*   **Automation**: Integrated **Sleep Timer** with customizable intervals to automatically stop playback.
*   **Dynamic UI**: Full support for **Light/Dark/System** modes and **Material You Dynamic Color** (Android 12+).
*   **Home Screen Widget**: Control your music instantly using a modern Jetpack Glance widget.

## Screenshots

<p align="center">
  <img src="docs/screenshots/1.jpg" width="22%" />
  <img src="docs/screenshots/2.jpg" width="22%" />
  <img src="docs/screenshots/3.jpg" width="22%" />
  <img src="docs/screenshots/4.jpg" width="22%" />
</p>

## Requirements

*   **Android Device**: Running Android 7.0 (API level 24) or higher.
*   **Storage Access**: Permission to read audio files from device storage.

## Getting Started

1.  **Clone the repository**: `git clone https://github.com/acevflow/Echo.git`
2.  **Open in Android Studio**: Selection the root folder.
3.  **Build & Run**: Use the `app` configuration.

For more detailed instructions, see the [Build & Installation guide](docs/build.md).

## Documentation

- [**Architecture**](docs/architecture.md) - Learn about the Clean Architecture implementation.
- [**Media Playback**](docs/media-playback.md) - Details on Media3, Equalizer, and Queue management.
- [**Permissions**](docs/permissions.md) - Information on required Android permissions and data access.

## Technical Stack
*   **UI**: Jetpack Compose (Material 3 & Window Size Classes)
*   **Logic**: Kotlin Coroutines & Flow
*   **DI**: Dagger Hilt
*   **Database**: Room (Relational persistence)
*   **Settings**: Jetpack DataStore (Reactive preferences)
*   **Media**: Jetpack Media3 (Audio engine & Session)
*   **Widget**: Jetpack Glance
*   **Image Loading**: Coil

## Roadmap

### Next Release
- [ ] **Advanced Audio Transitions**: Implementation of true gapless playback and cross-fade support.
- [ ] **Wear OS Support**: Companion app for music control from your wrist.

### Future
- [ ] **Enhanced Widgets**: Multiple widget sizes and customizable shortcut actions.
- [ ] **Cloud Sync**: Optional, privacy-focused synchronization of playlists and favorites.

## Contributing

Contributions are welcome! Please review our [CONTRIBUTING.md](CONTRIBUTING.md) to get started.

## Security

We take user privacy and security seriously. Please report vulnerabilities as described in [SECURITY.md](SECURITY.md).

## License

Echo is released under the [MIT License](LICENSE).
