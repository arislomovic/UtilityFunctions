package com.example.myapplication

import android.app.Application
import android.content.ContextWrapper

class MyApplication : Application() {


    override fun onCreate() {
        super.onCreate()

        INSTANCE = this
    }


    companion object {
        lateinit var INSTANCE: MyApplication

        fun getApplicationContext() = ContextWrapper(INSTANCE.applicationContext!!)
    }


}