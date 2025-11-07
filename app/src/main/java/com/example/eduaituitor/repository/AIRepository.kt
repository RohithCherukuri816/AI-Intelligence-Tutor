package com.example.eduaituitor.repository

import com.example.eduaituitor.ai.FirebenderService
import com.example.eduaituitor.data.ChatMessage
import com.example.eduaituitor.data.QuizQuestion
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Repository for handling AI interactions
 * In a real app, this would integrate with Firebender SDK
 */
class AIRepository {

    private val firebenderService = FirebenderService()

    // Simulate AI response - replace with actual Firebender SDK calls
    suspend fun getAIResponse(userMessage: String, chatHistory: List<ChatMessage>): String {
        // Simulate API call delay
        delay(1000 + Random.nextLong(500))

        // In production, use the actual service:
        // return firebenderService.sendMessage(userMessage)

        val topic = extractTopic(userMessage)

        return when {
            userMessage.contains("quiz", ignoreCase = true) -> {
                "I'd be happy to create a quiz for you! Let me generate some questions about $topic."
            }
            else -> {
                // Simulate AI explanation
                """
                I'd be happy to explain $topic!
                
                ${getSampleExplanation(topic)}
                
                This is a simplified explanation to get you started. Would you like me to go deeper into any specific aspect, or would you prefer to take a quiz to test your understanding?
                """.trimIndent()
            }
        }
    }

    // Simulate quiz generation - replace with actual AI call
    suspend fun generateQuiz(topic: String): List<QuizQuestion> {
        delay(1500)
        return getSampleQuestions(topic)
    }

    private fun extractTopic(message: String): String {
        val topics = listOf("photosynthesis", "physics", "math", "history", "programming")
        return topics.find { message.contains(it, ignoreCase = true) } ?: "the topic"
    }

    private fun getSampleExplanation(topic: String): String {
        return when (topic.toLowerCase()) {
            "photosynthesis" -> "Photosynthesis is how plants convert sunlight into energy. They take in carbon dioxide and water, and using sunlight, create glucose (sugar) and oxygen. The chemical formula is: 6CO₂ + 6H₂O → C₆H₁₂O₆ + 6O₂"
            "physics" -> "Physics is the study of matter, energy, and their interactions. It covers everything from tiny atoms to the entire universe! Key areas include mechanics, thermodynamics, and quantum physics."
            "math" -> "Mathematics is the language of patterns and relationships. It helps us solve problems using numbers, shapes, and logical reasoning. Important branches include algebra, geometry, and calculus."
            else -> "This is a fascinating topic with many interesting aspects to explore. The key concepts involve fundamental principles that build upon each other to create a comprehensive understanding."
        }
    }

    private fun getSampleQuestions(topic: String): List<QuizQuestion> {
        return when (topic.toLowerCase()) {
            "photosynthesis" -> listOf(
                QuizQuestion(
                    "What is the primary energy source for photosynthesis?",
                    listOf("Water", "Sunlight", "Oxygen", "Carbon dioxide"),
                    1
                ),
                QuizQuestion(
                    "Which gas do plants take in during photosynthesis?",
                    listOf("Oxygen", "Nitrogen", "Carbon dioxide", "Hydrogen"),
                    2
                ),
                QuizQuestion(
                    "What is the main product of photosynthesis that plants use for energy?",
                    listOf("Oxygen", "Water", "Glucose", "Chlorophyll"),
                    2
                )
            )
            else -> listOf(
                QuizQuestion(
                    "What is the main focus of $topic?",
                    listOf("Option A", "Option B", "Option C", "Option D"),
                    0
                ),
                QuizQuestion(
                    "Which concept is most important in $topic?",
                    listOf("Concept A", "Concept B", "Concept C", "Concept D"),
                    1
                ),
                QuizQuestion(
                    "How does $topic benefit learning?",
                    listOf("Benefit A", "Benefit B", "Benefit C", "Benefit D"),
                    2
                )
            )
        }
    }
}