package com.example.shortnews.ui.member

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shortnews.UserSharedPreferences
import com.example.shortnews.api.RetrofitClient
import com.example.shortnews.model.FindIdRequest
import com.example.shortnews.model.FindIdResponse
import com.example.shortnews.model.KakaoLoginRequest
import com.example.shortnews.model.KakaoLoginResponse
import com.example.shortnews.model.LoginRequest
import com.example.shortnews.model.LoginResponse
import com.example.shortnews.model.NaverLoginRequest
import com.example.shortnews.model.NaverLoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {

    var loginResponse = MutableLiveData<LoginResponse>()
    val findIdResponse = MutableLiveData<FindIdResponse>()
    val naverLoginResponse = MutableLiveData<NaverLoginResponse>()
    val kakaoLoginResponse = MutableLiveData<KakaoLoginResponse>()

    private fun saveUserInfo(loginResponse: LoginResponse) {
        val editor = UserSharedPreferences.sharedPreferences.edit()
        editor.clear().apply()
        editor.putString("access_token", loginResponse.access_token)
        editor.putString("id", loginResponse.dto.id)
        editor.putString("profileImage", "https://snewsuserprofile.s3.ap-northeast-2.amazonaws.com/${loginResponse.dto.id}")
        editor.putString("nickname", loginResponse.dto.nickname)
        editor.putString("phone", loginResponse.dto.phone)
        editor.putString("model", loginResponse.dto.model)
        editor.putString("speed", loginResponse.dto.speed)

        // 알림 수신 여부 확인
        editor.putString("alarm", loginResponse.dto.alarm.toString())

        if (loginResponse.dto.category.isNotEmpty()) {
            val category = loginResponse.dto.category.joinToString(",")
            editor.putString("category", category)
        } else {
            val category = ""
            editor.putString("category", category)
        }
        editor.apply()
    }

    private fun saveNaverUserInfo(loginResponse: NaverLoginResponse) {
        val editor = UserSharedPreferences.sharedPreferences.edit()
        editor.clear().apply()
        editor.putString("access_token", loginResponse.access_token)
        editor.putString("id", loginResponse.dto.id)
        editor.putString("profileImage", "https://snewsuserprofile.s3.ap-northeast-2.amazonaws.com/${loginResponse.dto.id}")
        editor.putString("nickname", loginResponse.dto.nickname)
        editor.putString("phone", loginResponse.dto.phone)
        editor.putString("model", loginResponse.dto.model)
        editor.putString("speed", loginResponse.dto.speed)

        // 알림 수신 여부 확인
        editor.putString("alarm", loginResponse.dto.alarm.toString())

        if (loginResponse.dto.category.isNotEmpty()) {
            val category = loginResponse.dto.category.joinToString(",")
            editor.putString("category", category)
        } else {
            val category = ""
            editor.putString("category", category)
        }
        editor.apply()
    }

    private fun saveKakaoUserInfo(loginResponse: KakaoLoginResponse) {
        val editor = UserSharedPreferences.sharedPreferences.edit()
        editor.clear().apply()
        editor.putString("access_token", loginResponse.access_token)
        editor.putString("id", loginResponse.dto.id)
        editor.putString("profileImage", "https://snewsuserprofile.s3.ap-northeast-2.amazonaws.com/${loginResponse.dto.id}")
        editor.putString("nickname", loginResponse.dto.nickname)
        editor.putString("phone", loginResponse.dto.phone)
        editor.putString("model", loginResponse.dto.model)
        editor.putString("speed", loginResponse.dto.speed)

        // 알림 수신 여부 확인
        editor.putString("alarm", loginResponse.dto.alarm.toString())

        if (loginResponse.dto.category.isNotEmpty()) {
            val category = loginResponse.dto.category.joinToString(",")
            editor.putString("category", category)
        } else {
            val category = ""
            editor.putString("category", category)
        }
        editor.apply()
    }

    fun loginCheck(id:String, pw:String) {
        val loginRequest = LoginRequest(id, pw)

        RetrofitClient.loginApi.loginCheck(loginRequest).enqueue(object :
            Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {

                    loginResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("로그인 응답", loginResponse.value.toString())

                    // sharedPreferences에 값 저장하는 함수 호출
                    if (loginResponse.value?.status == "OK") {
                        loginResponse.value?.let { saveUserInfo(it) }
                    }

                } else {
                    Log.d("요청 실패", response.body().toString())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.d("요청 실패", t.localizedMessage)
            }
        })
    }

    fun findId(email:String) {
        val findIdRequest = FindIdRequest(email)

        RetrofitClient.loginApi.findId(findIdRequest).enqueue(object :
            Callback<FindIdResponse> {
            override fun onResponse(call: Call<FindIdResponse>, response: Response<FindIdResponse>) {
                if (response.isSuccessful) {
                    findIdResponse.value = response.body()
                    Log.d("요청성공", response.toString())
                    Log.d("아이디 찾기 응답", findIdResponse.value.toString())
                } else {
                    Log.d("요청 실패", response.body().toString())
                }
            }

            override fun onFailure(call: Call<FindIdResponse>, t: Throwable) {
                Log.d("요청 실패", t.localizedMessage)
            }
        })
    }

    fun naverLogin(id:String, email:String) {
        val naverLoginRequest = NaverLoginRequest(id, email)

        RetrofitClient.loginApi.naverLogin(naverLoginRequest).enqueue(object :
            Callback<NaverLoginResponse> {
            override fun onResponse(call: Call<NaverLoginResponse>, response: Response<NaverLoginResponse>) {
                if (response.isSuccessful) {

                    naverLoginResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("네이버 로그인 응답", naverLoginResponse.value.toString())

                    // sharedPreferences에 값 저장하는 함수 호출
                    if (naverLoginResponse.value?.message == true) {
                        naverLoginResponse.value?.let { saveNaverUserInfo(it) }
                    }

                } else {
                    Log.d("요청 실패", response.body().toString())
                }
            }

            override fun onFailure(call: Call<NaverLoginResponse>, t: Throwable) {
                Log.d("요청 실패", t.localizedMessage)
            }
        })
    }

    fun kakaoLogin(id: String, email:String) {
        val kakaoLoginRequest = KakaoLoginRequest(id, email)
        Log.d("kakao login request", kakaoLoginRequest.toString())
        RetrofitClient.loginApi.kakaoLogin(kakaoLoginRequest).enqueue(object :
            Callback<KakaoLoginResponse> {
            override fun onResponse(call: Call<KakaoLoginResponse>, response: Response<KakaoLoginResponse>) {
                if (response.isSuccessful) {

                    kakaoLoginResponse.value = response.body()
                    Log.d("kakao 요청 성공", response.toString())
                    Log.d("kakao 로그인 응답", kakaoLoginResponse.value.toString())

                    // sharedPreferences에 값 저장하는 함수 호출
                    if (kakaoLoginResponse.value?.message == true) {
                        kakaoLoginResponse.value?.let { saveKakaoUserInfo(it) }
                    }

                } else {
                    Log.d("kakao 요청 실패", response.body().toString())
                }
            }

            override fun onFailure(call: Call<KakaoLoginResponse>, t: Throwable) {
                Log.d("kakao 요청 실패", t.localizedMessage)
            }
        })
    }


}