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
import androidx.navigation.fragment.findNavController
import com.example.shortnews.R
import com.example.shortnews.databinding.FragmentFindPwBinding
import com.example.shortnews.setOnSingleClickListener

class FindPwFragment : Fragment() {

    private var _binding: FragmentFindPwBinding?=null
    private val binding get()= _binding!!
    private val viewModel: FindPwViewModel by viewModels()

    val timer = object : CountDownTimer(180000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val seconds = millisUntilFinished / 1000
            binding.findPwCodeTimer.text = "${seconds / 60}:${String.format("%02d", seconds % 60)} 내에 인증해 주세요."
        }

        override fun onFinish() {
            binding.findPwCodeTimer.text = "시간 종료"
            // 타이머가 종료될 때 필요한 작업을 수행
            binding.resend.isEnabled = true
            binding.resend.alpha = 1f
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFindPwBinding.inflate(inflater, container, false)
        return binding.root
    }

    var code_flag = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 코드 전송 버튼 클릭
        fun sendCode() {
            val id = binding.findPwId.text.toString()
            val email = binding.findPwEmail.text.toString()
            val id_pattern = Regex("^[A-Za-z0-9]+$")
            val phone_pattern = Regex("""^\d{2,3}-?\d{3,4}-?\d{4}$""")

            if (id == "") {
                binding.findPwEmailAlert.text = "전화번호를 입력해 주세요."
                binding.findPwEmailAlert.setTextColor(Color.RED)
                binding.findPwEmailAlert.visibility = View.VISIBLE
                binding.findPwBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = R.id.findPwEmailAlert
                }
            } else if (id.length < 2 || id.length > 20) {
                binding.findPwEmailAlert.text = "2자리 이상 20자리 이하로 입력해 주세요."
                binding.findPwEmailAlert.setTextColor(Color.RED)
                binding.findPwEmailAlert.visibility = View.VISIBLE
                binding.findPwBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = R.id.findPwEmailAlert
                }
            }else if (!id_pattern.matches(id)) {
                binding.findPwEmailAlert.text = "영문자 및 숫자만 입력해 주세요."
                binding.findPwEmailAlert.setTextColor(Color.RED)
                binding.findPwEmailAlert.visibility = View.VISIBLE
                binding.findPwBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = R.id.findPwEmailAlert
                }
            } else if (!phone_pattern.matches(email)) {
                binding.findPwEmailAlert.text = "전화번호 양식을 지켜주세요."
                binding.findPwEmailAlert.setTextColor(Color.RED)
                binding.findPwEmailAlert.visibility = View.VISIBLE
                binding.findPwBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = R.id.findPwEmailAlert
                }
            } else {
                viewModel.findPw(id, email)

                viewModel.findPwResponse.observe(viewLifecycleOwner) { findPwResponse ->
                    if (findPwResponse.flag) {
                        binding.findPwEmailAlert.text = "입력하신 전화번호로 인증 코드가 전송되었습니다."
                        binding.findPwEmailAlert.setTextColor(Color.BLACK)
                        binding.findPwSendCodeBtn.isClickable = false
                        binding.findPwCode.visibility = View.VISIBLE
                        binding.findPwCodeBtn.visibility = View.VISIBLE
                        binding.findPwCodeTimer.visibility = View.VISIBLE
                        binding.findPwEmailAlert.visibility = View.VISIBLE
                        binding.resend.visibility = View.VISIBLE

                        binding.findPwBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            topToBottom = R.id.findPwEmailAlert
                        }
                        binding.findPwBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            topToBottom = R.id.findPwCodeTimer
                        }

                        timer.start()

                    } else {
                        binding.findPwEmailAlert.text = "존재하지 않는 회원 정보입니다."
                        binding.findPwEmailAlert.setTextColor(Color.RED)
                        binding.findPwEmailAlert.visibility = View.VISIBLE
                        binding.findPwBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            topToBottom = R.id.findPwEmailAlert
                        }
                    }

                }
            }
        } // sendCode

        fun checkCode() {

            viewModel.findPwResponse.observe(viewLifecycleOwner) { findPwResponse ->
                val code = findPwResponse.code

                if (binding.findPwCode.text.isEmpty()) {
                    binding.findPwCodeAlert.text = "인증번호를 입력해주세요."
                    binding.findPwCodeAlert.setTextColor(Color.RED)

                    code_flag = false
                } else if (binding.findPwCode.text.toString() == code) {
                    binding.findPwCodeAlert.text = "인증이 완료되었습니다."
                    binding.findPwCodeAlert.setTextColor(Color.BLUE)
                    binding.findPwCodeTimer.visibility = View.INVISIBLE
                    binding.findPwBtn.isEnabled = true
                    binding.findPwBtn.alpha = 1f

                    binding.resend.isEnabled = false
                    binding.resend.alpha = 0.5f

                    binding.findPwCodeAlert.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        topToBottom = R.id.findPwCode
                    }

                    code_flag = true
                } else {
                    binding.findPwCodeAlert.text = "인증번호가 틀렸습니다."
                    binding.findPwCodeAlert.setTextColor(Color.RED)

                    code_flag = false
                }

                binding.findPwCodeAlert.visibility = View.VISIBLE

                binding.findPwBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = R.id.findPwCodeAlert
                }
            }

        }

        binding.findPwSendCodeBtn.setOnSingleClickListener {
            sendCode()
        }

        binding.findPwCodeBtn.setOnSingleClickListener {
            checkCode()
        }

        binding.resend.setOnClickListener {
            sendCode()
            binding.resend.isEnabled = false
            binding.resend.alpha = 0.5f
        }

        // 비밀번호 찾기 버튼
        binding.findPwBtn.setOnSingleClickListener {
            if (code_flag) {
                val bundle = Bundle()
                bundle.putString("id", binding.findPwId.text.toString())

                findNavController().navigate(R.id.action_findPwFragment_to_changePwFragment, bundle)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer.cancel()
        _binding = null
    }

}