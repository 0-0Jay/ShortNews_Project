package com.example.shortnews.ui.news

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.shortnews.api.RetrofitClient
import com.example.shortnews.databinding.FragmentBookmarkBinding
import com.example.shortnews.databinding.FragmentNewsMainBinding
import com.example.shortnews.model.Alarms
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookmarkViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    fun checkAlarm(token:String, binding : FragmentBookmarkBinding) {
        RetrofitClient.alarmApi.getAlarm(token).enqueue(object : Callback<Alarms> {
            override fun onResponse(call: Call<Alarms>, response: Response<Alarms>) {
                val alarmList = response.body()
                val alarmCheck = alarmList?.alarm?.filter { it.status == 1 }?.size
                if (alarmList?.alarm?.size == alarmCheck) binding.include.notificationfragment.notification.visibility = View.INVISIBLE
                else binding.include.notificationfragment.notification.visibility = View.VISIBLE
            }

            override fun onFailure(call: Call<Alarms>, t: Throwable) {
                Log.e("checkAlarm 실패", t.toString())
            }
        })
    }
}