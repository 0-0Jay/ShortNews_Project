package com.example.shortnews.ui.news

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shortnews.api.RetrofitClient
import com.example.shortnews.model.NewsReply
import com.example.shortnews.model.NewsReplyDeleteRequest
import com.example.shortnews.model.NewsReplyRequest
import com.example.shortnews.model.NewsReplyResponse
import com.example.shortnews.model.NewsReplyUpdateRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class CommentViewModel : ViewModel() {

    val replyList = MutableLiveData<List<NewsReply>?>()

    fun getNewsReply(access_token: String, news_id: String, status: Int) {
        Log.d("댓글 요청 아이디", news_id)
        RetrofitClient.newsApi.getNewsReply(access_token, news_id).enqueue(object :
            Callback<NewsReplyResponse> {
            override fun onResponse(call: Call<NewsReplyResponse>, response: Response<NewsReplyResponse>) {
                if(response.isSuccessful) {
                    Log.d("댓글 요청 성공", response.toString())
                    val sortedReplies = response.body()?.replies?.let { replies ->
                        when { // 댓글 정렬
                            status == 1 -> replies.sortedByDescending { it.reply_id.toLongOrNull() ?: 0 }
                            status == -1 -> replies.sortedWith(compareByDescending<NewsReply> { it.like }
                                .thenByDescending { it.reply_id.toLongOrNull() ?: 0 })
                            else -> replies
                        }
                    }
                    replyList.value = sortedReplies
                    Log.d("댓글 값", replyList.value.toString())
                } else {
                    Log.d("댓글 요청 실패1" , response.toString())
                }


            }

            override fun onFailure(call: Call<NewsReplyResponse>, t: Throwable) {
                Log.e("댓글 요청 실패2", t.toString())
            }
        })
    }

    fun createReply(access_token: String, status: Int, news_id: String, reply:String, upper_id:String?, upper_user:String?) {
        val replyRequest = NewsReplyRequest(news_id, reply, upper_id, upper_user)
        RetrofitClient.newsApi.createReply(access_token, replyRequest).enqueue(object :
        Callback<NewsReplyResponse> {
            override fun onResponse(
                call: Call<NewsReplyResponse>,
                response: Response<NewsReplyResponse>
            ) {
                if(response.isSuccessful) {
                    Log.d("댓글 등록 요청 성공", response.toString())
                    val sortedReplies = response.body()?.replies?.let { replies ->
                        when { // 댓글 정렬
                            status == 1 -> replies.sortedByDescending { it.reply_id.toLongOrNull() ?: 0 }
                            status == -1 -> replies.sortedWith(compareByDescending<NewsReply> { it.like }
                                .thenByDescending { it.reply_id.toLongOrNull() ?: 0 })
                            else -> replies
                        }
                    }
                    replyList.value = sortedReplies
                } else {
                    Log.d("댓글 등록 요청 실패1", response.toString())
                }
            }

            override fun onFailure(call: Call<NewsReplyResponse>, t: Throwable) {
                Log.e("댓글 등록 요청 실패2", t.toString())
            }

        })
    }

    fun deleteReply(access_token: String, status: Int, news_id: String, reply_id:String) {
        val replyRequest = NewsReplyDeleteRequest(news_id, reply_id)
        RetrofitClient.newsApi.deleteReply(access_token, replyRequest).enqueue(object :
        Callback<NewsReplyResponse> {
            override fun onResponse(
                call: Call<NewsReplyResponse>,
                response: Response<NewsReplyResponse>
            ) {
                if(response.isSuccessful) {
                    Log.d("댓글 삭제 요청 성공", response.toString())
                    val sortedReplies = response.body()?.replies?.let { replies ->
                        when { // 댓글 정렬
                            status == 1 -> replies.sortedByDescending { it.reply_id.toLongOrNull() ?: 0 }
                            status == -1 -> replies.sortedWith(compareByDescending<NewsReply> { it.like }
                                .thenByDescending { it.reply_id.toLongOrNull() ?: 0 })
                            else -> replies
                        }
                    }
                    replyList.value = sortedReplies
                } else {
                    Log.d("댓글 삭제 요청 실패1", response.toString())
                }
            }

            override fun onFailure(call: Call<NewsReplyResponse>, t: Throwable) {
                Log.e("댓글 삭제 요청 실패2", t.toString())
            }

        })
    }

    fun updateReply(access_token: String, status: Int, news_id: String, reply_id: String, text: String) {
        val replyRequest = NewsReplyUpdateRequest(news_id, reply_id, text)
        RetrofitClient.newsApi.updateReply(access_token, replyRequest).enqueue(object :
        Callback<NewsReplyResponse> {
            override fun onResponse(
                call: Call<NewsReplyResponse>,
                response: Response<NewsReplyResponse>
            ) {
                if(response.isSuccessful) {
                    Log.d("댓글 수정 요청 성공", response.toString())
                    val sortedReplies = response.body()?.replies?.let { replies ->
                        when { // 댓글 정렬
                            status == 1 -> replies.sortedByDescending { it.reply_id.toLongOrNull() ?: 0 }
                            status == -1 -> replies.sortedWith(compareByDescending<NewsReply> { it.like }
                                .thenByDescending { it.reply_id.toLongOrNull() ?: 0 })
                            else -> replies
                        }
                    }
                    replyList.value = sortedReplies
                } else {
                    Log.d("댓글 수정 요청 실패1", response.toString())
                }
            }

            override fun onFailure(call: Call<NewsReplyResponse>, t: Throwable) {
                Log.e("댓글 수정 요청 실패2", t.toString())
            }

        })
    }
}