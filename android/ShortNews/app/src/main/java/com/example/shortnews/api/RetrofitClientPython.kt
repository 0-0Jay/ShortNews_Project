package com.example.shortnews.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClientPython {

    companion object{

        private val client  = Retrofit.Builder()
            .baseUrl("http://3.35.26.160:8091/")
            .addConverterFactory(GsonConverterFactory.create()) // String을 객체로 변환 (서버가 json을 리턴할 때 GsonConverterFactory)
            .build()

        val newsApi:NewsApi = client.create(NewsApi::class.java)
    }

}