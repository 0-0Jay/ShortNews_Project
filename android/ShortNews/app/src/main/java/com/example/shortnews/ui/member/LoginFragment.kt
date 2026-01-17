package com.example.shortnews.ui.member

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.shortnews.R
import com.example.shortnews.databinding.FragmentLoginBinding
import com.example.shortnews.setOnSingleClickListener
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.Constants
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding?=null
    private val binding get()= _binding!!
    private val viewModel: LoginViewModel by viewModels()
    private var info = MutableList(4) { "" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        _binding= FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun login() {
            val id = binding.loginId.text.toString()
            val pw = binding.loginPw.text.toString()

            if (id == "") {
                binding.loginAlert.text = "아이디를 입력해 주세요."
                binding.loginAlert.visibility = View.VISIBLE
            } else if (pw == "") {
                binding.loginAlert.text = "비밀번호를 입력해 주세요."
                binding.loginAlert.visibility = View.VISIBLE
            } else {
                viewModel.loginCheck(id, pw)
                viewModel.loginResponse.removeObservers(viewLifecycleOwner)

                viewModel.loginResponse.observe(viewLifecycleOwner) { loginResponse ->
                    if (loginResponse.status == "OK") {

                        findNavController().popBackStack(R.id.loginFragment, false)

                        findNavController().navigate(R.id.action_loginFragment_to_newsMainFragment)
                    } else {
                        binding.loginAlert.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.loginBtn.setOnSingleClickListener {
            login()
        }

        binding.signupBtn.setOnSingleClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        binding.findIdBtn.setOnSingleClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_findIdFragment)
        }

        binding.findPwBtn.setOnSingleClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_findPwFragment)
        }

        val naverClientId = resources.getString(R.string.naver_client_id)
        val naverClientSecret =  resources.getString(R.string.naver_client_secret)
        val naverClientName =  resources.getString(R.string.naver_client_name)
        NaverIdLoginSDK.initialize(requireContext(), naverClientId, naverClientSecret , naverClientName)

        binding.naverLoginBtn.setOnSingleClickListener {
            var naverToken : String? = ""

            val profileCallback = object : NidProfileCallback<NidProfileResponse> {

                override fun onSuccess(response: NidProfileResponse) {
                    val userId = "N" + response.profile?.id.toString()
                    val userEmail = response.profile?.email.toString() + "N"
                    Log.d("naver id", userId)
                    Log.d("naver email", userEmail)


                    viewModel.naverLogin(userId, userEmail)
                    // naverLoginResponse.오브젝트이름 하면 map의 해당 오브젝트 값 가져옴
                    viewModel.naverLoginResponse.observe(viewLifecycleOwner) { naverLoginResponse ->
                        val message = naverLoginResponse.message
                        Log.d("naver message", message.toString())
                        Log.d("naver Response", naverLoginResponse.toString())

                        if (message) { // 메시지가 true면 이미 있는 회원
                            findNavController().navigate(R.id.action_loginFragment_to_newsMainFragment)
                        } else {  // false면 새로운 회원
                            Log.d("naver false동작", "111")
                            info[0] = naverLoginResponse.id
                            info[1] = naverLoginResponse.phone
                            info[2] = naverLoginResponse.nickname
                            info[3] = ""
                            val dialog = SignupDialogFragment(info, "N")
                            dialog.show(requireActivity().supportFragmentManager, "SignupDialogFragment")
                        }
                    }
                }
                override fun onFailure(httpStatus: Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                }
                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }
            }

            val naverLoginCallback = object : OAuthLoginCallback {
                override fun onSuccess() {
                    // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                    naverToken = NaverIdLoginSDK.getAccessToken()
//                var naverRefreshToken = NaverIdLoginSDK.getRefreshToken()
//                var naverExpiresAt = NaverIdLoginSDK.getExpiresAt().toString()
//                var naverTokenType = NaverIdLoginSDK.getTokenType()
//                var naverState = NaverIdLoginSDK.getState().toString()

                    //로그인 유저 정보 가져오기
                    NidOAuthLogin().callProfileApi(profileCallback)
                }
                override fun onFailure(httpStatus: Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                }
                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }
            }

            NaverIdLoginSDK.authenticate(requireContext(), naverLoginCallback)

        }

        val kakaoNativeAppKey =  resources.getString(R.string.kakao_native_key)
        KakaoSdk.init(requireContext(), kakaoNativeAppKey)

        binding.kakaoLoginBtn.setOnSingleClickListener{
            // 카카오계정으로 로그인 공통 callback 구성
            // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
            var code = ""
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e("kakao LOGIN", "카카오계정으로 로그인 실패", error)
                } else if (token != null) {
                    Log.i("kakao LOGIN", "카카오계정으로 로그인 성공 ${token.accessToken}")
                    code = token.accessToken
                    Log.d("kakao 동작", code)

                    var userId = "K"
                    var userEmail = ""
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e(Constants.TAG, "사용자 정보 요청 실패 : $error")
                        } else if (user != null) {
                            Log.e(Constants.TAG, "사용자 정보 요청 성공 : $user")
                            userId += user.id.toString()
                            userEmail += user.kakaoAccount?.email
                            Log.d("kakao id", userId)
                            Log.d("kakao email", userEmail)

                            viewModel.kakaoLogin(userId, userEmail)
                            viewModel.kakaoLoginResponse.observe(viewLifecycleOwner) { kakaoLoginResponse ->
                                val message = kakaoLoginResponse.message
                                Log.d("kakao message", message.toString())
                                Log.d("kakao Response", kakaoLoginResponse.toString())
                                if (message) { // 메시지가 true면 이미 있는 회원
                                    Log.d("kakao nav 전", "nav")
                                    findNavController().navigate(R.id.action_loginFragment_to_newsMainFragment)
                                    Log.d("kakao nav 후", "nav")
                                } else {  // false면 새로운 회원
                                    Log.d("kakao false동작", "111")
                                    info[0] = kakaoLoginResponse.id
                                    info[1] = kakaoLoginResponse.phone
                                    info[2] = kakaoLoginResponse.nickname
                                    info[3] = ""
                                    val dialog = SignupDialogFragment(info, "K")
                                    dialog.show(
                                        requireActivity().supportFragmentManager,
                                        "SignupDialogFragment"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
                UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                    if (error != null) {
                        Log.e("kakao LOGIN", "카카오톡으로 로그인 실패", error)

                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
                    } else if (token != null) {
                        Log.i("kakao LOGIN", "카카오톡으로 로그인 성공 ${token.accessToken}")
                        code = token.accessToken
                        Log.d("kakao 동작", code)

                        var userId = "K"
                        var userEmail = ""
                        UserApiClient.instance.me { user, error ->
                            if (error != null) {
                                Log.e(Constants.TAG, "사용자 정보 요청 실패 : $error")
                            } else if (user != null) {
                                Log.e(Constants.TAG, "사용자 정보 요청 성공 : $user")
                                userId += user.id.toString()
                                userEmail += user.kakaoAccount?.email + "K"
                                Log.d("kakao id", userId)
                                Log.d("kakao email", userEmail)
                                viewModel.kakaoLogin(userId, userEmail)
                                viewModel.kakaoLoginResponse.observe(viewLifecycleOwner) { kakaoLoginResponse ->
                                    val message = kakaoLoginResponse.message
                                    Log.d("kakao message", message.toString())
                                    Log.d("kakao Response", kakaoLoginResponse.toString())
                                    if (message) { // 메시지가 true면 이미 있는 회원
                                        Log.d("kakao nav 전", "nav")
                                        findNavController().navigate(R.id.action_loginFragment_to_newsMainFragment)
                                        Log.d("kakao nav 후", "nav")
                                    } else {  // false면 새로운 회원
                                        Log.d("kakao false동작", "111")
                                        info[0] = kakaoLoginResponse.id
                                        info[1] = kakaoLoginResponse.phone
                                        info[2] = kakaoLoginResponse.nickname
                                        info[3] = ""
                                        val dialog = SignupDialogFragment(info, "K")
                                        dialog.show(
                                            requireActivity().supportFragmentManager,
                                            "SignupDialogFragment"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
            }

        }


        }

    override fun onResume() {
        super.onResume()
    } // onResume

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}