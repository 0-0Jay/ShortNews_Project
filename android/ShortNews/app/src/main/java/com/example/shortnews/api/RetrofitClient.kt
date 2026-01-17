package com.example.shortnews.api


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object{

        private val client  = Retrofit.Builder()
            .baseUrl("http://3.35.26.160:8090/")
            .addConverterFactory(GsonConverterFactory.create()) // String을 객체로 변환 (서버가 json을 리턴할 때 GsonConverterFactory)z
            .build()

        val loginApi:LoginApi = client.create(LoginApi::class.java)
        val signupApi:SignupApi = client.create(SignupApi::class.java)
        val newsApi:NewsApi = client.create(NewsApi::class.java)
        val myPageApi:MyPageApi = client.create(MyPageApi::class.java)
        val alarmApi:AlarmApi = client.create(AlarmApi::class.java)
    }


}