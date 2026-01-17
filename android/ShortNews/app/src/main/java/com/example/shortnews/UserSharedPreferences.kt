package com.example.shortnews

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class UserSharedPreferences : Application() {

    companion object {
        lateinit var sharedPreferences: SharedPreferences

        fun getAccessToken():String {
            return sharedPreferences.getString("access_token", null).toString()
        }
    }

    override fun onCreate() {
        super.onCreate()
        // 공통 SharedPreferences 초기화
        sharedPreferences = applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)

    }



}