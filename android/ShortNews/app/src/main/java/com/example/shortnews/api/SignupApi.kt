package com.example.shortnews.api

import com.example.shortnews.model.IdCheckRequest
import com.example.shortnews.model.IdCheckResponse
import com.example.shortnews.model.PhoneCheckRequest
import com.example.shortnews.model.PhoneCheckResponse
import com.example.shortnews.model.SelectCategoryRequest
import com.example.shortnews.model.SelectCategoryResponse
import com.example.shortnews.model.SubmitRequest
import com.example.shortnews.model.SubmitResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface SignupApi {

    @POST("signup/idCheck")
    fun idCheck(@Body body: IdCheckRequest): Call <IdCheckResponse>

    @POST("signup/phoneCheck")
    fun phoneCheck(@Body body: PhoneCheckRequest): Call <PhoneCheckResponse>

    @POST("signup/submit")
    fun submit(@Body body: SubmitRequest): Call <SubmitResponse>

    @POST("signup/selectCategory")
    fun selectCategory(@Body body: SelectCategoryRequest): Call <SelectCategoryResponse>
}