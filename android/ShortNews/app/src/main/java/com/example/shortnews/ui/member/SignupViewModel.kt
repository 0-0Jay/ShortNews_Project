package com.example.shortnews.ui.member

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shortnews.UserSharedPreferences
import com.example.shortnews.api.RetrofitClient
import com.example.shortnews.model.IdCheckRequest
import com.example.shortnews.model.IdCheckResponse
import com.example.shortnews.model.PhoneCheckRequest
import com.example.shortnews.model.PhoneCheckResponse
import com.example.shortnews.model.SelectCategoryRequest
import com.example.shortnews.model.SelectCategoryResponse
import com.example.shortnews.model.SubmitRequest
import com.example.shortnews.model.SubmitResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupViewModel : ViewModel() {

    val idCheckResponse = MutableLiveData<IdCheckResponse>()
    val phoneCheckResponse = MutableLiveData<PhoneCheckResponse>()
    val submitResponse = MutableLiveData<SubmitResponse>()
    val selectCategoryResponse = MutableLiveData<SelectCategoryResponse>()

    private fun saveUserInfo(selectCategoryResponse: SelectCategoryResponse) {
        val editor = UserSharedPreferences.sharedPreferences.edit()
        editor.clear()
        editor.putString("access_token", selectCategoryResponse.access_token)
        editor.putString("id", selectCategoryResponse.dto.id)
        editor.putString("profileImage", "https://snewsuserprofile.s3.ap-northeast-2.amazonaws.com/${selectCategoryResponse.dto.id}")
        editor.putString("nickname", selectCategoryResponse.dto.nickname)
        editor.putString("phone", selectCategoryResponse.dto.phone)
        editor.putString("model", selectCategoryResponse.dto.model)
        editor.putString("speed", selectCategoryResponse.dto.speed)

        // 알림 수신 여부 확인
        editor.putString("alarm", "1")

        if (selectCategoryResponse.dto.category.isNotEmpty()) {
            val category = selectCategoryResponse.dto.category.joinToString(",")
            editor.putString("category", category)
        } else {
            val category = ""
            editor.putString("category", category)
        }
        editor.apply()
    }

    fun idCheck(id:String) {
        val idCheckRequest = IdCheckRequest(id)

        RetrofitClient.signupApi.idCheck(idCheckRequest).enqueue(object : Callback<IdCheckResponse> {
            override fun onResponse(call: Call<IdCheckResponse>, response: Response<IdCheckResponse>) {
                if (response.isSuccessful) {
                    idCheckResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("아이디 중복 체크 응답", idCheckResponse.value.toString())
                } else {
                    Log.d("요청 실패", response.body().toString())
                }
            }

            override fun onFailure(call: Call<IdCheckResponse>, t: Throwable) {
                Log.d("요청 실패", t.localizedMessage)
            }
        })
    }

    fun phoneCheck(phone:String) {
        val phoneRequest = PhoneCheckRequest(phone)

        RetrofitClient.signupApi.phoneCheck(phoneRequest).enqueue(object : Callback<PhoneCheckResponse> {
            override fun onResponse(call: Call<PhoneCheckResponse>, response: Response<PhoneCheckResponse>) {
                if (response.isSuccessful) {
                    phoneCheckResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("전화번호 체크 응답", phoneCheckResponse.value.toString())
                } else {
                    Log.d("요청실패 ", response.body().toString())
                }
            }

            override fun onFailure(call: Call<PhoneCheckResponse>, t: Throwable) {
                Log.d("요청 실패 ", t.localizedMessage)
            }
        })
    }

    fun submit(id:String, phone:String, nickname:String, pw:String) {
        val submitRequest = SubmitRequest(id, phone, nickname, pw)

        RetrofitClient.signupApi.submit(submitRequest).enqueue(object : Callback<SubmitResponse> {
            override fun onResponse(call: Call<SubmitResponse>, response: Response<SubmitResponse>) {
                if (response.isSuccessful) {
                    submitResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("회원가입 응답", submitResponse.value.toString())
                } else {
                    Log.d("요청 실패 ", response.body().toString())
                }
            }

            override fun onFailure(call: Call<SubmitResponse>, t: Throwable) {
                Log.d("요청 실패 ", t.localizedMessage)
            }
        })
    }

    fun selectCategory(id:String, phone:String, nickname:String, pw:String, platform:String, cate:MutableList<Boolean>) {
        val selectCategoryRequest = SelectCategoryRequest(id, phone, nickname, pw, platform, cate)

        RetrofitClient.signupApi.selectCategory(selectCategoryRequest).enqueue(object : Callback<SelectCategoryResponse> {
            override fun onResponse(call: Call<SelectCategoryResponse>, response: Response<SelectCategoryResponse>) {
                if (response.isSuccessful) {
                    selectCategoryResponse.value = response.body()

                    Log.d("요청 성공", response.toString())
                    Log.d("카테고리 선택 응답", selectCategoryResponse.value.toString())

                    // sharedPreferences에 값 저장하는 함수 호출
                    if (selectCategoryResponse.value?.status == "OK") {
                        selectCategoryResponse.value?.let { saveUserInfo(it) }
                    }

                } else {
                    Log.d("요청 실패 ", response.body().toString())
                }
            }

            override fun onFailure(call: Call<SelectCategoryResponse>, t: Throwable) {
                Log.d("요청 실패 onFailure ", t.localizedMessage)
            }
        })
    }


}