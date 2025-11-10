package com.example.eduaituitor.repository

import android.app.Application
import android.util.Log
import com.example.eduaituitor.ai.FirebenderService
import com.example.eduaituitor.data.ChatMessage
import com.example.eduaituitor.data.QuizQuestion
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Repository for handling AI interactions with RunAnywhere model
 * Uses RunAnywhere SDK via FirebenderService
 */
class AIRepository(private val application: Application) {

    private val firebenderService = FirebenderService()

    /**
     * Get AI response using RunAnywhere model
     * Falls back to sample responses if model isn't loaded
     */
    suspend fun getAIResponse(userMessage: String, chatHistory: List<ChatMessage>): String {
        Log.d("AIRepository", "Getting AI response for: ${userMessage.take(50)}...")

        return try {
            // Build context from chat history
            val context = chatHistory.takeLast(5).map {
                if (it.isUser) "Student: ${it.content}" else "Tutor: ${it.content}"
            }

            Log.d("AIRepository", "Calling RunAnywhere model...")

            // Call RunAnywhere via FirebenderService
            val response = firebenderService.sendMessage(userMessage, context)

            // Check if response indicates model is downloading
            if (response.contains("First-Run Setup") || response.contains("downloading")) {
                Log.w("AIRepository", "Model is downloading, showing setup message")
                response
            } else if (response.startsWith("Error:")) {
                Log.w("AIRepository", "Model error, using fallback")
                getFallbackResponse(userMessage)
            } else {
                Log.d("AIRepository", "✓ Got response from model: ${response.take(100)}...")
                response
            }
        } catch (e: Exception) {
            Log.e("AIRepository", "Error calling model", e)
            Log.i("AIRepository", "Using fallback response")
            getFallbackResponse(userMessage)
        }
    }

    /**
     * Generate quiz using RunAnywhere model
     * Falls back to sample questions if model isn't loaded
     */
    suspend fun generateQuiz(topic: String): List<QuizQuestion> {
        Log.d("AIRepository", "Generating quiz for: $topic")

        return try {
            Log.d("AIRepository", "Calling model for quiz generation...")

            // Call RunAnywhere to generate quiz
            val quizText = firebenderService.generateQuiz(topic, 5)

            // Check if response is valid
            if (quizText.startsWith("Error:") || quizText.contains("Unable to generate")) {
                Log.w("AIRepository", "Model not ready for quiz, using fallback")
                getSampleQuestions(topic)
            } else {
                // Parse the quiz response from model
                Log.d("AIRepository", "Parsing quiz from model response...")
                val questions = parseQuizFromGemma(quizText)

                if (questions.isEmpty()) {
                    Log.w("AIRepository", "Failed to parse model quiz, using fallback")
                    getSampleQuestions(topic)
                } else {
                    Log.d("AIRepository", "✓ Got ${questions.size} questions from model")
                    questions
                }
            }
        } catch (e: Exception) {
            Log.e("AIRepository", "Error generating quiz with model", e)
            getSampleQuestions(topic)
        }
    }

    /**
     * Parse quiz questions from model's text response
     * Expected format:
     * Q: Question text
     * A) Option 1
     * B) Option 2
     * C) Option 3
     * D) Option 4
     * Correct: B
     */
    private fun parseQuizFromGemma(quizText: String): List<QuizQuestion> {
        val questions = mutableListOf<QuizQuestion>()

        try {
            // Split by "Q:" to get individual questions
            val questionBlocks = quizText.split(Regex("Q:\\s*")).filter { it.isNotBlank() }

            for (block in questionBlocks) {
                val lines = block.trim().lines().filter { it.isNotBlank() }
                if (lines.isEmpty()) continue

                // First line is the question
                val questionText = lines[0].trim()

                // Find options (A), B), C), D))
                val options = mutableListOf<String>()
                var correctAnswer = -1

                for (line in lines) {
                    when {
                        line.trim().startsWith("A)", ignoreCase = true) -> {
                            options.add(line.substring(2).trim())
                        }

                        line.trim().startsWith("B)", ignoreCase = true) -> {
                            options.add(line.substring(2).trim())
                        }

                        line.trim().startsWith("C)", ignoreCase = true) -> {
                            options.add(line.substring(2).trim())
                        }

                        line.trim().startsWith("D)", ignoreCase = true) -> {
                            options.add(line.substring(2).trim())
                        }

                        line.contains("Correct:", ignoreCase = true) -> {
                            val answerLetter = line.substringAfter("Correct:", "")
                                .trim()
                                .firstOrNull()
                                ?.uppercaseChar()

                            correctAnswer = when (answerLetter) {
                                'A' -> 0
                                'B' -> 1
                                'C' -> 2
                                'D' -> 3
                                else -> -1
                            }
                        }
                    }
                }

                // Only add if we have valid question, 4 options, and correct answer
                if (questionText.isNotBlank() && options.size == 4 && correctAnswer in 0..3) {
                    questions.add(
                        QuizQuestion(
                            question = questionText,
                            options = options,
                            correctAnswer = correctAnswer
                        )
                    )
                    Log.d("AIRepository", "Parsed question: ${questionText.take(50)}...")
                }
            }
        } catch (e: Exception) {
            Log.e("AIRepository", "Error parsing quiz", e)
        }

        return questions
    }

    /**
     * Enhanced fallback response with better messaging
     */
    private suspend fun getFallbackResponse(userMessage: String): String {
        // Add a small delay to simulate processing
        delay(800 + Random.nextLong(400))

        val topic = extractTopic(userMessage)

        return when {
            userMessage.contains("hello", ignoreCase = true) ||
                    userMessage.contains("hi", ignoreCase = true) -> {
                """👋 Hello! I'm your AI tutor powered by RunAnywhere.

**Note**: I'm currently using pre-cached educational responses. For real-time AI responses:
• The RunAnywhere SDK is integrating on-device models
• Full AI features coming soon!

In the meantime, I can help you learn:
• "Teach me about photosynthesis"
• "Explain Newton's Laws"
• "How does programming work?"
• "Create a quiz on [topic]"

What would you like to learn about?"""
            }
            userMessage.contains("quiz", ignoreCase = true) -> {
                "I'd be happy to create a quiz for you about $topic! Let me prepare some questions..."
            }
            else -> {
                """📚 Let me explain $topic:

${getSampleExplanation(topic)}

---
**💡 Pro Tip**: This is using our knowledge base. For personalized, real-time AI tutoring, we're working on full model integration!

Would you like me to create a quiz to test your understanding?"""
            }
        }
    }

    private fun extractTopic(message: String): String {
        val commonTopics = listOf(
            "photosynthesis", "physics", "math", "mathematics", "history", "programming",
            "chemistry", "biology", "gravity", "newton", "calculus", "algebra", "geometry"
        )

        val lowerMessage = message.lowercase()
        val foundTopic = commonTopics.find { lowerMessage.contains(it) }

        if (foundTopic != null) return foundTopic.replaceFirstChar { it.uppercase() }

        val words = message.lowercase().split(" ")
        val stopWords = listOf(
            "teach",
            "explain",
            "learn",
            "about",
            "what",
            "is",
            "me",
            "the",
            "a",
            "an",
            "how",
            "why"
        )
        val topic = words.find { !stopWords.contains(it) && it.length > 3 }

        return topic?.replaceFirstChar { it.uppercase() } ?: "this topic"
    }

    private fun getSampleExplanation(topic: String): String {
        return when (topic.lowercase()) {
            "photosynthesis" -> """Photosynthesis is the process by which plants convert sunlight into energy.

**Key Steps**:
1. Plants absorb sunlight through chlorophyll in their leaves
2. They take in CO₂ from the air through stomata
3. Roots absorb water from the soil
4. Using sunlight, water and CO₂ are converted into glucose (sugar) and oxygen

**Chemical Equation**: 6CO₂ + 6H₂O + light energy → C₆H₁₂O₆ + 6O₂

This process is crucial because it produces the oxygen we breathe and forms the base of most food chains!"""

            "physics", "gravity", "newton" -> """Physics is the study of matter, energy, and their interactions. Let's focus on Newton's Laws of Motion:

**Newton's First Law** (Inertia): An object at rest stays at rest, and an object in motion stays in motion unless acted upon by a force.

**Newton's Second Law** (F=ma): Force equals mass times acceleration. This tells us how much force is needed to move an object.

**Newton's Third Law**: For every action, there is an equal and opposite reaction.

These laws explain everything from how cars move to how rockets launch into space!"""

            "math", "mathematics", "algebra", "calculus", "geometry" -> """Mathematics is the language of patterns, numbers, and logical reasoning.

**Key Areas**:
• **Algebra**: Working with variables and equations (e.g., 2x + 5 = 13)
• **Geometry**: Study of shapes, angles, and spatial relationships
• **Calculus**: Understanding rates of change and areas under curves

Math is essential in everyday life - from calculating tips to understanding statistics in the news!"""

            "programming", "coding" -> """Programming is giving instructions to a computer to perform tasks.

**Key Concepts**:
• **Variables**: Store information (like numbers or text)
• **Loops**: Repeat actions multiple times
• **Conditions**: Make decisions (if this, then that)
• **Functions**: Reusable blocks of code

Popular languages include Python, Java, JavaScript, and Kotlin. Each has its strengths for different tasks!"""

            else -> """This topic covers fundamental concepts that build upon each other.

**Key Points**:
• Understanding the basic principles and definitions
• How these concepts apply in real-world scenarios
• The relationships between different aspects
• Practical applications and examples

Learning any new subject takes practice and patience. Start with the fundamentals and gradually build your knowledge!"""
        }
    }

    private fun getSampleQuestions(topic: String): List<QuizQuestion> {
        return when (topic.lowercase()) {
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
                ),
                QuizQuestion(
                    "Where in plant cells does photosynthesis occur?",
                    listOf("Nucleus", "Mitochondria", "Chloroplasts", "Cell wall"),
                    2
                ),
                QuizQuestion(
                    "What pigment in plants absorbs light for photosynthesis?",
                    listOf("Carotene", "Chlorophyll", "Melanin", "Hemoglobin"),
                    1
                )
            )

            "physics", "gravity", "newton" -> listOf(
                QuizQuestion(
                    "According to Newton's First Law, what happens to an object at rest?",
                    listOf(
                        "It moves randomly",
                        "It stays at rest unless a force acts on it",
                        "It accelerates",
                        "It falls down"
                    ),
                    1
                ),
                QuizQuestion(
                    "What does F=ma represent in Newton's Second Law?",
                    listOf(
                        "Force = mass × acceleration",
                        "Force = mass + acceleration",
                        "Force = mass ÷ acceleration",
                        "Force = mass - acceleration"
                    ),
                    0
                ),
                QuizQuestion(
                    "Newton's Third Law states that for every action there is:",
                    listOf(
                        "A similar reaction",
                        "An equal and opposite reaction",
                        "A delayed reaction",
                        "No reaction"
                    ),
                    1
                ),
                QuizQuestion(
                    "What is inertia?",
                    listOf(
                        "Speed of light",
                        "Resistance to changes in motion",
                        "Force of gravity",
                        "Magnetic force"
                    ),
                    1
                ),
                QuizQuestion(
                    "If you push a wall, why don't you move the wall?",
                    listOf(
                        "Walls can't move",
                        "The wall pushes back with equal force",
                        "Friction prevents it",
                        "Gravity holds it"
                    ),
                    1
                )
            )

            "math", "mathematics", "algebra" -> listOf(
                QuizQuestion(
                    "What is 2 + 2 × 3?",
                    listOf("12", "8", "10", "6"),
                    1
                ),
                QuizQuestion(
                    "In algebra, what is x if 2x + 4 = 10?",
                    listOf("2", "3", "4", "5"),
                    1
                ),
                QuizQuestion(
                    "What is the area of a rectangle with length 5 and width 3?",
                    listOf("8", "15", "30", "16"),
                    1
                ),
                QuizQuestion(
                    "What is 25% of 80?",
                    listOf("15", "20", "25", "30"),
                    1
                ),
                QuizQuestion(
                    "What is the square root of 64?",
                    listOf("6", "7", "8", "9"),
                    2
                )
            )

            else -> listOf(
                QuizQuestion(
                    "What is a key concept in understanding $topic?",
                    listOf(
                        "Memorizing facts only",
                        "Understanding principles and connections",
                        "Speed reading",
                        "Guessing answers"
                    ),
                    1
                ),
                QuizQuestion(
                    "Why is practice important when learning $topic?",
                    listOf(
                        "It's not important",
                        "It reinforces understanding and builds skills",
                        "It wastes time",
                        "It makes learning harder"
                    ),
                    1
                ),
                QuizQuestion(
                    "What's the best way to approach difficult concepts in $topic?",
                    listOf(
                        "Give up immediately",
                        "Skip to easier topics",
                        "Break them into smaller parts",
                        "Memorize without understanding"
                    ),
                    2
                ),
                QuizQuestion(
                    "How does $topic relate to real-world applications?",
                    listOf(
                        "It doesn't",
                        "It has practical uses in many fields",
                        "Only in textbooks",
                        "Only for experts"
                    ),
                    1
                ),
                QuizQuestion(
                    "What mindset helps most when learning $topic?",
                    listOf(
                        "Fixed mindset",
                        "Growth mindset and curiosity",
                        "Avoiding challenges",
                        "Perfectionism"
                    ),
                    1
                )
            )
        }
    }
}