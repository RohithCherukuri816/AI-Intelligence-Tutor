# EduAI Tutor 📚🤖

An AI-powered Android learning assistant that provides personalized tutoring, interactive quizzes, and progress tracking - all with privacy-first on-device AI.

## Features

### 🎓 Core Functionality
- **AI Chat Tutor**: Ask questions on any topic and get clear, educational explanations
- **Auto-Generated Quizzes**: Test your knowledge with AI-created multiple-choice questions
- **Progress Tracking**: Monitor your learning journey with detailed statistics
- **Voice Tutoring**: Optional text-to-speech for spoken explanations
- **Dark Mode**: Comfortable learning in any lighting condition

### 🔒 Privacy-First
- On-device AI processing with RunAnywhere SDK
- No data leaves your device unless explicitly needed
- Secure local storage for all learning data

## Tech Stack

- **Platform**: Android (Kotlin)
- **UI**: Jetpack Compose with Material 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **AI**: Firebender Enterprise (GPT-5 / Claude)
- **Database**: Room for local storage
- **Settings**: DataStore Preferences
- **Async**: Kotlin Coroutines & Flow

## Project Structure

```
app/src/main/java/com/example/eduaituitor/
├── MainActivity.kt                 # App entry point
├── data/                          # Data models
│   ├── Models.kt                  # ChatMessage, QuizQuestion, etc.
│   ├── database/                  # Room database
│   └── local/                     # DataStore settings
├── ai/                            # AI integration
│   ├── FirebenderService.kt       # Firebender SDK wrapper
│   ├── AIClient.kt                # AI client interface
│   └── prompts/                   # Prompt templates
├── repository/                    # Data repositories
├── viewmodel/                     # ViewModels
│   └── MainViewModel.kt           # Main app state
├── ui/                            # Compose UI
│   ├── screens/                   # Screen composables
│   │   ├── WelcomeScreen.kt
│   │   ├── ChatScreen.kt
│   │   ├── QuizScreen.kt
│   │   ├── ProgressScreen.kt
│   │   └── SettingsScreen.kt
│   ├── components/                # Reusable components
│   ├── theme/                     # Material 3 theming
│   └── navigation/                # Navigation setup
└── utils/                         # Utilities
    ├── Constants.kt
    ├── Extensions.kt
    └── TextToSpeechManager.kt
```

## Setup Instructions

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 24+ (Android 7.0+)
- Kotlin 1.9+
- Firebender Enterprise account

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/eduai-tutor.git
   cd eduai-tutor
   ```

2. **Configure Firebender SDK**
   - Sign up for Firebender Enterprise account
   - Get your API key
   - Add to `local.properties`:
     ```properties
     firebender.api.key=YOUR_API_KEY_HERE
     ```

3. **Add Firebender SDK dependency**
   In `app/build.gradle.kts`, uncomment:
   ```kotlin
   implementation("com.firebender:sdk:x.x.x")
   ```

4. **Sync and Build**
   ```bash
   ./gradlew build
   ```

5. **Run the app**
   - Connect an Android device or start an emulator
   - Click Run in Android Studio

## Usage

### Chat with AI Tutor
1. Open the app and tap "Start Learning"
2. Type your question in the chat input
3. Receive detailed explanations from the AI
4. Ask follow-up questions for deeper understanding

### Take a Quiz
1. After learning a topic, tap "Take a Quiz"
2. Answer multiple-choice questions
3. Get instant feedback with explanations
4. View your score and motivational message

### Track Progress
1. Navigate to the Progress tab
2. View your learning statistics
3. See topics studied and quiz scores
4. Monitor your improvement over time

## AI Prompts

### Explanation Prompt
```
You are a friendly AI tutor. Explain the topic '{{user_input}}' 
in clear, student-friendly language, with examples. 
End by asking if the learner wants a quiz.
```

### Quiz Generation Prompt
```
Generate 5 multiple-choice questions with 4 options each, 
based on the topic '{{last_topic}}'. 
Return the result in JSON with keys: question, options, correct_answer.
```

## Configuration

### AI Model Selection
In `FirebenderService.kt`:
```kotlin
private const val MODEL_GPT5 = "gpt-5"
private const val MODEL_CLAUDE = "claude-3"
```

### Quiz Settings
In `Constants.kt`:
```kotlin
const val DEFAULT_QUIZ_QUESTIONS = 5
const val QUIZ_TIME_LIMIT = 300 // seconds
```

## Development

### Adding New Features
1. Create data models in `data/Models.kt`
2. Add repository methods in `repository/`
3. Update ViewModel in `viewmodel/MainViewModel.kt`
4. Create UI in `ui/screens/`

### Testing
```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## Roadmap

- [ ] Voice input for questions
- [ ] Offline mode with cached responses
- [ ] Multi-language support
- [ ] Spaced repetition system
- [ ] Study reminders
- [ ] Export progress reports
- [ ] Collaborative learning features

## Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## License

This project is licensed under the MIT License - see LICENSE file for details.

## Acknowledgments

- Firebender Enterprise for AI capabilities
- RunAnywhere SDK for on-device processing
- Material Design 3 for beautiful UI
- Android Jetpack for modern architecture

## Support

For issues or questions:
- Open an issue on GitHub
- Email: support@eduaitutor.com
- Documentation: https://docs.eduaitutor.com

---

Made with ❤️ for learners everywhere
