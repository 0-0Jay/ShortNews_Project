package com.example.shortnews.api

import com.example.shortnews.model.BookmarkNewsItem
import com.example.shortnews.model.BookmarkNewsResponse
import com.example.shortnews.model.CategoryRequest
import com.example.shortnews.model.CategoryResponse
import com.example.shortnews.model.CheckPhoneRequest
import com.example.shortnews.model.CheckPhoneResponse
import com.example.shortnews.model.DeleteMemberRequest
import com.example.shortnews.model.DeleteMemberResponse
import com.example.shortnews.model.DisLikeActivityResponse
import com.example.shortnews.model.LikeActivityResponse
import com.example.shortnews.model.NicknameRequest
import com.example.shortnews.model.NicknameResponse
import com.example.shortnews.model.PhoneRequest
import com.example.shortnews.model.PhoneResponse
import com.example.shortnews.model.PwRequest
import com.example.shortnews.model.PwResponse
import com.example.shortnews.model.RecommendResponse
import com.example.shortnews.model.ReplyActivityResponse
import com.example.shortnews.model.TTSRequest
import com.example.shortnews.model.TTSResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

interface MyPageApi {

    @PATCH("member/updateNickname")
    fun updateNickname(
        @Header("Authorization") access_token:String,
        @Body body: NicknameRequest
    ): Call<NicknameResponse>
    @POST("member/mypage/checkPhone")
    fun checkPhone(
        @Header("Authorization") access_token:String,
        @Body body: CheckPhoneRequest
    ): Call<CheckPhoneResponse>
    @PATCH("member/mypage/updatePhone")
    fun updatePhone(
        @Header("Authorization") access_token:String,
        @Body body: PhoneRequest
    ): Call<PhoneResponse>

    @PATCH("member/mypage/updatePassword")
    fun updatePw(
        @Header("Authorization") access_token:String,
        @Body body: PwRequest
    ): Call<PwResponse>

    @PATCH("member/categoryUpdate")
    fun updateCategory(
        @Header("Authorization") access_token:String,
        @Body body: CategoryRequest
    ): Call<CategoryResponse>

    @PATCH("member/tts")
    fun updateTTS(
        @Header("Authorization") access_token:String,
        @Body body: TTSRequest
    ): Call<TTSResponse>

    @GET("member/bookmark")
    fun getBookmarkNews(
        @Header("Authorization") access_token:String
    ): Call<BookmarkNewsResponse>

    @PATCH("member/delete")
    fun deleteMember(
        @Header("Authorization") access_token:String,
        @Body body: DeleteMemberRequest
    ): Call<DeleteMemberResponse>

    @GET("myActivity/like")
    fun likeActivity(
        @Header("Authorization") access_token:String
    ): Call<LikeActivityResponse>

    @GET("myActivity/dislike")
    fun dislikeActivity(
        @Header("Authorization") access_token:String
    ): Call<DisLikeActivityResponse>

    @GET("myActivity/reply")
    fun replyActivity(
        @Header("Authorization") access_token:String
    ): Call<ReplyActivityResponse>


}