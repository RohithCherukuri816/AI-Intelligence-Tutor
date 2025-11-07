# EduAI Tutor - Complete Setup Guide

## 🚀 Quick Start

This guide will help you set up and run the EduAI Tutor Android app from scratch.

## Prerequisites

### Required Software
1. **Android Studio** (Hedgehog 2023.1.1 or later)
   - Download from: https://developer.android.com/studio
   
2. **JDK 17** or later
   - Usually bundled with Android Studio
   
3. **Android SDK**
   - API Level 24 (Android 7.0) minimum
   - API Level 34 (Android 14) target

### Required Accounts
1. **Firebender Enterprise Account**
   - Sign up at: https://firebender.ai/enterprise
   - Get your API key from the dashboard

## Step-by-Step Setup

### 1. Project Setup

```bash
# Clone or create the project
cd /path/to/your/projects
# If cloning:
git clone <repository-url>
cd eduai-tutor

# Or if starting fresh, copy all the files to your project directory
```

### 2. Configure Android Studio

1. Open Android Studio
2. Click "Open" and select the project folder
3. Wait for Gradle sync to complete
4. If prompted, accept SDK licenses:
   ```bash
   yes | sdkmanager --licenses
   ```

### 3. Configure Firebender SDK

#### Option A: Using local.properties (Recommended)
1. Create/edit `local.properties` in project root:
   ```properties
   sdk.dir=/path/to/Android/sdk
   firebender.api.key=YOUR_FIREBENDER_API_KEY_HERE
   ```

2. Update `FirebenderService.kt` to read from BuildConfig:
   ```kotlin
   private const val API_KEY = BuildConfig.FIREBENDER_API_KEY
   ```

#### Option B: Direct Configuration (For Testing)
1. Open `app/src/main/java/com/example/eduaituitor/ai/FirebenderService.kt`
2. Replace the API_KEY constant:
   ```kotlin
   private const val API_KEY = "your_actual_api_key_here"
   ```

### 4. Add Firebender SDK Dependency

1. Open `app/build.gradle.kts`
2. Add the Firebender SDK dependency:
   ```kotlin
   dependencies {
       // ... other dependencies
       
       // Firebender SDK (replace x.x.x with actual version)
       implementation("com.firebender:android-sdk:1.0.0")
       
       // Or if using RunAnywhere SDK:
       implementation("com.runanywhere:sdk:1.0.0")
   }
   ```

3. Sync Gradle files

### 5. Update AndroidManifest.xml

Add required permissions:
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- Internet permission for AI API calls -->
    <uses-permission android:name="android.permission.INTERNET" />
    
    <!-- Optional: For voice tutoring -->
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    
    <application
        android:name=".EduAIApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.EduAITutor">
        
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.EduAITutor">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

### 6. Create Application Class (Optional but Recommended)

Create `app/src/main/java/com/example/eduaituitor/EduAIApplication.kt`:
```kotlin
package com.example.eduaituitor

import android.app.Application
import com.example.eduaituitor.ai.FirebenderService

class EduAIApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebender SDK
        FirebenderService().initialize()
    }
}
```

### 7. Build and Run

1. **Connect a device or start an emulator**
   - Physical device: Enable USB debugging in Developer Options
   - Emulator: Create one in AVD Manager (API 24+)

2. **Build the project**
   ```bash
   ./gradlew assembleDebug
   ```

3. **Run the app**
   - Click the green "Run" button in Android Studio
   - Or use command line:
     ```bash
     ./gradlew installDebug
     ```

## Testing the App

### 1. Welcome Screen
- App should open to a welcome screen
- Tap "Start Learning" to proceed

### 2. Chat Functionality
Try these test questions:
- "Teach me about photosynthesis"
- "Explain Newton's Laws"
- "What is quantum physics?"

### 3. Quiz Feature
- After asking a question, tap "Take a Quiz"
- Answer the multiple-choice questions
- View your score and feedback

### 4. Progress Tracking
- Navigate to Progress tab
- View your learning statistics
- Check topics studied and scores

## Troubleshooting

### Common Issues

#### 1. Gradle Sync Failed
```bash
# Clean and rebuild
./gradlew clean
./gradlew build
```

#### 2. SDK Not Found
- Open SDK Manager in Android Studio
- Install Android SDK Platform 34
- Install Android SDK Build-Tools 34.0.0

#### 3. Firebender SDK Not Found
- Check if dependency is correctly added
- Verify API key is configured
- Check internet connection

#### 4. App Crashes on Launch
- Check Logcat for error messages
- Verify all required permissions are granted
- Ensure minimum SDK version is met

#### 5. AI Responses Not Working
- Verify Firebender API key is correct
- Check internet connection
- Review FirebenderService.kt implementation
- Check API quota/limits

### Debug Mode

Enable detailed logging:
```kotlin
// In FirebenderService.kt
private const val DEBUG_MODE = true

fun log(message: String) {
    if (DEBUG_MODE) {
        Log.d("EduAI", message)
    }
}
```

## Development Tips

### 1. Hot Reload
- Use Compose Preview for UI development
- Enable Live Edit in Android Studio

### 2. Testing Without AI
- Use simulated responses in `FirebenderService.kt`
- Comment out actual API calls during development

### 3. Database Inspection
```bash
# View Room database
adb shell
run-as com.example.eduaituitor
cd databases
sqlite3 eduai_database
.tables
SELECT * FROM quiz_sessions;
```

### 4. Performance Monitoring
- Use Android Profiler in Android Studio
- Monitor memory, CPU, and network usage

## Production Deployment

### 1. Update Build Configuration
```kotlin
// In app/build.gradle.kts
android {
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

### 2. Generate Signed APK
1. Build > Generate Signed Bundle/APK
2. Create or select keystore
3. Choose release build variant
4. Sign and build

### 3. Test Release Build
```bash
./gradlew assembleRelease
adb install app/build/outputs/apk/release/app-release.apk
```

## Next Steps

1. **Customize AI Prompts**
   - Edit `ai/prompts/ExplanationPrompt.kt`
   - Edit `ai/prompts/QuizPrompt.kt`

2. **Add More Features**
   - Implement voice input
   - Add more quiz types
   - Create study schedules

3. **Improve UI**
   - Customize theme colors
   - Add animations
   - Create custom components

4. **Optimize Performance**
   - Implement caching
   - Add offline mode
   - Optimize database queries

## Resources

- **Android Documentation**: https://developer.android.com
- **Jetpack Compose**: https://developer.android.com/jetpack/compose
- **Kotlin Coroutines**: https://kotlinlang.org/docs/coroutines-overview.html
- **Room Database**: https://developer.android.com/training/data-storage/room
- **Material Design 3**: https://m3.material.io

## Support

Need help? Check:
- Project README.md
- Code comments
- Android Studio documentation
- Stack Overflow

Happy coding! 🚀
