package com.lincolnstewart.android.reachout

import android.app.Application

class ReachOutApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ContactRepository.initialize(this)
    }
}