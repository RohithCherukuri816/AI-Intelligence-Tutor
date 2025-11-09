package com.example.eduaituitor.ai

import android.util.Log
import com.runanywhere.sdk.public.RunAnywhere
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * RunAnywhere SDK Integration Service with Gemma 2B Model
 * Optimized for fast, high-quality educational responses
 */
class FirebenderService {

    private val modelLoadMutex = Mutex()
    private var isModelLoaded = false
    private val modelName = "Gemma 2B Instruct Q4_K_M" // Must match EduAIApplication registration

    /**
     * Ensure model is loaded before use
     * Only loads once, subsequent calls are no-op
     */
    private suspend fun ensureModelLoaded() {
        modelLoadMutex.withLock {
            if (!isModelLoaded) {
                try {
                    Log.i("EduAI", "Loading Gemma 2B model for first use...")

                    // Try to load the model
                    RunAnywhere.loadModel(modelName)

                    isModelLoaded = true
                    Log.i("EduAI", "✓ Gemma 2B model loaded successfully!")
                } catch (e: IllegalArgumentException) {
                    // Model not found - still downloading
                    Log.w("EduAI", "Model not found: ${e.message}")
                    throw IllegalStateException(
                        "📥 Model is downloading in background. This is your first launch and the Gemma 2B model (1.4 GB) is being downloaded. " +
                                "Please wait 2-5 minutes and try again. You'll see a notification when ready."
                    )
                } catch (e: Exception) {
                    Log.e("EduAI", "❌ Failed to load Gemma model: ${e.message}", e)
                    throw IllegalStateException(
                        "Failed to load model: ${e.message}. The model may still be downloading or there may be insufficient memory."
                    )
                }
            }
        }
    }

    /**
     * Send message to Gemma 2B model and get complete response
     * Optimized for educational Q&A with fast inference
     */
    suspend fun sendMessage(
        message: String,
        context: List<String> = emptyList()
    ): String {
        return try {
            val startTime = System.currentTimeMillis()

            // Ensure model is loaded first
            ensureModelLoaded()

            // Build optimized prompt for Gemma
            val fullPrompt = buildGemmaPrompt(message, context)

            Log.d("EduAI", "Sending to Gemma 2B: ${message.take(50)}...")

            // Generate response using RunAnywhere SDK with Gemma
            var response = ""
            var tokenCount = 0

            RunAnywhere.generateStream(fullPrompt).collect { token ->
                response += token
                tokenCount++
            }

            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime
            val tokensPerSecond = if (duration > 0) (tokenCount * 1000) / duration else 0

            Log.d("EduAI", "✓ Response generated in ${duration}ms")
            Log.d("EduAI", "✓ Tokens: $tokenCount (~$tokensPerSecond tokens/sec)")
            Log.d("EduAI", "✓ Response length: ${response.length} chars")

            if (response.isEmpty()) {
                Log.w("EduAI", "Empty response from model")
                "I'm having trouble generating a response. The Gemma model may still be initializing. Please try again in a moment."
            } else {
                response.trim()
            }
        } catch (e: IllegalStateException) {
            // Handle model not downloaded / not ready
            Log.e("EduAI", "Model not ready: ${e.message}")
            e.message ?: "Model is not ready. Please wait for the model to download."
        } catch (e: Exception) {
            Log.e("EduAI", "Error generating response from Gemma", e)
            when {
                e.message?.contains("Model not found") == true -> {
                    "Model is downloading. This is your first launch and the Gemma 2B model (1.4 GB) is being downloaded. Please wait 2-5 minutes and try again."
                }
                e.message?.contains("No model loaded") == true -> {
                    "Model is not loaded yet. Please wait for the download to complete (2-5 minutes on first launch)."
                }
                e.message?.contains("Out of memory") == true || e.message?.contains("OOM") == true -> {
                    "Insufficient memory to run the AI model. Please close some apps and try again, or restart your device."
                }
                else -> {
                    "Error: ${e.message ?: "Unable to generate response"}. The model may still be downloading or loading."
                }
            }
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

            val fullPrompt = buildGemmaPrompt(message, context)

            Log.d("EduAI", "Starting streaming response from Gemma...")
            var tokenCount = 0
            val startTime = System.currentTimeMillis()

            // Stream tokens from Gemma via RunAnywhere SDK
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
            Log.d("EduAI", "✓ Streaming complete: $tokenCount tokens in ${duration}ms")

        } catch (e: Exception) {
            Log.e("EduAI", "Error in streaming from Gemma", e)
            emit("\n\n[Error: ${e.message}]")
        }
    }

    /**
     * Generate quiz questions using Gemma 2B
     * Optimized prompt for generating educational quiz content
     */
    suspend fun generateQuiz(
        topic: String,
        numberOfQuestions: Int = 5
    ): String {
        val prompt = buildGemmaQuizPrompt(topic, numberOfQuestions)
        return sendMessage(prompt)
    }

    /**
     * Build optimized prompt for Gemma 2B model
     * Gemma works best with clear, structured instructions
     */
    private fun buildGemmaPrompt(
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
     * Build quiz generation prompt optimized for Gemma
     */
    private fun buildGemmaQuizPrompt(
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
     * Check if Gemma model is loaded and ready
     */
    fun isModelLoaded(): Boolean {
        return isModelLoaded
    }

    /**
     * Get model info for debugging
     */
    fun getModelInfo(): String {
        return """
        Model: Google Gemma 2B Instruct
        Quantization: Q4_K_M
        Size: ~1.4 GB
        Speed: 50-100 tokens/sec (device dependent)
        Optimized: Educational content
        Status: ${if (isModelLoaded) "Loaded" else "Not loaded"}
        """.trimIndent()
    }
}

/**
 * GEMMA 2B MODEL DOWNLOAD NOTES:
 *
 * First Launch Behavior:
 * - Model is registered in EduAIApplication but NOT downloaded
 * - RunAnywhere downloads it automatically in background
 * - Download takes 2-5 minutes on Wi-Fi (1.4 GB)
 * - User will see fallback responses until download completes
 * - After download completes, restart app to load model
 *
 * Solution:
 * - Check if model is downloaded before loading
 * - Provide clear user message about download status
 * - Fall back gracefully while downloading
 *
 * GEMMA 2B OPTIMIZATION TIPS:
 *
 * 1. Model Loading:
 *    - Model must be downloaded first (automatic)
 *    - Then loaded before first use with RunAnywhere.loadModel()
 *    - Loading takes 1-5 seconds depending on device
 *    - Only load once, reuse for all subsequent requests
 *
 * 2. Prompt Engineering:
 *    - Use clear, structured prompts
 *    - Specify the role (e.g., "You are a tutor")
 *    - Keep system prompts concise
 *
 * 3. Performance:
 *    - Q4_K_M offers best quality/speed balance
 *    - Expect 50-100 tokens/sec on mid-range phones
 *    - Lower quantization (Q3_K_M) for faster inference
 *
 * 4. Context Management:
 *    - Keep context window manageable (last 3-5 messages)
 *    - Gemma has 8K token context but mobile RAM is limited
 *
 * 5. Error Handling:
 *    - Always provide fallback responses
 *    - Log performance metrics for optimization
 *    - Handle streaming errors gracefully
 *    - Check for "Model not found" errors (downloading)
 */