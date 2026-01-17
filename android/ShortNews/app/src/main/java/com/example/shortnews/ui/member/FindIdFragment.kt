package com.example.shortnews.ui.member

import android.content.res.ColorStateList
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
import com.example.shortnews.databinding.FragmentFindIdBinding
import com.example.shortnews.setOnSingleClickListener

class FindIdFragment : Fragment() {

    private var _binding: FragmentFindIdBinding?=null
    private val binding get()= _binding!!
    private val viewModel: LoginViewModel by viewModels()
    val bundle = Bundle()

    val timer = object : CountDownTimer(180000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val seconds = millisUntilFinished / 1000
            binding.findIdCodeTimer.text = "${seconds / 60}:${String.format("%02d", seconds % 60)} 내에 인증해 주세요."
        }

        override fun onFinish() {
            binding.findIdCodeTimer.text = "시간 종료"
            // 타이머가 종료될 때 필요한 작업을 수행
            binding.resend.isEnabled = true
            binding.resend.alpha = 1f
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFindIdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 코드 전송 버튼 클릭
        fun sendCode() {
            val phone = binding.findIdPhone.text.toString()
            val phone_pattern = Regex("""^\d{2,3}-?\d{3,4}-?\d{4}$""")

            if (phone == "") {
                binding.findIdPhoneAlert.text = "전화번호를 입력해 주세요."
                binding.findIdPhoneAlert.setTextColor(Color.RED)
                binding.findIdPhoneAlert.visibility = View.VISIBLE
                binding.findIdBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = R.id.findIdPhoneAlert
                }

            } else if (!phone_pattern.matches(phone)) {
                binding.findIdPhoneAlert.text = "전화번호를 양식을 지켜주세요."
                binding.findIdPhoneAlert.setTextColor(Color.RED)
                binding.findIdPhoneAlert.visibility = View.VISIBLE
                binding.findIdBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = R.id.findIdPhoneAlert
                }
            } else {
                val phone2 = phone.replace("-", "")
                viewModel.findId(phone2)

                viewModel.findIdResponse.observe(viewLifecycleOwner) { findIdResponse ->
                    if (findIdResponse.flag) {
                        binding.findIdPhoneAlert.text = "입력하신 전화번호로 인증 코드가 전송되었습니다."

                        bundle.putString("phone", phone)

                        binding.findIdPhoneAlert.setTextColor(Color.BLACK)
                        binding.findIdSendCodeBtn.isEnabled = false
                        binding.findIdSendCodeBtn.alpha = 0.5f
                        binding.findIdCode.visibility = View.VISIBLE
                        binding.findIdCodeBtn.visibility = View.VISIBLE
                        binding.findIdCodeTimer.visibility = View.VISIBLE
                        binding.findIdPhoneAlert.visibility = View.VISIBLE
                        binding.resend.visibility = View.VISIBLE

                        binding.findIdBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            topToBottom = R.id.findIdCodeTimer
                        }

                        // 타이머 시작
                        timer.start()
                    } else {
                        binding.findIdPhoneAlert.text = "존재하지 않는 회원 정보입니다."
                        binding.findIdPhoneAlert.setTextColor(Color.RED)
                        binding.findIdPhoneAlert.visibility = View.VISIBLE
                        binding.findIdBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            topToBottom = R.id.findIdPhoneAlert
                        }
                    }

                }

            }
        } // sendCode

        fun checkCode() {
            val myCode = binding.findIdCode.text.toString()

            if (myCode == "") {
                binding.findIdCodeAlert.text = "인증번호를 입력해주세요."
                binding.findIdCodeAlert.setTextColor(Color.RED)
            } else {
                viewModel.findIdResponse.observe(viewLifecycleOwner) { findIdResponse ->
                    if (findIdResponse.code == myCode) {
                        timer.cancel()
                        binding.findIdCodeAlert.text = "인증이 완료되었습니다."
                        binding.findIdCodeAlert.setTextColor(Color.BLACK)
                        binding.findIdCodeTimer.visibility = View.INVISIBLE

                        binding.findIdCodeAlert.updateLayoutParams<ConstraintLayout.LayoutParams> {
                            topToBottom = R.id.findIdCode
                        }

                        binding.findIdBtn.isEnabled = true
                        binding.findIdBtn.alpha = 1f

                        binding.resend.isEnabled = false
                        binding.resend.alpha = 0.5f

                    } else {
                        binding.findIdCodeAlert.text = "인증번호가 틀렸습니다."
                        binding.findIdCodeAlert.setTextColor(Color.RED)
                    }
                }

            }
            binding.findIdCodeAlert.visibility = View.VISIBLE

            binding.findIdBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = R.id.findIdCodeAlert
            }

        } // checkCode


        binding.findIdSendCodeBtn.setOnSingleClickListener {
            sendCode()
        }

        binding.findIdCodeBtn.setOnSingleClickListener {
            checkCode()
        }

        binding.resend.setOnSingleClickListener {
            sendCode()
            binding.resend.isEnabled = false
            binding.resend.alpha = 0.5f
        }

        binding.findIdBtn.setOnSingleClickListener {

            viewModel.findIdResponse.observe(viewLifecycleOwner) { findIdResponse ->

                if (findIdResponse.flag) {
                    bundle.putString("id", findIdResponse.id)

                    findNavController().navigate(R.id.action_findIdFragment_to_findIdSuccessFragment, bundle)
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