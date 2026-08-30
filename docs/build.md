# Build & Installation

Echo is a standard Android project that can be built using Android Studio or the command line via the Gradle wrapper.

## Prerequisites

- **Android Studio Ladybug** (2024.2.1) or newer.
- **Android SDK 37** (installed via SDK Manager).
- **JDK 17** or newer.

## Building with Android Studio

1. **Clone the repository**:
   ```bash
   git clone https://github.com/acevflow/Echo.git
   ```
2. **Open the project**: Select the root `Echo` folder.
3. **Sync Gradle**: Click "Sync Project with Gradle Files" and wait for completion.
4. **Select configuration**: Choose the `app` module and a target device/emulator.
5. **Run**: Click the "Run" button or press `Shift+F10`.

## Building via Command Line

You can build the APK directly using the provided Gradle wrapper:

### Debug Build
```bash
./gradlew assembleDebug
```
The output APK will be located at `app/build/outputs/apk/debug/app-debug.apk`.

### Release Build
To build a signed release APK, you must configure your signing properties in `local.properties` or your environment variables.
```bash
./gradlew assembleRelease
```

## Static Analysis

Run the following command to check for code quality and potential issues:
```bash
./gradlew lint
```
