@echo off
echo ========================================
echo Cleaning EduAI Tutor Build Cache
echo ========================================
echo.

echo [1/4] Cleaning Gradle build...
call gradlew clean
if %errorlevel% neq 0 (
    echo Warning: Gradle clean had issues, continuing...
)

echo.
echo [2/4] Deleting build folders...
if exist build (
    rmdir /s /q build
    echo Root build folder deleted
) else (
    echo Root build folder not found
)

if exist app\build (
    rmdir /s /q app\build
    echo App build folder deleted
) else (
    echo App build folder not found
)

echo.
echo [3/4] Deleting kapt cache...
if exist app\build\tmp\kapt3 (
    rmdir /s /q app\build\tmp\kapt3
    echo Kapt cache deleted
)

if exist app\build\generated (
    rmdir /s /q app\build\generated
    echo Generated sources deleted
)

echo.
echo [4/4] Deleting .gradle cache (optional)...
set /p CLEAN_GRADLE="Delete .gradle folder? This will re-download dependencies (y/n): "
if /i "%CLEAN_GRADLE%"=="y" (
    if exist .gradle (
        rmdir /s /q .gradle
        echo .gradle folder deleted
    )
)

echo.
echo ========================================
echo Build cache cleaned successfully!
echo ========================================
echo.
echo Next steps:
echo 1. Open Android Studio
echo 2. File -^> Invalidate Caches -^> Invalidate and Restart
echo 3. File -^> Sync Project with Gradle Files
echo 4. Build -^> Rebuild Project
echo.
echo Or run: gradlew assembleDebug
echo.
pause
