package com.example.eduaituitor.ai.prompts

object QuizPrompt {
    fun create(topic: String, difficulty: String, questionCount: Int): String {
        return """
            Generate $questionCount multiple-choice questions about $topic at $difficulty difficulty level.
            Format: JSON with question, options array, correctAnswer index, and explanation.
        """.trimIndent()
    }
}
