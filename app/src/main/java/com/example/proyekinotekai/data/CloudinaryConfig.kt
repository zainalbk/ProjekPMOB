package com.example.proyekinotekai.data

import android.content.Context
import com.cloudinary.android.MediaManager

object CloudinaryConfig {
    private const val CLOUD_NAME = "dtchpo9mx"
    private const val API_KEY = "223927749361971"
    private const val API_SECRET = "kxsWvftUe7TyJdvJAwHpTgTZPyU"

    private var config: HashMap<String, String>? = null

    fun getConfig(): HashMap<String, String> {
        if (config == null) {
            config = HashMap()
            config!!["cloud_name"] = CLOUD_NAME
            config!!["api_key"] = API_KEY
            config!!["api_secret"] = API_SECRET
        }
        return config!!
    }

    fun setup(context: Context) {
        MediaManager.init(context, getConfig())
    }
}