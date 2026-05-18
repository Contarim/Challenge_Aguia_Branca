package com.conectagab.app

import android.app.Application

class ConectaGABApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.init(this)
    }
}
