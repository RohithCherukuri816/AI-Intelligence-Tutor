package com.example.eduaituitor.ai.prompts

object ExplanationPrompt {
    fun create(topic: String, userLevel: String): String {
        return """
            You are an educational AI tutor. Explain the following topic in a clear and engaging way 
            suitable for a $userLevel level student: $topic
        """.trimIndent()
    }
}
