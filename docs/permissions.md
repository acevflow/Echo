# Permissions

Echo requires specific permissions to access and manage local music files on your device. The app follows modern Android security practices by requesting only the necessary permissions and handling denials gracefully.

## Required Permissions

### Storage Access
Depending on the Android version of your device, Echo requests different permissions to access audio files:

- **Android 13+ (API 33+)**: Requests `READ_MEDIA_AUDIO`. This is a granular permission that only grants access to audio files.
- **Android 12 and below**: Requests `READ_EXTERNAL_STORAGE`. This allows the app to discover music files in the device's shared storage.

### Notifications
- **Android 13+ (API 33+)**: Requests `POST_NOTIFICATIONS`. This is required to show the media playback control notification, which is essential for background playback.

### Foreground Services
- Echo uses `FOREGROUND_SERVICE_MEDIA_PLAYBACK` to ensure that music continues playing when the app is in the background or the screen is off.

## Permission Handling

Echo requests permissions at runtime using the **Jetpack Compose Permissions API**. 
1. Upon first launch or when entering the Library, the app checks for storage access.
2. If granted, the `MediaStoreMusicRepository` begins scanning for files.
3. If denied, the app displays a friendly empty state explaining why permission is needed.
4. Users can manually grant permissions in the system settings at any time, and Echo will automatically refresh the library.
