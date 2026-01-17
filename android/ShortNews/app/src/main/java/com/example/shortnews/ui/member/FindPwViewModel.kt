package com.example.shortnews.ui.member

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shortnews.api.RetrofitClient
import com.example.shortnews.model.FindPwRequest
import com.example.shortnews.model.FindPwResponse
import com.example.shortnews.model.UpdatePwRequest
import com.example.shortnews.model.UpdatePwResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindPwViewModel : ViewModel() {

    val findPwResponse = MutableLiveData<FindPwResponse>()
    val updatePwResponse = MutableLiveData<UpdatePwResponse>()

    fun findPw(id:String, email:String) {
        val findPwRequest = FindPwRequest(id, email)

        RetrofitClient.loginApi.findPw(findPwRequest).enqueue(object :
            Callback<FindPwResponse> {
            override fun onResponse(call: Call<FindPwResponse>, response: Response<FindPwResponse>) {
                if (response.isSuccessful) {
                    findPwResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("비밀번호 찾기 응답", findPwResponse.value.toString())
                } else {
                    Log.d("요청 실패", response.toString())
                }
            }

            override fun onFailure(call: Call<FindPwResponse>, t: Throwable) {
                Log.d("요청 실패", t.localizedMessage)
            }
        })
    }

    fun updatePw(id:String, pw:String) {
        val updatePwRequest = UpdatePwRequest(id, pw)

        RetrofitClient.loginApi.updatePw(updatePwRequest).enqueue(object :
            Callback<UpdatePwResponse> {
            override fun onResponse(call: Call<UpdatePwResponse>, response: Response<UpdatePwResponse>) {
                if (response.isSuccessful) {
                    updatePwResponse.value = response.body()
                    Log.d("요청 성공", response.toString())
                    Log.d("비밀번호 변경 응답", updatePwResponse.value.toString())
                } else {
                    Log.d("요청 실패", response.toString())
                }
            }

            override fun onFailure(call: Call<UpdatePwResponse>, t: Throwable) {
                Log.d("요청 실패", t.localizedMessage)
            }
        })
    }

}