package com.example.shortnews.ui.member

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shortnews.UserSharedPreferences
import com.example.shortnews.api.RetrofitClient
import com.example.shortnews.databinding.FragmentMyPageBinding
import com.example.shortnews.databinding.FragmentNewsContentBinding
import com.example.shortnews.model.AlarmStatus
import com.example.shortnews.model.Alarms
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
import com.example.shortnews.model.ReplyActivityResponse
import com.example.shortnews.model.TTSRequest
import com.example.shortnews.model.TTSResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageViewModel : ViewModel() {

    val nicknameResponse = MutableLiveData<NicknameResponse>()
    val checkPhoneResponse = MutableLiveData<CheckPhoneResponse>()
    val phoneResponse = MutableLiveData<PhoneResponse>()
    val pwResponse = MutableLiveData<PwResponse>()
    val categoryResponse = MutableLiveData<CategoryResponse>()
    val ttsResponse = MutableLiveData<TTSResponse>()
    val bookmarkNewsResponse = MutableLiveData<BookmarkNewsResponse>()
    val deleteMemberResponse = MutableLiveData<DeleteMemberResponse>()
    val likeActivityResponse = MutableLiveData<LikeActivityResponse>()
    val dislikeActivityResponse = MutableLiveData<DisLikeActivityResponse>()
    val replyActivityResponse = MutableLiveData<ReplyActivityResponse>()

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> get() = _nickname

    private val _phone = MutableLiveData<String>()
    val phone: LiveData<String> get() = _phone

    fun setNickname(newNickname: String) {
        _nickname.value = newNickname

        val editor = UserSharedPreferences.sharedPreferences.edit()
        editor.putString("nickname", newNickname)
        editor.apply()
    }

    fun setPhone(newPhone: String) {
        _phone.value = newPhone

        val editor = UserSharedPreferences.sharedPreferences.edit()
        editor.putString("phone", newPhone)
        editor.apply()
    }
    fun updateNickname(authorization: String, nickname: String) {

        val nicknameRequest = NicknameRequest(nickname)

        RetrofitClient.myPageApi.updateNickname(authorization, nicknameRequest).enqueue(object :
            Callback<NicknameResponse> {
            override fun onResponse(call: Call<NicknameResponse>, response: Response<NicknameResponse>) {
                if (response.isSuccessful) {
                    nicknameResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("닉네임 변경 응답:", response.body().toString())
                } else {
                    Log.d("요청 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<NicknameResponse>, t: Throwable) {
                Log.e("요청 실패", t.toString())
            }
        })
    }

    fun checkPhone(authorization: String, phone: String) {

        val checkPhoneRequest = CheckPhoneRequest(phone)

        RetrofitClient.myPageApi.checkPhone(authorization, checkPhoneRequest).enqueue(object :
            Callback<CheckPhoneResponse> {
            override fun onResponse(call: Call<CheckPhoneResponse>, response: Response<CheckPhoneResponse>) {
                if (response.isSuccessful) {
                    checkPhoneResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("전화번호 인증 코드 응답:", response.body().toString())
                } else {
                    Log.d("요청 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<CheckPhoneResponse>, t: Throwable) {
                Log.e("요청 실패", t.toString())
            }
        })
    }

    fun updatePhone(authorization: String, phone: String) {

        val phoneRequest = PhoneRequest(phone)

        RetrofitClient.myPageApi.updatePhone(authorization, phoneRequest).enqueue(object :
            Callback<PhoneResponse> {
            override fun onResponse(call: Call<PhoneResponse>, response: Response<PhoneResponse>) {
                if (response.isSuccessful) {
                    phoneResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("전화번호 변경 응답:", response.body().toString())
                } else {
                    Log.d("요청 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<PhoneResponse>, t: Throwable) {
                Log.e("요청 실패", t.toString())
            }
        })
    }

    fun updatePw(authorization: String, pw: String, new_pw:String) {

        val pwRequest = PwRequest(pw, new_pw)

        RetrofitClient.myPageApi.updatePw(authorization, pwRequest).enqueue(object :
            Callback<PwResponse> {
            override fun onResponse(call: Call<PwResponse>, response: Response<PwResponse>) {
                if (response.isSuccessful) {
                    pwResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("비밀번호 변경 응답:", response.body().toString())
                } else {
                    Log.d("요청 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<PwResponse>, t: Throwable) {
                Log.e("요청 실패", t.toString())
            }
        })
    }

    fun updateCategory(authorization: String, category:List<Boolean>) {

        val categoryRequest = CategoryRequest(category)

        RetrofitClient.myPageApi.updateCategory(authorization, categoryRequest).enqueue(object :
            Callback<CategoryResponse> {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                if (response.isSuccessful) {
                    categoryResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("카테고리 변경 응답:", response.body().toString())
                } else {
                    Log.d("요청 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                Log.e("요청 실패", t.toString())
            }
        })
    }

    fun updateTTS(authorization: String, model:String, speed:String) {

        val ttsRequest = TTSRequest(model, speed)

        RetrofitClient.myPageApi.updateTTS(authorization, ttsRequest).enqueue(object :
            Callback<TTSResponse> {
            override fun onResponse(call: Call<TTSResponse>, response: Response<TTSResponse>) {
                if (response.isSuccessful) {
                    ttsResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("TTS 변경 응답:", response.body().toString())
                } else {
                    Log.d("요청 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<TTSResponse>, t: Throwable) {
                Log.e("요청 실패", t.toString())
            }
        })
    }

    fun getBookmarkNews(authorization: String) {

        RetrofitClient.myPageApi.getBookmarkNews(authorization).enqueue(object :
            Callback<BookmarkNewsResponse> {
            override fun onResponse(call: Call<BookmarkNewsResponse>, response: Response<BookmarkNewsResponse>) {
                if (response.isSuccessful) {
                    bookmarkNewsResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("북마크 뉴스 응답:", response.body().toString())
                } else {
                    Log.d("요청 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<BookmarkNewsResponse>, t: Throwable) {
                Log.e("요청 실패", t.toString())
            }
        })
    }

    fun deleteMember(authorization:String, reason:String) {

        val deleteMemberRequest = DeleteMemberRequest(reason)

        RetrofitClient.myPageApi.deleteMember(authorization, deleteMemberRequest).enqueue(object :
            Callback<DeleteMemberResponse> {
            override fun onResponse(call: Call<DeleteMemberResponse>, response: Response<DeleteMemberResponse>) {
                if (response.isSuccessful) {
                    deleteMemberResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("회원 탈퇴 응답:", response.body().toString())
                } else {
                    Log.d("요청 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<DeleteMemberResponse>, t: Throwable) {
                Log.e("요청 실패", t.toString())
            }
        })
    }

    fun likeActivity(authorization:String) {

        RetrofitClient.myPageApi.likeActivity(authorization).enqueue(object :
            Callback<LikeActivityResponse> {
            override fun onResponse(call: Call<LikeActivityResponse>, response: Response<LikeActivityResponse>) {
                if (response.isSuccessful) {
                    likeActivityResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("좋아요 활동 응답:", response.body().toString())
                } else {
                    Log.d("요청 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<LikeActivityResponse>, t: Throwable) {
                Log.e("요청 실패", t.toString())
            }
        })
    }

    fun dislikeActivity(authorization:String) {

        RetrofitClient.myPageApi.dislikeActivity(authorization).enqueue(object :
            Callback<DisLikeActivityResponse> {
            override fun onResponse(call: Call<DisLikeActivityResponse>, response: Response<DisLikeActivityResponse>) {
                if (response.isSuccessful) {
                    dislikeActivityResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("싫어요 활동 응답:", response.body().toString())
                } else {
                    Log.d("요청 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<DisLikeActivityResponse>, t: Throwable) {
                Log.e("요청 실패", t.toString())
            }
        })
    }

    fun replyActivity(authorization:String) {

        RetrofitClient.myPageApi.replyActivity(authorization).enqueue(object :
            Callback<ReplyActivityResponse> {
            override fun onResponse(call: Call<ReplyActivityResponse>, response: Response<ReplyActivityResponse>) {
                if (response.isSuccessful) {
                    replyActivityResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("댓글 활동 응답:", response.body().toString())
                } else {
                    Log.d("요청 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<ReplyActivityResponse>, t: Throwable) {
                Log.e("요청 실패", t.toString())
            }
        })
    }
    fun logout(access_token: String) {
        Log.d("호출된 거 맞음?", "ㅇㅇ")

        RetrofitClient.alarmApi.logout(access_token).enqueue(object : Callback<AlarmStatus> {
            override fun onResponse(call: Call<AlarmStatus>, response: Response<AlarmStatus>) {
                if (response.isSuccessful) {
                    Log.d("로그아웃 성공", response.toString())
                } else {
                    Log.d("로그아웃 실패" , response.body().toString())
                }
            }

            override fun onFailure(call: Call<AlarmStatus>, t: Throwable) {
                Log.e("로그아웃 실패", t.toString())
            }
        })
    }

    fun checkAlarm(token:String, binding : FragmentMyPageBinding) {
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