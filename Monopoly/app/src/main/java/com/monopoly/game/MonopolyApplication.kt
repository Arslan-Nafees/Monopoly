package com.monopoly.game

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MonopolyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any application-wide configurations here
    }
} 