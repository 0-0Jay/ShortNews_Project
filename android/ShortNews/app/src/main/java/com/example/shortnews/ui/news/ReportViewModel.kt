package com.example.shortnews.ui.news

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shortnews.api.RetrofitClient
import com.example.shortnews.model.ReportRequest
import com.example.shortnews.model.ReportResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportViewModel : ViewModel() {

    val reportResponse = MutableLiveData<ReportResponse>()

    fun report(accessToken: String, content: String, type: String, reply_id: String?, news_id: String) {
        val reportRequest = ReportRequest(content, type, reply_id, news_id)
        RetrofitClient.newsApi.report(accessToken, reportRequest).enqueue(object :
            Callback<ReportResponse> {
            override fun onResponse(
                call: Call<ReportResponse>,
                response: Response<ReportResponse>
            ) {
                if(response.isSuccessful) {
                    Log.d("신고 요청 성공", response.body().toString())
                    reportResponse.value = response.body()
                } else {
                    Log.d("신고 요청 실패", response.toString())
                }
            }

            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                Log.e("신고 요청 실패", t.toString())
            }

        })
    }
}