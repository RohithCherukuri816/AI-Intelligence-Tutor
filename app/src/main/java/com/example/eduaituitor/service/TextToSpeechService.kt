package com.example.eduaituitor.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.speech.tts.TextToSpeech

class TextToSpeechService : Service() {
    private var tts: TextToSpeech? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        // Initialize TTS
    }

    override fun onDestroy() {
        tts?.shutdown()
        super.onDestroy()
    }
}
