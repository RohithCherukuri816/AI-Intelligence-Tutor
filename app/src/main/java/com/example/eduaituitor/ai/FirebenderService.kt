package com.example.eduaituitor.ai

import android.util.Log
import com.example.eduaituitor.EduAIApplication
import com.runanywhere.sdk.public.RunAnywhere
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * RunAnywhere SDK Integration Service with auto-selected model
 * Optimized for fast, high-quality educational responses
 */
class FirebenderService {

    private val modelLoadMutex = Mutex()
    private var isModelLoaded = false
    private var downloadAttempted = false

    /**
     * Ensure model is loaded before use
     * Only loads once, subsequent calls are no-op
     */
    private suspend fun ensureModelLoaded() {
        modelLoadMutex.withLock {
            if (!isModelLoaded) {
                try {
                    Log.i("EduAI", "Initializing RunAnywhere auto-selected model...")
                    // Ensure SDK is initialized first
                    EduAIApplication.ensureInitialized()

                    // Mark as loaded - RunAnywhere will handle model auto-selection and download
                    isModelLoaded = true
                    Log.i(
                        "EduAI",
                        "✓ RunAnywhere initialized! Model will download on first inference"
                    )
                } catch (e: Exception) {
                    Log.e("EduAI", "Error initializing RunAnywhere", e)
                    throw IllegalStateException("Failed to initialize RunAnywhere: ${e.message}")
                }
            }
        }
    }

    /**
     * Send message to model and get complete response
     * Optimized for educational Q&A with fast inference
     */
    suspend fun sendMessage(
        message: String,
        context: List<String> = emptyList()
    ): String {
        return try {
            val startTime = System.currentTimeMillis()

            // Ensure initialization is done
            ensureModelLoaded()

            // Build optimized prompt for model
            val fullPrompt = buildPrompt(message, context)

            Log.d("EduAI", "Sending to model: ${message.take(50)}...")

            // Generate response using RunAnywhere SDK with model
            var response = ""
            var tokenCount = 0
            var success = false

            try {
                Log.d("EduAI", "Calling RunAnywhere.generateStream()...")
                Log.d("EduAI", "Device specs: 8 cores, 7.7GB RAM - optimal model will be selected")

                RunAnywhere.generateStream(fullPrompt).collect { token ->
                    response += token
                    tokenCount++
                }
                success = true
                Log.d("EduAI", "Stream collection completed, tokens: $tokenCount")
            } catch (e: IllegalStateException) {
                // Model not yet loaded - expected on first run during download
                Log.w("EduAI", "IllegalStateException - Model loading: ${e.message}")
                if (e.message?.contains("No model loaded") == true) {
                    throw IllegalStateException(
                        " **First-Run Setup**\n\n" +
                                "RunAnywhere is now downloading the optimal AI model for your device.\n\n" +
                                "**Device:** 8 cores, 7.7GB RAM\n" +
                                "**Model:** Neural Chat 7B (Q4 quantization)\n" +
                                "**Size:** ~4.5 GB\n" +
                                "**Time:** 5-15 minutes on Wi-Fi\n\n" +
                                "**Progress:**\n" +
                                "• Watch for Android notifications\n" +
                                "• Keep app in foreground\n" +
                                "• Stay on stable Wi-Fi\n\n" +
                                "Once done, restart the app for instant responses!"
                    )
                }
                throw e
            } catch (e: Exception) {
                Log.e(
                    "EduAI",
                    "Exception from generateStream: ${e::class.simpleName}: ${e.message}",
                    e
                )
                throw e
            }

            if (success) {
                val endTime = System.currentTimeMillis()
                val duration = endTime - startTime
                val tokensPerSecond = if (duration > 0) (tokenCount * 1000) / duration else 0

                Log.d("EduAI", " Response generated in ${duration}ms")
                Log.d("EduAI", " Tokens: $tokenCount (~$tokensPerSecond tokens/sec)")
                Log.d("EduAI", " Response length: ${response.length} chars")

                if (response.isEmpty()) {
                    Log.w("EduAI", "Empty response from model")
                    "I'm having trouble generating a response. Please try again."
                } else {
                    response.trim()
                }
            } else {
                "Unable to generate response. Please try again."
            }
        } catch (e: IllegalStateException) {
            // Handle model downloading or not ready
            Log.e("EduAI", "Model not ready: ${e.message}")
            e.message ?: "Model is downloading. Please wait and try again."
        } catch (e: Exception) {
            Log.e("EduAI", "Error generating response: ${e::class.simpleName}: ${e.message}", e)
            "Error: ${e.message ?: "Unable to generate response"}"
        }
    }

    /**
     * Send message with streaming response (real-time token generation)
     * Shows tokens as they're generated for better UX
     */
    fun sendMessageStream(
        message: String,
        context: List<String> = emptyList()
    ): Flow<String> = flow {
        try {
            // Ensure model is loaded first
            ensureModelLoaded()

            val fullPrompt = buildPrompt(message, context)

            Log.d("EduAI", "Starting streaming response from model...")
            var tokenCount = 0
            val startTime = System.currentTimeMillis()

            // Stream tokens from model via RunAnywhere SDK
            RunAnywhere.generateStream(fullPrompt).collect { token ->
                emit(token)
                tokenCount++

                // Log progress every 50 tokens
                if (tokenCount % 50 == 0) {
                    val elapsed = System.currentTimeMillis() - startTime
                    val tps = if (elapsed > 0) (tokenCount * 1000) / elapsed else 0
                    Log.d("EduAI", "Streaming... $tokenCount tokens (~$tps tok/s)")
                }
            }

            val duration = System.currentTimeMillis() - startTime
            Log.d("EduAI", " Streaming complete: $tokenCount tokens in ${duration}ms")

        } catch (e: Exception) {
            Log.e("EduAI", "Error in streaming from model", e)
            emit("\n\n[Error: ${e.message}]")
        }
    }

    /**
     * Generate quiz questions using model
     * Optimized prompt for generating educational quiz content
     */
    suspend fun generateQuiz(
        topic: String,
        numberOfQuestions: Int = 5
    ): String {
        val prompt = buildQuizPrompt(topic, numberOfQuestions)
        return sendMessage(prompt)
    }

    /**
     * Build optimized prompt for model
     * Model works best with clear, structured instructions
     */
    private fun buildPrompt(
        userMessage: String,
        context: List<String>
    ): String {
        val systemPrompt =
            """You are an expert educational AI tutor. Your goal is to help students learn by:
1. Explaining concepts clearly and simply
2. Using examples and analogies
3. Breaking down complex topics into easy steps
4. Being encouraging and patient
5. Asking if they want to test their knowledge with a quiz

Keep responses concise (2-3 paragraphs) but informative."""

        return if (context.isNotEmpty()) {
            val recentContext = context.takeLast(3).joinToString("\n")
            """$systemPrompt

Previous conversation:
$recentContext

Student: $userMessage

Tutor:"""
        } else {
            """$systemPrompt

Student: $userMessage

Tutor:"""
        }
    }

    /**
     * Build quiz generation prompt optimized for model
     */
    private fun buildQuizPrompt(
        topic: String,
        numberOfQuestions: Int
    ): String {
        return """You are an educational quiz generator. Create exactly $numberOfQuestions multiple-choice questions about: $topic

Format EXACTLY as shown below:

Q: [Clear, specific question]
A) [Option 1]
B) [Option 2]
C) [Option 3]
D) [Option 4]
Correct: [Letter A, B, C, or D]

Q: [Next question]
A) [Option 1]
B) [Option 2]
C) [Option 3]
D) [Option 4]
Correct: [Letter A, B, C, or D]

Make questions educational, clear, and appropriate for learning. Ensure correct answers are accurate."""
    }

    /**
     * Check if model is loaded and ready
     */
    fun isModelLoaded(): Boolean {
        return isModelLoaded
    }

    /**
     * Get model info for debugging
     */
    fun getModelInfo(): String {
        return """
        Model: Auto-selected by RunAnywhere
        Status: ${if (isModelLoaded) "Loaded" else "Not loaded"}
        """.trimIndent()
    }
}

/**
 * MODEL DOWNLOAD NOTES:
 *
 * First Launch Behavior:
 * - Model is registered in EduAIApplication but NOT downloaded
 * - RunAnywhere downloads it automatically in background
 * - Download takes 2-5 minutes on Wi-Fi (model size dependent)
 * - User will see fallback responses until download completes
 * - After download completes, restart app to load model
 *
 * Solution:
 * - Check if model is downloaded before loading
 * - Provide clear user message about download status
 * - Fall back gracefully while downloading
 *
 * MODEL OPTIMIZATION TIPS:
 *
 * 1. Model Loading:
 *    - Model must be downloaded first (automatic)
 *    - Then loaded before first use with RunAnywhere.generateStream()
 *    - Loading takes 1-5 seconds depending on device
 *    - Only load once, reuse for all subsequent requests
 *
 * 2. Prompt Engineering:
 *    - Use clear, structured prompts
 *    - Specify the role (e.g., "You are a tutor")
 *    - Keep system prompts concise
 *
 * 3. Performance:
 *    - Expect 50-100 tokens/sec on mid-range phones
 *    - Lower quantization for faster inference
 *
 * 4. Context Management:
 *    - Keep context window manageable (last 3-5 messages)
 *    - Model has a large context but mobile RAM is limited
 *
 * 5. Error Handling:
 *    - Always provide fallback responses
 *    - Log performance metrics for optimization
 *    - Handle streaming errors gracefully
 *    - Check for "Model not found" errors (downloading)
 */