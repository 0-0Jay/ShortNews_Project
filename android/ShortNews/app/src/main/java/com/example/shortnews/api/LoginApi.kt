package com.example.shortnews.api

import com.example.shortnews.model.FindIdRequest
import com.example.shortnews.model.FindIdResponse
import com.example.shortnews.model.FindPwRequest
import com.example.shortnews.model.FindPwResponse
import com.example.shortnews.model.KakaoLoginRequest
import com.example.shortnews.model.KakaoLoginResponse
import com.example.shortnews.model.LoginIdCheckRequest
import com.example.shortnews.model.LoginIdCheckResponse
import com.example.shortnews.model.LoginRequest
import com.example.shortnews.model.LoginResponse
import com.example.shortnews.model.NaverLoginRequest
import com.example.shortnews.model.NaverLoginResponse
import com.example.shortnews.model.NewsResponse
import com.example.shortnews.model.UpdatePwRequest
import com.example.shortnews.model.UpdatePwResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface LoginApi {

    @POST("login/password")
    fun loginCheck(@Body body: LoginRequest): Call<LoginResponse>

    @POST("login/findPassword")
    fun findPw(@Body body: FindPwRequest): Call<FindPwResponse>

    @POST("login/findId")
    fun findId(@Body body: FindIdRequest): Call<FindIdResponse>

    @PATCH("login/updatePassword")
    fun updatePw(@Body body: UpdatePwRequest): Call<UpdatePwResponse>

    @POST("/naver/appLogin")
    fun naverLogin(@Body body: NaverLoginRequest): Call<NaverLoginResponse>

    @POST("/kakao/appLogin")
    fun kakaoLogin(@Body body: KakaoLoginRequest): Call<KakaoLoginResponse>


}