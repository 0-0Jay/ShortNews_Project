package com.example.shortnews.ui.news

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shortnews.api.RetrofitClient
import com.example.shortnews.api.RetrofitClientPython
import com.example.shortnews.databinding.FragmentNewsMainBinding
import com.example.shortnews.model.AlarmRealtimeData
import com.example.shortnews.model.AlarmStatus
import com.example.shortnews.model.Alarms
import com.example.shortnews.model.BookmarkRequest
import com.example.shortnews.model.BookmarkResponse
import com.example.shortnews.model.KeywordResponse
import com.example.shortnews.model.NewsResponse
import com.example.shortnews.model.RecommendResponse
import com.example.shortnews.model.SearchRequest
import com.example.shortnews.model.SearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Header

class NewsMainViewModel : ViewModel() {

    val news = MutableLiveData<NewsResponse>()
    val recommend = MutableLiveData<RecommendResponse>()
    val searchResponse = MutableLiveData<SearchResponse>()
    val bookmarkResponse = MutableLiveData<BookmarkResponse>()
    val keywordResponse = MutableLiveData<KeywordResponse>()
    val alarmRealtimeData = MutableLiveData<AlarmRealtimeData>()

    fun selectCategory(authorization: String, category: String, date: String) {

        RetrofitClient.newsApi.selectCategory(authorization, category, date).enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    news.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("카테고리 뉴스 가져오기 응답:", response.body().toString())
                } else {
                    Log.d("요청 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                Log.e("요청 실패", t.toString())
            }
        })
    }

    fun getRecommend(access_token: String) {

        RetrofitClientPython.newsApi.getRecommend(access_token).enqueue(object : Callback<RecommendResponse> {
            override fun onResponse(call: Call<RecommendResponse>, response: Response<RecommendResponse>) {
                if (response.isSuccessful) {
                    recommend.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("추천 뉴스 가져오기 응답:", response.body().toString())
                } else {
                    Log.d("요청 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<RecommendResponse>, t: Throwable) {
                Log.e("요청 실패", t.toString())
            }
        })
    }

    fun search(access_token: String, keyword: String, st: Int, ed: Int) {
        val searchRequest = SearchRequest(0, 1000000)

        RetrofitClient.newsApi.search(access_token, keyword, st, ed).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    searchResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("검색 뉴스 가져오기 응답:", response.body().toString())
                } else {
                    Log.d("요청 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Log.e("요청 실패", t.toString())
            }
        })
    }

    fun bookmark(access_token: String, news_id:String, type:Boolean) {
        val bookmarkRequest = BookmarkRequest(news_id, type)

        RetrofitClient.newsApi.bookmark(access_token, bookmarkRequest).enqueue(object : Callback<BookmarkResponse> {
            override fun onResponse(call: Call<BookmarkResponse>, response: Response<BookmarkResponse>) {
                if (response.isSuccessful) {
                    bookmarkResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("북마크 응답:", response.body().toString())
                } else {
                    Log.d("요청 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<BookmarkResponse>, t: Throwable) {
                Log.e("요청 실패", t.toString())
            }
        })
    }

    fun keyword(access_token: String, date: String) {
        RetrofitClientPython.newsApi.keyword(access_token, date).enqueue(object : Callback<KeywordResponse> {
            override fun onResponse(call: Call<KeywordResponse>, response: Response<KeywordResponse>) {
                Log.d("Search response:", response.toString())
                if (response.isSuccessful) {
                    keywordResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("키워드 응답:", response.body().toString())
                } else {
                    Log.d("요청 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<KeywordResponse>, t: Throwable) {
                Log.e("요청 실패", t.toString())
            }
        })
    }

    fun checkAlarm(token:String, binding : FragmentNewsMainBinding) {
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
