# Media Playback

Echo leverages the **Jetpack Media3** library to provide a robust and high-performance audio playback experience.

## Key Components

### ExoPlayer
The core engine for audio rendering. It is configured within the `PlaybackService` to handle local file URIs provided by the MediaStore.

### MediaSession
Integrates the player with the Android system. This enables:
- System-level media controls (Notification, Lock Screen).
- Bluetooth and headset button support.
- Integration with Android Auto and Wear OS.
- Background playback persistence.

### MediaController
The UI interacts with the `MediaSession` through a `MediaController`. In Echo, the `MediaControllerManager` encapsulates the complexity of connecting to the session and managing the controller's lifecycle.

## Playback Features

### Gapless Playback
The architecture is designed to support gapless playback by leveraging ExoPlayer's playlist API. Media items are queued in advance to ensure smooth transitions between tracks.

### Audio Customization (Equalizer)
Echo includes a built-in 5-band equalizer implemented using the Android `AudioFX` API. The equalizer is attached to the player's audio session ID, allowing real-time frequency adjustment.

### Sleep Timer
A managed countdown timer that automatically calls `pause()` on the `MediaController` when the timer expires, allowing users to fall asleep to their music.

### Queue Management
The playback queue is managed reactively. Users can:
- "Play Next": Insert a track immediately after the current one.
- "Add to Queue": Append a track to the end of the current list.
- Reorder: Drag and drop tracks within the upcoming queue to customize the listening order.
