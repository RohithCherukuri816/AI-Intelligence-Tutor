package com.example.eduaituitor

import android.app.Application
import com.example.eduaituitor.ai.FirebenderService

/**
 * Application class for EduAI Tutor
 * Initializes app-wide services and configurations
 */
class EduAIApplication : Application() {
    
    lateinit var firebenderService: FirebenderService
        private set

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebender SDK
        firebenderService = FirebenderService()
        firebenderService.initialize()
        
        // Initialize other services
        initializeServices()
    }

    private fun initializeServices() {
        // Initialize Text-to-Speech if needed
        // Initialize analytics
        // Initialize crash reporting
    }
}
