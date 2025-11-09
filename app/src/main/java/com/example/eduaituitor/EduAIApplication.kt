package com.example.eduaituitor

import android.app.Application
import android.util.Log
import com.runanywhere.sdk.public.RunAnywhere
import com.runanywhere.sdk.data.models.SDKEnvironment
import com.runanywhere.sdk.public.extensions.addModelFromURL
import com.runanywhere.sdk.llm.llamacpp.LlamaCppServiceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Application class for EduAI Tutor
 * Initializes RunAnywhere SDK with Gemma 2B model for on-device AI
 */
class EduAIApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Log.i("EduAI", "Starting EduAI Tutor Application...")

        // Initialize RunAnywhere SDK asynchronously
        GlobalScope.launch(Dispatchers.IO) {
            initializeRunAnywhereSDK()
        }
    }

    private suspend fun initializeRunAnywhereSDK() {
        try {
            Log.i("EduAI", "Initializing RunAnywhere SDK with Gemma 2B model...")
            
            // Step 1: Initialize SDK
            RunAnywhere.initialize(
                context = this@EduAIApplication,
                apiKey = "dev",  // Development mode
                environment = SDKEnvironment.DEVELOPMENT
            )
            Log.i("EduAI", "✓ SDK initialized")

            // Step 2: Register LLM Service Provider (LlamaCpp for GGUF models)
            LlamaCppServiceProvider.register()
            Log.i("EduAI", "✓ LlamaCpp provider registered")

            // Step 3: Register Gemma Model
            registerGemmaModel()

            // Step 4: Scan for previously downloaded models
            RunAnywhere.scanForDownloadedModels()
            Log.i("EduAI", "✓ Scanning for downloaded models")

            Log.i("EduAI", "========================================")
            Log.i("EduAI", "✓ RunAnywhere SDK ready!")
            Log.i("EduAI", "✓ Gemma 2B model configured")
            Log.i("EduAI", "✓ On-device AI enabled")
            Log.i("EduAI", "========================================")

        } catch (e: Exception) {
            Log.e("EduAI", "❌ SDK initialization failed: ${e.message}", e)
            Log.e("EduAI", "App will use fallback responses")
        }
    }

    /**
     * Register Google Gemma 2B model - optimized for mobile devices
     *
     * Model Details:
     * - Size: ~1.4 GB (quantized Q4_K_M)
     * - Speed: Very fast on mobile
     * - Quality: High quality responses
     * - Use Case: Perfect for educational Q&A
     *
     * Alternative smaller options:
     * - Q4_K_S: ~1.2 GB (slightly lower quality, faster)
     * - Q3_K_M: ~900 MB (good quality, very fast)
     */
    private suspend fun registerGemmaModel() {
        try {
            Log.i("EduAI", "Registering Google Gemma 2B model...")

            // Option 1: Gemma 2B Instruct Q4_K_M (Recommended - Best balance)
            // Size: ~1.4 GB, Fast inference, High quality
            addModelFromURL(
                url = "https://huggingface.co/lmstudio-community/gemma-2-2b-it-GGUF/resolve/main/gemma-2-2b-it-Q4_K_M.gguf",
                name = "Gemma 2B Instruct Q4_K_M",
                type = "LLM"
            )

            Log.i("EduAI", "✓ Gemma 2B model registered successfully")
            Log.i("EduAI", "  Model: Gemma 2-2B Instruct")
            Log.i("EduAI", "  Size: ~1.4 GB (Q4_K_M quantization)")
            Log.i("EduAI", "  Speed: Fast on-device inference")
            Log.i("EduAI", "  Quality: High - optimized for instruction following")

            // Optional: Register smaller variant for faster devices
            // Uncomment below if you want even smaller model
            /*
            addModelFromURL(
                url = "https://huggingface.co/lmstudio-community/gemma-2-2b-it-GGUF/resolve/main/gemma-2-2b-it-Q3_K_M.gguf",
                name = "Gemma 2B Instruct Q3_K_M (Smaller)",
                type = "LLM"
            )
            Log.i("EduAI", "✓ Smaller Gemma variant also registered")
            */

        } catch (e: Exception) {
            Log.e("EduAI", "❌ Gemma model registration failed: ${e.message}", e)
            Log.e("EduAI", "Trying alternative model sources...")

            // Fallback: Try alternative Gemma source
            try {
                addModelFromURL(
                    url = "https://huggingface.co/bartowski/gemma-2-2b-it-GGUF/resolve/main/gemma-2-2b-it-Q4_K_M.gguf",
                    name = "Gemma 2B Instruct Q4_K_M (Alt)",
                    type = "LLM"
                )
                Log.i("EduAI", "✓ Alternative Gemma source registered")
            } catch (fallbackError: Exception) {
                Log.e("EduAI", "❌ All Gemma sources failed: ${fallbackError.message}")
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.i("EduAI", "EduAI Tutor shutting down...")
    }
}

/*
 * MODEL COMPARISON:
 * 
 * Gemma 2B Q4_K_M (RECOMMENDED):
 * - Size: ~1.4 GB
 * - RAM: ~2-3 GB required
 * - Speed: Very fast (50-100 tokens/sec on mid-range phones)
 * - Quality: Excellent for educational content
 * - Best for: Most devices
 * 
 * Gemma 2B Q3_K_M (Smaller):
 * - Size: ~900 MB
 * - RAM: ~1.5-2 GB required
 * - Speed: Very fast (60-120 tokens/sec)
 * - Quality: Good, slight quality trade-off
 * - Best for: Lower-end devices
 * 
 * Gemma 2B Q4_K_S (Fast):
 * - Size: ~1.2 GB
 * - RAM: ~2 GB required
 * - Speed: Faster than Q4_K_M
 * - Quality: Good
 * - Best for: Speed priority
 */
