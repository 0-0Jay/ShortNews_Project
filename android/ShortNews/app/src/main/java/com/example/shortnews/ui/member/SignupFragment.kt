package com.example.shortnews.ui.member

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import com.example.shortnews.R
import com.example.shortnews.databinding.FragmentSignupBinding
import com.example.shortnews.setOnSingleClickListener

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding?=null
    private val binding get()= _binding!!
    private val viewModel: SignupViewModel by viewModels()
    private var flags = MutableList(5) { false }
    private var info = MutableList(4) { "" }

    val timer = object : CountDownTimer(180000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val seconds = millisUntilFinished / 1000
            binding.codeTimer.text = "${seconds / 60}:${String.format("%02d", seconds % 60)} 내에 인증해 주세요."
        }

        override fun onFinish() {
            binding.codeTimer.text = "시간 종료"
            // 타이머가 종료될 때 필요한 작업을 수행
            binding.resend.isEnabled = true
            binding.resend.alpha = 1f
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 아이디 중복 체크
        fun idCheck() {
            val id = binding.signupId.text.toString()
            val pattern = Regex("^[A-Za-z0-9]+$")

            flags[0] = false

            // 유효성 검사
            if (id == "") {
                binding.idAlert.text = "아이디를 입력해 주세요."
                binding.idAlert.setTextColor(Color.RED)
            } else if (id.length < 2 || id.length > 20) {
                binding.idAlert.text = "2자리 이상 20자리 이하로 입력해 주세요."
                binding.idAlert.setTextColor(Color.RED)
            } else if (!pattern.matches(id)) {
                binding.idAlert.text = "영문자 및 숫자만 입력해 주세요."
                binding.idAlert.setTextColor(Color.RED)
            } else {
                // 서버에 요청
                viewModel.idCheck(id)

                viewModel.idCheckResponse.observe(viewLifecycleOwner) { idCheckResponse ->
                    if (idCheckResponse.flag) {
                        binding.idAlert.text = "사용 가능한 아이디입니다."
                        binding.idAlert.setTextColor(Color.BLUE)
                        flags[0] = true
                        info[0] = id
                    } else {
                        binding.idAlert.text = "이미 사용 중인 아이디입니다."
                        binding.idAlert.setTextColor(Color.RED)
                    }
                }
            }

            binding.idAlert.visibility = View.VISIBLE
            binding.signupPw.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = R.id.idAlert
            }

        } // idCheck

        // 비밀번호 체크
        fun pwCheck() {
            val pw = binding.signupPw.text.toString()
            val pw2 = binding.signupPwCheck.text.toString()
            flags[1] = false

            if (pw.length < 6 || pw.length > 30) {
                binding.pwAlert.text = "6자리 이상 30자리 이하로 입력해 주세요."
                binding.pwAlert.setTextColor(Color.RED)
            } else if (pw != pw2) {
                binding.pwAlert.text = "비밀번호가 일치하지 않습니다."
                binding.pwAlert.setTextColor(Color.RED)
            } else {
                binding.pwAlert.text = "비밀번호 일치"
                binding.pwAlert.setTextColor(Color.BLUE)
                flags[1] = true
                info[3] = pw
            }

            binding.pwAlert.visibility = View.VISIBLE

            binding.signupPhone.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = R.id.pwAlert
            }

        } // pwCheck

        // 코드 전송
        fun sendCode() {
            var phone = binding.signupPhone.text.toString()
            val pattern = Regex("""^\d{2,3}-?\d{3,4}-?\d{4}$""")

            flags[2] = false

            if (phone == "") {
                binding.phoneAlert.text = "전화번호를 입력해 주세요."
                binding.phoneAlert.setTextColor(Color.RED)
                binding.phoneAlert.visibility = View.VISIBLE
                binding.signupNickname.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = R.id.phoneAlert
                }
            }
            else if (!pattern.matches(phone)) {
                binding.phoneAlert.text = "전화번호를 양식을 지켜주세요."
                binding.phoneAlert.setTextColor(Color.RED)
                binding.phoneAlert.visibility = View.VISIBLE
                binding.signupNickname.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = R.id.phoneAlert
                }
            } else {
                // 서버에 요청
                phone = phone.replace("-", "")
                viewModel.phoneCheck(phone)

                // 이메일 중복 체크
                viewModel.phoneCheckResponse.observe(viewLifecycleOwner) { phoneCheckResponse ->
                    if (!phoneCheckResponse.flag) {
                        binding.phoneAlert.text = "이미 가입된 아이디가 있습니다."
                        binding.phoneAlert.setTextColor(Color.RED)
                        binding.phoneAlert.visibility = View.VISIBLE
                        binding.signupNickname.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            topToBottom = R.id.phoneAlert
                        }

                    } else {
                        binding.phoneAlert.text = "인증 코드가 전송되었습니다."
                        binding.phoneAlert.setTextColor(Color.BLACK)
                        binding.phoneAlert.visibility = View.VISIBLE
                        binding.sendCodeBtn.isClickable = false
                        binding.code.visibility = View.VISIBLE
                        binding.codeBtn.visibility = View.VISIBLE
                        binding.codeTimer.visibility = View.VISIBLE
                        binding.sendCodeBtn.visibility = View.INVISIBLE
                        binding.resend.visibility = View.VISIBLE

                        binding.signupNickname.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            topToBottom = R.id.codeTimer
                        }

                        flags[2] = true
                        info[1] = phone

                        // 타이머 시작
                        timer.start()
                    }
                }

            }

        } // sendCode

        // 인증 코드 체크
        fun checkCode() {
            // 응답받은 코드로 비교

            val myCode = binding.code.text.toString()
            flags[3] = false
            binding.codeAlert.setTextColor(Color.RED)

            if (myCode == "") {
                binding.codeAlert.text = "인증번호를 입력해 주세요."
                binding.codeTimer.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = R.id.codeAlert
                }
            } else {
                viewModel.phoneCheckResponse.observe(viewLifecycleOwner) { phoneCheckResponse ->
                    if (phoneCheckResponse.code == myCode) {
                        binding.codeAlert.text = "인증이 완료되었습니다."
                        binding.codeAlert.setTextColor(Color.BLUE)
                        binding.codeTimer.visibility = View.INVISIBLE

                        binding.resend.isEnabled = false
                        binding.resend.alpha = 0.5f

                        binding.signupNickname.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            topToBottom = R.id.codeAlert
                        }
                        flags[3] = true
                    } else {
                        binding.codeAlert.text = "인증번호가 틀렸습니다."
                        binding.codeTimer.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            topToBottom = R.id.codeAlert
                        }
                    }
                }
            }

            binding.codeAlert.visibility = View.VISIBLE

        } // checkCode

        fun nicknameCheck(): String {
            var nickname = binding.signupNickname.text.toString()

            if (nickname.isEmpty()) {
                nickname = binding.signupId.text.toString()
            }

            return nickname

        } // nicknameCheck

        // 중복 확인 버튼 클릭
        binding.idCheckBtn.setOnSingleClickListener {
            idCheck()
        }

        // 코드 전송 버튼 클릭
        binding.sendCodeBtn.setOnSingleClickListener {
            sendCode()
        }

        // 인증 버튼 클릭
        binding.codeBtn.setOnSingleClickListener {
            checkCode()
        }

        binding.resend.setOnSingleClickListener {
            sendCode()
            binding.resend.isEnabled = false
            binding.resend.alpha = 0.5f
        }

        // 회원 가입 버튼 클릭
        binding.signupBtn.setOnSingleClickListener {
            pwCheck()
            if (!flags[0]) idCheck()
            else if (!flags[2]) {
                binding.phoneAlert.text = "전화번호 입력 후 코드 전송해 주세요."
                binding.phoneAlert.visibility = View.VISIBLE
                binding.signupNickname.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = R.id.phoneAlert
                }
            }
            else if (!flags[3]) {
                binding.codeAlert.text = "인증을 완료해 주세요."
                binding.codeAlert.visibility = View.VISIBLE
                binding.codeTimer.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = R.id.codeAlert
                }
            }

            info[2] = nicknameCheck()

            val pattern = Regex("^[a-zA-Z가-힣0-9]{2,}$")

            if (info[2] == "" || info[2].length < 2) {
                binding.nicknameAlert.text = "2자리 이상 입력해 주세요."
                binding.nicknameAlert.setTextColor(Color.RED)
            } else if (!pattern.matches(info[2])) {
                binding.nicknameAlert.text = "영어, 한글, 숫자만 입력해 주세요."
                binding.nicknameAlert.setTextColor(Color.RED)
            } else {
                // 다이얼로그 호출
                if (flags[0] && flags[1] && flags[2] && flags[3]) {

                    // id, phone, nickname, pw
                    viewModel.submit(info[0], info[1], info[2], info[3])

                    viewModel.submitResponse.observe(viewLifecycleOwner) { submitResponse ->

                        if (!submitResponse.flag) {
                            binding.nicknameAlert.text = "중복된 닉네임입니다."
                            binding.nicknameAlert.setTextColor(Color.RED)
                        } else {
                            binding.nicknameAlert.text = "사용 가능한 닉네임입니다."
                            binding.nicknameAlert.setTextColor(Color.BLUE)

                            val dialog = SignupDialogFragment(info, "I")
                            dialog.show(requireActivity().supportFragmentManager, "SignupDialogFragment")
                        }
                    }
                }
            }

        }

    } // onViewCreated



    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
        _binding = null
    }


}
