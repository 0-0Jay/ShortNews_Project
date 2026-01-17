package com.example.shortnews.ui.news

import androidx.lifecycle.ViewModel
import com.example.shortnews.api.RetrofitClient
import com.example.shortnews.model.Alarms
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.viewbinding.ViewBinding
import com.example.shortnews.databinding.FragmentNavBarBinding
import com.example.shortnews.databinding.FragmentNotificationIconBinding
import com.example.shortnews.model.AlarmBodyRequest
import com.example.shortnews.model.AlarmChild
import com.example.shortnews.model.AlarmStatus
import com.example.shortnews.model.AlarmSwitchRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class AlarmDialogViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var alarm = MutableLiveData<List<AlarmChild>>()
    fun getAlarm(authorization: String) {
        RetrofitClient.alarmApi.getAlarm(authorization).enqueue(object : Callback<Alarms> {
            override fun onResponse(call: Call<Alarms>, response: Response<Alarms>) {
                Log.d("News response:", response.toString())
                if (response.isSuccessful) {
                    alarm.value = response.body()?.alarm
                    Log.d("요청 성공", response.toString())
                    Log.d("알람 가져오기 응답:", response.body().toString())
                } else {
                    Log.d("요청 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<Alarms>, t: Throwable) {
                Log.e("요청 실패", t.toString())
            }
        })
    }
    fun getAlarm_forCheck(authorization: String, binding: FragmentNotificationIconBinding) {
        RetrofitClient.alarmApi.getAlarm(authorization).enqueue(object : Callback<Alarms> {
            override fun onResponse(call: Call<Alarms>, response: Response<Alarms>) {
                Log.d("News response:", response.toString())
                if (response.isSuccessful) {
                    Log.d("요청 성공", response.toString())
                    Log.d("알람 가져오기 응답:", response.body().toString())
                    val data = response.body()?.alarm
                    data?.forEach { alarmChild ->
                        if (alarmChild.status == 0) {
                            binding.notification.visibility = View.VISIBLE
                            return@forEach
                        }
                    }
                } else {
                    Log.d("요청 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<Alarms>, t: Throwable) {
                Log.e("요청 실패", t.toString())
            }
        })
    }

    fun switchAlarm(authorization: String, alarm : Int){
        val data = AlarmSwitchRequest(alarm)

        RetrofitClient.alarmApi.switchAlarm(authorization, data).enqueue(object : Callback<AlarmStatus>{
            override fun onResponse(call: Call<AlarmStatus>, response: Response<AlarmStatus>) {
                if (response.isSuccessful){
                    Log.d("알림 수정 성공", response.toString())
                }else {
                    Log.d("알림 수정 실패", response.body().toString())
                }
            }

            override fun onFailure(call: Call<AlarmStatus>, t: Throwable) {
                Log.e("알림 수정 실패 onFailure", t.toString())
            }
        })
    }

    fun deleteAlarm(authorization: String, target : AlarmChild, binding: FragmentNavBarBinding){
        val data = AlarmBodyRequest(target.time, target.link)
        RetrofitClient.alarmApi.deleteAlarm(authorization, data).enqueue(object : Callback<AlarmStatus>{
            override fun onResponse(call: Call<AlarmStatus>, response: Response<AlarmStatus>) {
                if (response.isSuccessful){
                    Log.d("알림 삭제 성공", response.toString())
                    Log.d("삭제 후 목록", alarm.toString())
                    val temp = alarm.value?.toMutableList()
                    temp?.remove(target)
                    alarm.value = temp!!
                    if (temp.isEmpty()) binding.notificationfragment.notification.visibility = View.INVISIBLE
                }else {
                    Log.d("알림 삭제 실패", response.body().toString())
                }
            }

            override fun onFailure(call: Call<AlarmStatus>, t: Throwable) {
                Log.e("알림 수정 실패 onFailure", t.toString())
            }
        })
    }

    fun dropAlarm(token:String) {
        RetrofitClient.alarmApi.dropAlarm(token).enqueue(object : Callback<AlarmStatus>{
            override fun onResponse(call: Call<AlarmStatus>, response: Response<AlarmStatus>) {
                if (response.isSuccessful){
                    Log.d("알림 드랍 성공", response.toString())
                    getAlarm(token)

                }else {
                    Log.d("알림 드랍 실패", response.body().toString())
                }
            }
            override fun onFailure(call: Call<AlarmStatus>, t: Throwable) {
                Log.e("알림 드랍 실패 onFailure", t.toString())
            }
        })
    }

    fun selectAlarm(token:String, time : String, news_id : String) {
        val data = AlarmBodyRequest(time, news_id)
        RetrofitClient.alarmApi.selectAlarm(token, data).enqueue(object : Callback<AlarmStatus>{
            override fun onResponse(call: Call<AlarmStatus>, response: Response<AlarmStatus>) {
                if (response.isSuccessful){
                    Log.d("알림 읽기 성공", response.toString())
                    getAlarm(token)
                    alarm.value
                }else {
                    Log.d("알림 드랍 실패", response.body().toString())
                }
            }

            override fun onFailure(call: Call<AlarmStatus>, t: Throwable) {
                Log.e("알림 읽기 실패 onFailure", t.toString())
            }
        })
    }

    fun makeInt(data:String?) : Int {
        var tmp = data?.replace("/", "")
        tmp = data?.replace(" ", "")
        tmp = data?.replace(".", "")
        tmp = data?.replace(":", "")
        return Integer.valueOf(tmp);
    }
}