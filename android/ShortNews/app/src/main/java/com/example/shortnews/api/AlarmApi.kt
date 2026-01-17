package com.example.shortnews.api

import com.example.shortnews.model.AlarmBodyRequest
import com.example.shortnews.model.AlarmStatus
import com.example.shortnews.model.AlarmSwitchRequest
import com.example.shortnews.model.Alarms
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.PATCH


interface AlarmApi {
    @GET("main/alarm")
    fun getAlarm(
        @Header("Authorization") authorization:String
    ) : Call<Alarms>

    @PATCH("main/alarm/switch")
    fun switchAlarm(
        @Header("Authorization") authorization:String,
        @Body alarm : AlarmSwitchRequest
    ): Call<AlarmStatus>

    @PATCH("main/alarm/select")
    fun selectAlarm(
        @Header("Authorization") authorization:String,
        @Body body : AlarmBodyRequest
    ): Call<AlarmStatus>

    @HTTP(method = "DELETE", path = "main/alarm/remove", hasBody = true)
    fun deleteAlarm(
        @Header("Authorization") authorization:String,
        @Body body : AlarmBodyRequest,
    ): Call<AlarmStatus>

    @DELETE("main/alarm/drop")
    fun dropAlarm(
        @Header("Authorization") authorization:String,
    ): Call<AlarmStatus>

    @PATCH("logout")
    fun logout(
        @Header("Authorization") authorization:String
    ) : Call<AlarmStatus>
}