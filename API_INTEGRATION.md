# Firebender SDK Integration Guide

## Overview

This document explains how to integrate the Firebender Enterprise SDK (or RunAnywhere SDK) with the EduAI Tutor app for AI-powered tutoring capabilities.

## SDK Options

### Option 1: Firebender Enterprise SDK
- **Best for**: Production apps with high-quality AI responses
- **Models**: GPT-5, Claude 3
- **Features**: On-device processing, privacy-first, enterprise support

### Option 2: RunAnywhere SDK
- **Best for**: Cross-platform AI integration
- **Models**: Multiple AI models
- **Features**: Model switching, caching, offline support

### Option 3: Direct API Integration
- **Best for**: Custom implementations
- **Models**: OpenAI GPT-4/5, Anthropic Claude
- **Features**: Full control, custom prompts

## Integration Steps

### 1. Add SDK Dependency

#### For Firebender SDK:
```kotlin
// In app/build.gradle.kts
dependencies {
    implementation("com.firebender:android-sdk:1.0.0")
    implementation("com.firebender:ai-models:1.0.0")
}
```

#### For RunAnywhere SDK:
```kotlin
dependencies {
    implementation("com.runanywhere:sdk:2.0.0")
}
```

#### For Direct API (Retrofit):
```kotlin
dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}
```

### 2. Initialize SDK

#### In `FirebenderService.kt`:

```kotlin
package com.example.eduaituitor.ai

import com.firebender.sdk.FirebenderClient
import com.firebender.sdk.models.ChatRequest
import com.firebender.sdk.models.ChatResponse

class FirebenderService {
    
    private lateinit var client: FirebenderClient
    
    fun initialize() {
        client = FirebenderClient.Builder()
            .setApiKey(BuildConfig.FIREBENDER_API_KEY)
            .setModel("gpt-5")
            .enableOnDeviceProcessing(true)
            .setMaxTokens(1000)
            .setTemperature(0.7)
            .build()
    }
    
    suspend fun generateResponse(prompt: String): String {
        val request = ChatRequest.Builder()
            .addMessage("system", SYSTEM_PROMPT)
            .addMessage("user", prompt)
            .build()
            
        val response = client.chat(request)
        return response.content
    }
    
    companion object {
        private const val SYSTEM_PROMPT = """
            You are a friendly and knowledgeable AI tutor. 
            Your goal is to help students learn by:
            1. Explaining concepts clearly and simply
            2. Using examples and analogies
            3. Encouraging questions
            4. Providing positive feedback
            Always end explanations by asking if the student wants a quiz.
        """
    }
}
```

### 3. API Configuration

#### Create `local.properties`:
```properties
sdk.dir=/path/to/Android/sdk
firebender.api.key=fb_live_xxxxxxxxxxxxx
```

#### Update `build.gradle.kts`:
```kotlin
android {
    defaultConfig {
        // Read API key from local.properties
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField("String", "FIREBENDER_API_KEY", 
            "\"${properties.getProperty("firebender.api.key")}\"")
    }
    
    buildFeatures {
        buildConfig = true
    }
}
```

### 4. Implement AI Client

#### Update `AIClient.kt`:

```kotlin
package com.example.eduaituitor.ai

import com.example.eduaituitor.ai.prompts.ExplanationPrompt
import com.example.eduaituitor.ai.prompts.QuizPrompt

class AIClient {
    private val firebenderService = FirebenderService()

    suspend fun chat(message: String): String {
        val prompt = ExplanationPrompt.create(message, "beginner")
        return firebenderService.generateResponse(prompt)
    }

    suspend fun generateQuiz(topic: String, difficulty: String): String {
        val prompt = QuizPrompt.create(topic, difficulty, 5)
        return firebenderService.generateResponse(prompt)
    }
}
```

### 5. Handle Responses

#### Parse AI Responses:

```kotlin
// For chat responses - direct text
val response: String = aiClient.chat(userMessage)

// For quiz responses - JSON parsing
val quizJson: String = aiClient.generateQuiz(topic, "medium")
val questions: List<QuizQuestion> = parseQuizJson(quizJson)

private fun parseQuizJson(json: String): List<QuizQuestion> {
    val gson = Gson()
    val type = object : TypeToken<QuizResponse>() {}.type
    val quizResponse: QuizResponse = gson.fromJson(json, type)
    return quizResponse.questions
}

data class QuizResponse(
    val questions: List<QuizQuestionDto>
)

data class QuizQuestionDto(
    val question: String,
    val options: List<String>,
    val correct_answer: Int,
    val explanation: String
)
```

## Prompt Engineering

### Explanation Prompt Template

```kotlin
// In ExplanationPrompt.kt
object ExplanationPrompt {
    fun create(topic: String, userLevel: String): String {
        return """
            You are an educational AI tutor. The student is at a $userLevel level.
            
            Topic: $topic
            
            Please explain this topic by:
            1. Starting with a simple definition
            2. Breaking it down into key concepts
            3. Providing real-world examples
            4. Using analogies when helpful
            5. Keeping language appropriate for the student's level
            
            Format your response with:
            - Clear sections
            - Bullet points for key ideas
            - Emojis to make it engaging
            
            End by asking: "Would you like to test your understanding with a quiz?"
        """.trimIndent()
    }
}
```

### Quiz Generation Prompt Template

```kotlin
// In QuizPrompt.kt
object QuizPrompt {
    fun create(topic: String, difficulty: String, questionCount: Int): String {
        return """
            Generate $questionCount multiple-choice questions about: $topic
            
            Difficulty level: $difficulty
            
            Requirements:
            - Each question should have 4 options
            - Only one correct answer per question
            - Include a brief explanation for each answer
            - Questions should test understanding, not just memorization
            - Progress from easier to harder questions
            
            Return ONLY valid JSON in this exact format:
            {
              "questions": [
                {
                  "question": "Question text here?",
                  "options": ["Option A", "Option B", "Option C", "Option D"],
                  "correct_answer": 1,
                  "explanation": "Why this answer is correct..."
                }
              ]
            }
        """.trimIndent()
    }
}
```

## Error Handling

```kotlin
class AIRepository {
    suspend fun getAIResponse(message: String): String {
        return try {
            withTimeout(30000) { // 30 second timeout
                aiClient.chat(message)
            }
        } catch (e: TimeoutCancellationException) {
            "I'm taking longer than usual to respond. Please try again."
        } catch (e: IOException) {
            "I'm having trouble connecting. Please check your internet connection."
        } catch (e: Exception) {
            Log.e("AIRepository", "Error getting AI response", e)
            "I encountered an error. Please try again."
        }
    }
}
```

## Rate Limiting

```kotlin
class RateLimiter {
    private val requestTimes = mutableListOf<Long>()
    private val maxRequestsPerMinute = 10
    
    fun canMakeRequest(): Boolean {
        val now = System.currentTimeMillis()
        val oneMinuteAgo = now - 60000
        
        // Remove old requests
        requestTimes.removeAll { it < oneMinuteAgo }
        
        return requestTimes.size < maxRequestsPerMinute
    }
    
    fun recordRequest() {
        requestTimes.add(System.currentTimeMillis())
    }
}
```

## Caching

```kotlin
class ResponseCache {
    private val cache = LruCache<String, String>(50) // Cache 50 responses
    
    fun get(key: String): String? = cache.get(key)
    
    fun put(key: String, value: String) {
        cache.put(key, value)
    }
    
    fun generateKey(message: String): String {
        return message.lowercase().trim().hashCode().toString()
    }
}
```

## Testing

### Mock AI Responses for Testing

```kotlin
class MockFirebenderService : FirebenderService() {
    override suspend fun generateResponse(prompt: String): String {
        delay(1000) // Simulate network delay
        
        return when {
            prompt.contains("photosynthesis") -> MOCK_PHOTOSYNTHESIS_RESPONSE
            prompt.contains("quiz") -> MOCK_QUIZ_JSON
            else -> MOCK_DEFAULT_RESPONSE
        }
    }
    
    companion object {
        private const val MOCK_PHOTOSYNTHESIS_RESPONSE = """
            Photosynthesis is how plants make food using sunlight! 🌱☀️
            
            Here's the simple version:
            1. Plants take in CO₂ and water
            2. Using sunlight and chlorophyll, they create glucose
            3. They release oxygen as a byproduct
            
            Would you like to test your understanding with a quiz?
        """
        
        private const val MOCK_QUIZ_JSON = """
            {
              "questions": [
                {
                  "question": "What do plants need for photosynthesis?",
                  "options": ["Water only", "Sunlight only", "Water, sunlight, and CO₂", "Oxygen"],
                  "correct_answer": 2,
                  "explanation": "Plants need water, sunlight, and carbon dioxide for photosynthesis."
                }
              ]
            }
        """
    }
}
```

## Performance Optimization

### 1. Streaming Responses
```kotlin
suspend fun streamResponse(prompt: String): Flow<String> = flow {
    val response = client.streamChat(prompt)
    response.collect { chunk ->
        emit(chunk)
    }
}
```

### 2. Background Processing
```kotlin
class AIRepository {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    fun preloadCommonResponses() {
        scope.launch {
            // Preload common topics
            listOf("photosynthesis", "newton's laws", "algebra").forEach { topic ->
                val response = aiClient.chat("Explain $topic")
                cache.put(topic, response)
            }
        }
    }
}
```

## Security Best Practices

1. **Never commit API keys** to version control
2. **Use ProGuard** to obfuscate API calls
3. **Implement certificate pinning** for API requests
4. **Validate all AI responses** before displaying
5. **Sanitize user input** before sending to AI
6. **Rate limit requests** to prevent abuse
7. **Monitor API usage** and costs

## Troubleshooting

### Common Issues

1. **401 Unauthorized**: Check API key configuration
2. **429 Too Many Requests**: Implement rate limiting
3. **Timeout**: Increase timeout or check network
4. **Invalid JSON**: Improve prompt or add fallback parsing
5. **Empty responses**: Check model configuration

## Resources

- Firebender SDK Docs: https://docs.firebender.ai
- RunAnywhere SDK: https://runanywhere.dev
- OpenAI API: https://platform.openai.com/docs
- Anthropic Claude: https://docs.anthropic.com

## Support

For SDK-specific issues:
- Firebender: support@firebender.ai
- RunAnywhere: help@runanywhere.dev
- OpenAI: help.openai.com
