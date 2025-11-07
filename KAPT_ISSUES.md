# KAPT (Kotlin Annotation Processing) Issues

## Current Issue: Duplicate Class Error

### Error Message
```
error: duplicate class: com.example.eduaituitor.data.database.dao.QuizSessionDao
```

### Root Cause
KAPT (Kotlin Annotation Processing Tool) caches generated files. When files are modified or the build is interrupted, stale cache can cause duplicate class errors.

---

## Solution: Clean KAPT Cache

### Method 1: Quick Clean (Recommended)

Run the clean script:
```bash
clean-build.bat
```

Or manually:
```bash
# 1. Clean Gradle
./gradlew clean

# 2. Delete build folders
rm -rf app/build
rm -rf build

# 3. Rebuild
./gradlew assembleDebug
```

### Method 2: Deep Clean

1. **Close Android Studio**

2. **Delete all build artifacts:**
   ```bash
   rm -rf app/build
   rm -rf build
   rm -rf .gradle
   rm -rf app/.cxx
   ```

3. **Reopen Android Studio**

4. **Invalidate Caches:**
   - File → Invalidate Caches
   - Select "Invalidate and Restart"

5. **Sync and Build:**
   ```bash
   ./gradlew clean
   ./gradlew assembleDebug
   ```

### Method 3: Nuclear Option

If the above don't work:

```bash
# Delete everything
rm -rf app/build
rm -rf build
rm -rf .gradle
rm -rf .idea
rm -rf ~/.gradle/caches

# Reopen project in Android Studio
# Let it reimport everything
# Then build
```

---

## Understanding KAPT

### What is KAPT?
KAPT is the Kotlin Annotation Processing Tool used by Room, Dagger, and other libraries to generate code at compile time.

### What Does KAPT Generate?
For Room DAOs, KAPT generates:
- Implementation classes for DAOs
- Database implementation
- Type converters
- Query validation

### Where Are Generated Files?
```
app/build/generated/source/kapt/
app/build/tmp/kapt3/
```

### Why Duplicate Classes?
1. **Stale cache** - Old generated files not cleaned
2. **Interrupted build** - Build stopped mid-generation
3. **File system issues** - Files not properly deleted
4. **IDE cache** - Android Studio cache out of sync

---

## Prevention

### 1. Always Clean Before Major Changes
```bash
./gradlew clean
```

### 2. Use Gradle Build Cache Properly
In `gradle.properties`:
```properties
org.gradle.caching=true
org.gradle.parallel=true
```

### 3. Exclude Build Folders from Version Control
In `.gitignore`:
```
build/
.gradle/
*.iml
.idea/
local.properties
```

### 4. Regular Cache Cleanup
Once a week:
```bash
./gradlew clean
rm -rf .gradle/caches
```

---

## KAPT Configuration

### Current Configuration (app/build.gradle.kts)

```kotlin
plugins {
    id("kotlin-kapt")
}

dependencies {
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
}
```

### Optional: KAPT Arguments

Add to `android` block:
```kotlin
kapt {
    correctErrorTypes = true
    useBuildCache = true
    
    arguments {
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.incremental", "true")
        arg("room.expandProjection", "true")
    }
}
```

---

## Verification

### Check Generated Files
```bash
# List generated DAO implementations
ls app/build/generated/source/kapt/debug/com/example/eduaituitor/data/database/dao/

# Should see:
# QuizSessionDao_Impl.java
# LearningProgressDao_Impl.java
```

### Check for Duplicates
```bash
# Search for duplicate class definitions
find app/build -name "*QuizSessionDao*.java" -type f
```

### Verify Build Success
```bash
./gradlew assembleDebug --info | grep "QuizSessionDao"
```

---

## Common KAPT Errors

### 1. "Cannot find symbol"
**Cause:** Room compiler can't find entity classes
**Fix:** Check package names and imports

### 2. "Duplicate class"
**Cause:** Stale cache
**Fix:** Clean build (this document)

### 3. "Schema export directory not set"
**Cause:** Room schema location not configured
**Fix:** Add kapt argument (see above)

### 4. "Incremental annotation processing"
**Cause:** KAPT incremental processing issues
**Fix:** Disable incremental:
```kotlin
kapt {
    useBuildCache = false
}
```

---

## Debugging KAPT

### Enable Verbose Logging
```bash
./gradlew assembleDebug --info --stacktrace
```

### Check KAPT Output
```bash
# View generated files
cat app/build/generated/source/kapt/debug/com/example/eduaituitor/data/database/dao/QuizSessionDao_Impl.java
```

### Verify Room Configuration
```kotlin
// In AppDatabase.kt
@Database(
    entities = [QuizSession::class, LearningProgress::class],
    version = 1,
    exportSchema = false  // Set to true for production
)
abstract class AppDatabase : RoomDatabase()
```

---

## Quick Reference

| Issue | Solution |
|-------|----------|
| Duplicate class | `./gradlew clean` |
| Cannot find symbol | Check imports |
| Build slow | Enable caching |
| Stale cache | Delete build folders |
| IDE out of sync | Invalidate caches |

---

## After Cleaning

### 1. Verify Clean
```bash
# These should not exist:
ls app/build  # Should not exist or be empty
ls build      # Should not exist or be empty
```

### 2. Rebuild
```bash
./gradlew assembleDebug
```

### 3. Expected Output
```
> Task :app:kaptGenerateStubsDebugKotlin
> Task :app:kaptDebugKotlin
> Task :app:compileDebugKotlin
BUILD SUCCESSFUL
```

### 4. Verify Generated Files
```bash
ls app/build/generated/source/kapt/debug/com/example/eduaituitor/data/database/dao/
# Should see: QuizSessionDao_Impl.java
```

---

## Prevention Checklist

- [ ] Always run `./gradlew clean` before major changes
- [ ] Invalidate caches after pulling code
- [ ] Don't interrupt builds
- [ ] Keep Android Studio updated
- [ ] Use stable Gradle versions
- [ ] Exclude build folders from VCS

---

## Current Status

✅ **Build folders cleaned**
✅ **KAPT cache cleared**
✅ **Ready to rebuild**

### Next Steps:
1. Run: `./gradlew clean`
2. Run: `./gradlew assembleDebug`
3. Expected: BUILD SUCCESSFUL

---

**Last Updated:** After encountering duplicate class error
**Issue:** KAPT cache causing duplicate classes
**Solution:** Clean build folders and rebuild
**Status:** Ready to fix
