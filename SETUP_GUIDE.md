# EduAI Tutor - Developer Setup Guide

This guide will help you set up and run the EduAI Tutor Android application on your development
machine.

## 📋 Prerequisites

### Required Software

1. **Android Studio** (Arctic Fox or newer)
    - Download from: https://developer.android.com/studio
    - Recommended: Latest stable version

2. **Java Development Kit (JDK) 8+**
    - Included with Android Studio
    - Or download from: https://www.oracle.com/java/technologies/downloads/

3. **Git** (for cloning the repository)
    - Download from: https://git-scm.com/

### Hardware Requirements

- **RAM**: Minimum 8 GB (16 GB recommended)
- **Storage**: At least 4 GB free space
- **Internet**: Required for Gradle sync and model download

### Android Device/Emulator

- **Minimum Android Version**: 7.0 (API 24)
- **Target Android Version**: 14.0 (API 34)
- **Recommended**: Physical device for best AI performance

---

## 🚀 Step-by-Step Setup

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd eduaituitor
```

### 2. Open Project in Android Studio

1. Launch **Android Studio**
2. Select **"Open an Existing Project"**
3. Navigate to the `eduaituitor` folder
4. Click **"OK"**

### 3. Gradle Sync

Android Studio will automatically start syncing Gradle. If not:

1. Click **File → Sync Project with Gradle Files**
2. Wait for the sync to complete (may take 5-10 minutes first time)

**If you encounter Gradle errors:**

```bash
# Clean and rebuild
./gradlew clean
./gradlew build
```

### 4. Verify Dependencies

Check that these key dependencies are resolved in `app/build.gradle.kts`:

- ✅ RunAnywhere SDK AAR files (in `app/libs/`)
- ✅ Jetpack Compose BOM
- ✅ Room Database
- ✅ Ktor Client
- ✅ Kotlin Coroutines

### 5. Configure Build Variants

1. Go to **Build → Select Build Variant**
2. Choose **"debug"** for development
3. For release builds, use **"release"**

### 6. Set Up Emulator (Optional)

If you don't have a physical device:

1. Click **Tools → Device Manager**
2. Click **"Create Device"**
3. Select a device (e.g., Pixel 6)
4. Choose system image: **API 34 (Android 14)** or higher
5. Click **"Finish"**

**Recommended Emulator Settings:**

- RAM: 2048 MB or higher
- Internal Storage: 2048 MB or higher
- Enable: Use Host GPU

### 7. Run the Application

#### Option A: Using Android Studio

1. Connect your Android device via USB (enable USB debugging) OR start emulator
2. Click the **Run** button (▶️) or press `Shift + F10`
3. Select your device from the list
4. Wait for app to build and install

#### Option B: Using Command Line

```bash
# Debug build
./gradlew installDebug

# Run on connected device
adb shell am start -n com.example.eduaituitor/.MainActivity
```

---

## 🔧 Configuration

### Changing AI Model

Edit `app/src/main/java/com/example/eduaituitor/EduAIApplication.kt`:

```kotlin
private suspend fun registerModels() {
    try {
        addModelFromURL(
            url = "YOUR_CUSTOM_MODEL_URL.gguf",
            name = "Your Model Name",
            type = "LLM"
        )
        Log.i("EduAI", "AI model registered successfully")
    } catch (e: Exception) {
        Log.e("EduAI", "Model registration failed: ${e.message}", e)
    }
}
```

### Adjusting Fallback Behavior

Edit `app/src/main/java/com/example/eduaituitor/repository/AIRepository.kt`:

```kotlin
class AIRepository(private val application: Application) {
    private val firebenderService = FirebenderService()
    private var useRealAI = true // Set to false to use fallback responses
    
    // ... rest of code
}
```

### Database Configuration

The app uses Room database with default settings. To customize:

Edit `app/src/main/java/com/example/eduaituitor/viewmodel/MainViewModel.kt`:

```kotlin
private val database = Room.databaseBuilder(
    application,
    AppDatabase::class.java,
    "eduai_database" // Change database name here
)
.fallbackToDestructiveMigration() // Add this if you want to clear DB on schema changes
.build()
```

---

## 🐛 Common Issues & Solutions

### Issue 1: Gradle Sync Failed

**Error:**

```
Could not resolve dependencies
```

**Solution:**

1. Check internet connection
2. Update Gradle wrapper:
   ```bash
   ./gradlew wrapper --gradle-version=8.0
   ```
3. Invalidate caches: **File → Invalidate Caches / Restart**

---

### Issue 2: AAR Files Not Found

**Error:**

```
Could not find RunAnywhereKotlinSDK-release.aar
```

**Solution:**

1. Verify files exist in `app/libs/`:
    - `RunAnywhereKotlinSDK-release.aar`
    - `runanywhere-llm-llamacpp-release.aar`
2. If missing, download from RunAnywhere SDK releases
3. Place in `app/libs/` folder

---

### Issue 3: App Crashes on Launch

**Error:**

```
java.lang.RuntimeException: Unable to start activity
```

**Solution:**

1. Check logcat for detailed error:
   ```bash
   adb logcat | grep -E "EduAI|AndroidRuntime"
   ```
2. Common causes:
    - Missing permissions in AndroidManifest.xml
    - Database migration issues
    - SDK initialization failure

**Quick Fix:**

```bash
# Clear app data
adb shell pm clear com.example.eduaituitor

# Reinstall
./gradlew clean installDebug
```

---

### Issue 4: Model Download Fails

**Error:**

```
SDK initialization failed: Unable to download model
```

**Solution:**

1. Check internet connection
2. Ensure device has 500+ MB free storage
3. Try downloading model manually:
   ```bash
   # Check available storage
   adb shell df
   ```
4. Check Firebase/RunAnywhere API keys are valid

---

### Issue 5: Compose Preview Not Working

**Error:**

```
Failed to instantiate compose preview
```

**Solution:**

1. Update Android Studio to latest version
2. Rebuild project: **Build → Clean Project → Rebuild Project**
3. Enable Compose in Gradle:
   ```kotlin
   composeOptions {
       kotlinCompilerExtensionVersion = "1.5.3"
   }
   ```

---

## 📱 Testing the App

### Manual Testing Checklist

#### Welcome Screen

- [ ] App launches without crashes
- [ ] Welcome screen displays correctly
- [ ] "Start Learning" button is clickable

#### Chat Screen

- [ ] Chat input field is functional
- [ ] Send button works
- [ ] Messages appear in chat
- [ ] AI responds within 3-5 seconds
- [ ] Chat bubbles are properly styled

#### Quiz Screen

- [ ] Quiz generates after clicking button
- [ ] Questions display correctly
- [ ] Radio buttons work
- [ ] Navigation (Previous/Next) works
- [ ] Submit button validates all answers
- [ ] Score displays correctly

#### Progress Screen

- [ ] Progress list displays
- [ ] Statistics calculate correctly
- [ ] Progress bars show accurate values
- [ ] Reset button clears data

#### Settings Screen

- [ ] Dark mode toggle works
- [ ] TTS toggle saves state
- [ ] Clear history prompts confirmation
- [ ] Settings persist after app restart

### Automated Testing

Run unit tests:

```bash
./gradlew test
```

Run instrumented tests:

```bash
./gradlew connectedAndroidTest
```

---

## 🔍 Debugging Tips

### Enable Verbose Logging

Add to `AndroidManifest.xml`:

```xml
<application android:debuggable="true">
```

### View Logs in Real-Time

```bash
# All logs
adb logcat

# Filter by app
adb logcat | grep EduAI

# Filter by priority (Error)
adb logcat *:E

# Save to file
adb logcat > app_logs.txt
```

### Inspect Database

```bash
# Pull database from device
adb pull /data/data/com.example.eduaituitor/databases/eduai_database .

# Open with SQLite browser
sqlite3 eduai_database
.tables
SELECT * FROM learning_progress;
```

### Monitor Network Calls

Use Android Studio's Network Profiler:

1. **View → Tool Windows → Profiler**
2. Select your device and app
3. Click **Network** tab

---

## 🏗️ Build Variants

### Debug Build

```bash
./gradlew assembleDebug
```

- Includes debugging symbols
- Logs enabled
- Not optimized

### Release Build

```bash
./gradlew assembleRelease
```

- Optimized with ProGuard/R8
- Logs disabled
- Signed with release key

**Note:** Configure signing in `app/build.gradle.kts`:

```kotlin
signingConfigs {
    create("release") {
        storeFile = file("keystore.jks")
        storePassword = "your-password"
        keyAlias = "your-alias"
        keyPassword = "your-password"
    }
}
```

---

## 📦 Generating APK/AAB

### APK (for direct installation)

```bash
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

### AAB (for Google Play)

```bash
./gradlew bundleRelease
# Output: app/build/outputs/bundle/release/app-release.aab
```

---

## 🌐 Environment Variables

Optional: Set environment variables for API keys

**Linux/Mac:**

```bash
export RUNANYWHERE_API_KEY="your-api-key"
export FIREBENDER_API_KEY="your-api-key"
```

**Windows:**

```cmd
set RUNANYWHERE_API_KEY=your-api-key
set FIREBENDER_API_KEY=your-api-key
```

**In Code:**

```kotlin
val apiKey = System.getenv("RUNANYWHERE_API_KEY") ?: "dev"
```

---

## 🎯 Next Steps

Once setup is complete:

1. ✅ Explore the codebase
2. ✅ Read the main README.md
3. ✅ Review architecture documentation
4. ✅ Try adding a new feature
5. ✅ Write tests for your code
6. ✅ Submit a pull request

---

## 📚 Additional Resources

- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Jetpack Compose Guide](https://developer.android.com/jetpack/compose)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [RunAnywhere SDK Docs](https://docs.runanywhere.com)
- [Material Design 3](https://m3.material.io/)

---

## 💬 Getting Help

If you encounter issues not covered here:

1. Check existing GitHub issues
2. Search Stack Overflow
3. Join our Discord/Slack community
4. Open a new GitHub issue with:
    - Error message
    - Steps to reproduce
    - Environment details (OS, Android Studio version)
    - Relevant logs

---

## ✅ Setup Verification

Run this checklist to verify your setup:

```bash
# Check Java version
java -version  # Should be 8+

# Check Gradle
./gradlew --version

# Check Android SDK
$ANDROID_HOME/tools/bin/sdkmanager --list

# Check connected devices
adb devices

# Build project
./gradlew build

# Run tests
./gradlew test
```

If all commands complete without errors, you're ready to develop! 🎉

---

**Happy Coding!** 🚀

For questions or support, reach out to the team or open an issue on GitHub.