package com.example.eduaituitor.ai

import com.example.eduaituitor.ai.prompts.ExplanationPrompt
import com.example.eduaituitor.ai.prompts.QuizPrompt
import kotlinx.coroutines.delay

/**
 * AI Client for interacting with Firebender/RunAnywhere SDK
 * Handles all AI model communications (GPT-5 / Claude)
 */
class AIClient {
    private val firebenderService = FirebenderService()

    /**
     * Send a chat message to the AI tutor
     * @param message User's question or topic
     * @return AI's educational response
     */
    suspend fun chat(message: String): String {
        val prompt = ExplanationPrompt.create(message, "beginner")
        return firebenderService.sendMessage(prompt)
    }

    /**
     * Generate quiz questions based on a topic
     * @param topic The subject to create questions about
     * @param difficulty Quiz difficulty level (easy, medium, hard)
     * @return JSON string with quiz questions
     */
    suspend fun generateQuiz(topic: String, difficulty: String): String {
        val prompt = QuizPrompt.create(topic, difficulty, 5)
        return firebenderService.sendMessage(prompt)
    }

    /**
     * Get personalized feedback on quiz performance
     * @param score User's quiz score
     * @param totalQuestions Total number of questions
     * @return Motivational feedback message
     */
    suspend fun getFeedback(score: Int, totalQuestions: Int): String {
        val percentage = (score.toFloat() / totalQuestions * 100).toInt()
        val prompt = "Provide encouraging feedback for a student who scored $score out of $totalQuestions ($percentage%) on a quiz."
        return firebenderService.sendMessage(prompt)
    }
}