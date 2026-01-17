package com.example.shortnews.ui.news

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shortnews.api.RetrofitClient
import com.example.shortnews.databinding.FragmentNewsContentBinding
import com.example.shortnews.databinding.FragmentNewsMainBinding
import com.example.shortnews.model.Alarms
import com.example.shortnews.model.LikeReponse
import com.example.shortnews.model.LikeRequest
import com.example.shortnews.model.SelectNewsResponse
import com.example.shortnews.model.SourceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Header

class NewsContentViewModel : ViewModel() {

    val select_news = MutableLiveData<SelectNewsResponse>()
    val likeReponse = MutableLiveData<LikeReponse>()
    val sourceResponse = MutableLiveData<SourceResponse>()

    fun selectNews(access_token:String, news_id:String) {

        RetrofitClient.newsApi.selectNews(access_token, news_id).enqueue(object :
            Callback<SelectNewsResponse> {
            override fun onResponse(call: Call<SelectNewsResponse>, response: Response<SelectNewsResponse>) {
                Log.d("selectNews response:", response.toString())
                if (response.isSuccessful) {
                    select_news.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("선택한 뉴스 가져오기 응답", response.body().toString())
                } else {
                    Log.d("요청 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<SelectNewsResponse>, t: Throwable) {
                Log.e("요청 실패", t.toString())
            }
        })
    }

    fun like(access_token:String, like:Int, news_id:String, reply_id:String?) {
        val likeRequest = LikeRequest(like, news_id, reply_id)

        RetrofitClient.newsApi.like(access_token, likeRequest).enqueue(object :
            Callback<LikeReponse> {
            override fun onResponse(call: Call<LikeReponse>, response: Response<LikeReponse>) {
                Log.d("selectNews response:", response.toString())
                if (response.isSuccessful) {
                    likeReponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("좋아요 응답", response.body().toString())
                } else {
                    Log.d("요청 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<LikeReponse>, t: Throwable) {
                Log.e("요청 실패", t.toString())
            }
        })
    }

    fun source(access_token:String, news_id:String) {

        RetrofitClient.newsApi.source(access_token, news_id).enqueue(object :
            Callback<SourceResponse> {
            override fun onResponse(call: Call<SourceResponse>, response: Response<SourceResponse>) {
                Log.d("selectNews response:", response.toString())
                if (response.isSuccessful) {
                    sourceResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("출처 응답", response.body().toString())
                } else {
                    Log.d("요청 실패" , response.toString())
                    Log.d("요청 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<SourceResponse>, t: Throwable) {
                Log.e("요청 실패", t.toString())
            }
        })
    }
    fun checkAlarm(token:String, binding : FragmentNewsContentBinding) {
        RetrofitClient.alarmApi.getAlarm(token).enqueue(object : Callback<Alarms>{
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