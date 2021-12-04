package com.kailin.goal.app

import android.app.Application

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: MyApplication
    }
}