# Java Version Issue Fix

## Current Error

```
Dependency requires at least JVM runtime version 11. This build uses a Java 8 JVM.
```

## Root Cause

Android Gradle Plugin 8.13.0 requires Java 11 or higher, but your system is using Java 8.

---

## Solution: Update Java Version

### Method 1: Configure in Android Studio (Recommended)

1. **Open Android Studio**

2. **Go to Settings:**
   - File → Settings (Windows/Linux)
   - Android Studio → Preferences (Mac)

3. **Navigate to Gradle JDK:**
   - Build, Execution, Deployment → Build Tools → Gradle
   - Find "Gradle JDK" dropdown

4. **Select Java 17 or 11:**
   - Choose "jbr-17" (JetBrains Runtime 17) - Recommended
   - Or "Embedded JDK" if available
   - Or download JDK 17 if not available

5. **Apply and OK**

6. **Sync Gradle:**
   - File → Sync Project with Gradle Files

### Method 2: Set JAVA_HOME Environment Variable

#### Windows:

1. **Download JDK 17:**
   - https://adoptium.net/temurin/releases/
   - Or use Android Studio's embedded JDK

2. **Set JAVA_HOME:**
   ```cmd
   # Find Android Studio's JDK (usually here):
   C:\Program Files\Android\Android Studio\jbr
   
   # Set environment variable:
   setx JAVA_HOME "C:\Program Files\Android\Android Studio\jbr"
   ```

3. **Restart terminal and try again:**
   ```bash
   ./gradlew clean
   ```

#### Mac/Linux:

```bash
# Find Java installations
/usr/libexec/java_home -V

# Set JAVA_HOME (add to ~/.bashrc or ~/.zshrc)
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Or use Android Studio's JDK
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"

# Reload shell
source ~/.bashrc  # or source ~/.zshrc
```

### Method 3: Use gradle.properties

Create or edit `gradle.properties` in project root:

```properties
# Use Android Studio's embedded JDK
org.gradle.java.home=C:\\Program Files\\Android\\Android Studio\\jbr

# Or specify your JDK path
# org.gradle.java.home=C:\\Program Files\\Java\\jdk-17
```

**Note:** Use double backslashes on Windows!

---

## Verification

### Check Java Version

```bash
# Check current Java version
java -version

# Should show:
# openjdk version "17.x.x" or "11.x.x"
```

### Check Gradle Java Version

```bash
./gradlew --version

# Should show:
# JVM: 17.x.x or 11.x.x
```

---

## Recommended Java Versions

| AGP Version | Minimum Java | Recommended |
|-------------|--------------|-------------|
| 8.0+ | Java 11 | Java 17 |
| 7.4+ | Java 11 | Java 17 |
| 7.0-7.3 | Java 11 | Java 11 |

**Current Project:** AGP 8.13.0 → **Requires Java 11+, Recommended Java 17**

---

## Download Java

### Option 1: Use Android Studio's JDK (Easiest)
Android Studio comes with JetBrains Runtime (JBR) which includes Java 17.

**Location:**
- Windows: `C:\Program Files\Android\Android Studio\jbr`
- Mac: `/Applications/Android Studio.app/Contents/jbr/Contents/Home`
- Linux: `/opt/android-studio/jbr`

### Option 2: Download Separately

**Recommended: Eclipse Temurin (formerly AdoptOpenJDK)**
- https://adoptium.net/temurin/releases/
- Choose: Java 17 (LTS)
- Platform: Your OS
- Package Type: JDK

**Alternative: Oracle JDK**
- https://www.oracle.com/java/technologies/downloads/
- Choose: Java 17

---

## After Installing Java

### 1. Verify Installation
```bash
java -version
# Should show Java 11 or 17
```

### 2. Clean Project
```bash
./gradlew clean
```

### 3. Build Project
```bash
./gradlew assembleDebug
```

### 4. Expected Output
```
BUILD SUCCESSFUL
```

---

## Troubleshooting

### Issue: "JAVA_HOME is not set"

**Solution:**
```bash
# Windows (CMD)
setx JAVA_HOME "C:\Program Files\Android\Android Studio\jbr"

# Windows (PowerShell)
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Android\Android Studio\jbr", "User")

# Mac/Linux
export JAVA_HOME="/path/to/jdk"
```

### Issue: "Still using Java 8"

**Solution:**
1. Close all terminals
2. Restart Android Studio
3. Check Gradle JDK setting in Android Studio
4. Try again

### Issue: "Multiple Java versions installed"

**Solution:**
Use gradle.properties to specify exact JDK:
```properties
org.gradle.java.home=C:\\Program Files\\Android\\Android Studio\\jbr
```

---

## Quick Fix for Android Studio

### Fastest Solution:

1. **Open Android Studio**
2. **File → Settings → Build Tools → Gradle**
3. **Gradle JDK → Select "jbr-17"**
4. **Click OK**
5. **File → Sync Project with Gradle Files**
6. **Build → Rebuild Project**

Done! ✅

---

## gradle.properties Configuration

Add these to `gradle.properties`:

```properties
# Java Configuration
org.gradle.java.home=C:\\Program Files\\Android\\Android Studio\\jbr

# Performance Optimizations
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.daemon=true

# Android Configuration
android.useAndroidX=true
android.enableJetifier=false

# Kotlin
kotlin.code.style=official
```

---

## Current Status

❌ **Java 8 detected** - Too old for AGP 8.13.0
✅ **Solution available** - Use Android Studio's JDK 17

### Next Steps:

1. **Configure Gradle JDK in Android Studio** (Method 1 above)
2. **Sync Gradle**
3. **Clean and build:**
   ```bash
   ./gradlew clean assembleDebug
   ```

---

## Expected After Fix

```bash
$ ./gradlew --version

Gradle 8.13
JVM: 17.0.x (JetBrains Runtime)
OS: Windows 11
```

```bash
$ ./gradlew clean assembleDebug

BUILD SUCCESSFUL in 45s
```

---

**Issue:** Java 8 too old for AGP 8.13.0
**Required:** Java 11 minimum, Java 17 recommended
**Solution:** Configure Gradle JDK in Android Studio
**Status:** Waiting for Java version update
