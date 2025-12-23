package com.example.proyekinotekai

import android.app.Application
import com.example.proyekinotekai.data.CloudinaryConfig

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CloudinaryConfig.setup(this)
    }
}