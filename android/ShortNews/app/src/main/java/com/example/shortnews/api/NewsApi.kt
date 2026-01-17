package com.example.shortnews.api

import com.example.shortnews.model.BookmarkRequest
import com.example.shortnews.model.BookmarkResponse
import com.example.shortnews.model.KeywordResponse
import com.example.shortnews.model.LikeReponse
import com.example.shortnews.model.LikeRequest
import com.example.shortnews.model.LoginRequest
import com.example.shortnews.model.LoginResponse
import com.example.shortnews.model.NewsReply
import com.example.shortnews.model.NewsReplyDeleteRequest
import com.example.shortnews.model.NewsReplyRequest
import com.example.shortnews.model.NewsReplyResponse
import com.example.shortnews.model.NewsReplyUpdateRequest
import com.example.shortnews.model.NewsResponse
import com.example.shortnews.model.RecommendResponse
import com.example.shortnews.model.ReportRequest
import com.example.shortnews.model.ReportResponse
import com.example.shortnews.model.SearchRequest
import com.example.shortnews.model.SearchResponse
import com.example.shortnews.model.SelectNewsResponse
import com.example.shortnews.model.SourceResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NewsApi {

    @GET("main/news/{category}/{date}")
    fun selectCategory(
        @Header("Authorization") authorization:String,
        @Path("category") category:String,
        @Path("date") date:String
    ): Call<NewsResponse>

    @GET("main/selectNews/{news_id}")
    fun selectNews(
        @Header("Authorization") access_token:String,
        @Path("news_id") news_id:String
    ): Call<SelectNewsResponse>

    @GET("main/selectNews/{news_id}/reply")
    fun getNewsReply(
        @Header("Authorization") access_token:String,
        @Path("news_id") news_id:String
    ): Call<NewsReplyResponse>

    @PATCH("main/write")
    fun createReply(
        @Header("Authorization") access_token:String,
        @Body body:NewsReplyRequest
    ): Call<NewsReplyResponse>

    @PATCH("main/delete")
    fun deleteReply(
        @Header("Authorization") access_token:String,
        @Body body:NewsReplyDeleteRequest
    ): Call<NewsReplyResponse>

    @PATCH("main/update")
    fun updateReply(
        @Header("Authorization") access_token:String,
        @Body body:NewsReplyUpdateRequest
    ): Call<NewsReplyResponse>

    @GET("main/recommend")
    fun getRecommend(
        @Header("Authorization") access_token:String
    ): Call<RecommendResponse>

    @PATCH("main/like")
    fun like(
        @Header("Authorization") access_token:String,
        @Body body: LikeRequest
    ): Call<LikeReponse>

    @GET("main/search")
    fun search(
        @Header("Authorization") access_token:String,
        @Query("q") keyword:String,
        @Query("s") st: Int,
        @Query("e") ed: Int,
    ): Call<SearchResponse>

    @PATCH("main/bookmark")
    fun bookmark(
        @Header("Authorization") access_token:String,
        @Body body: BookmarkRequest
    ): Call<BookmarkResponse>

    @POST("main/report")
    fun report(
        @Header("Authorization") access_token:String,
        @Body body: ReportRequest
    ): Call<ReportResponse>

    @GET("main/link/{news_id}")
    fun source(
        @Header("Authorization") access_token:String,
        @Path("news_id") news_id:String
    ): Call<SourceResponse>
    
    @GET("/sub")
    fun sseSubscribe(@Header("Authorization") authorization:String): Void

    @GET("main/keyword/{date}")
    fun keyword(
        @Header("Authorization") access_token:String,
        @Path("date") date:String
    ): Call<KeywordResponse>

}


