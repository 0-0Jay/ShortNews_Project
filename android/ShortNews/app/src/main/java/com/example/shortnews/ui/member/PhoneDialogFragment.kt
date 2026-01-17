package com.example.shortnews.ui.member

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.shortnews.R
import com.example.shortnews.UserSharedPreferences
import com.example.shortnews.databinding.FragmentPhoneDialogBinding
import com.example.shortnews.setOnSingleClickListener

class PhoneDialogFragment : DialogFragment() {

        private var _binding: FragmentPhoneDialogBinding?=null
        private val binding get() = _binding!!
        private val viewModel: MyPageViewModel by viewModels()
        var onPhoneChanged: ((String) -> Unit)? = null
        private val access_token = UserSharedPreferences.getAccessToken()

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            _binding = FragmentPhoneDialogBinding.inflate(inflater, container, false)
            return binding.root
        }

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

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            fun sendCode() {
                var phone = binding.changePhoneText.text.toString()
                val pattern = Regex("""^\d{2,3}-?\d{3,4}-?\d{4}$""")

                if (phone == "") {
                    binding.phoneAlert.text = "전화번호를 입력해 주세요."
                    binding.phoneAlert.setTextColor(Color.RED)
                    binding.phoneAlert.visibility = View.VISIBLE
                }
                else if (!pattern.matches(phone)) {
                    binding.phoneAlert.text = "전화번호를 양식을 지켜주세요."
                    binding.phoneAlert.setTextColor(Color.RED)
                    binding.phoneAlert.visibility = View.VISIBLE
                } else {
                    phone = phone.replace("-", "")
                    viewModel.checkPhone(access_token, phone)

                    viewModel.checkPhoneResponse.observe(viewLifecycleOwner) { checkPhoneResponse ->
                        if (checkPhoneResponse.status == "OK") {
                            binding.phoneAlert.text = "인증 코드가 전송되었습니다."
                            binding.phoneAlert.setTextColor(Color.BLACK)
                            binding.phoneAlert.visibility = View.VISIBLE
                            binding.code.visibility = View.VISIBLE
                            binding.changePhoneCodeBtn.visibility = View.VISIBLE
                            binding.codeTimer.visibility = View.VISIBLE
                            binding.resend.visibility = View.VISIBLE

                            timer.start()
                        } else {
                            binding.phoneAlert.text = "중복된 전화번호입니다."
                            binding.phoneAlert.setTextColor(Color.RED)
                            binding.phoneAlert.visibility = View.VISIBLE
                        }
                    }

                }
            }


            binding.sendCodeBtn.setOnSingleClickListener {
                sendCode()
            }


            binding.changePhoneCodeBtn.setOnSingleClickListener {
                val myCode = binding.code.text.toString()

                if (myCode == "") {
                    binding.codeAlert.text = "인증번호를 입력해주세요."
                    binding.codeAlert.setTextColor(Color.RED)
                    binding.codeAlert.visibility = View.VISIBLE
                } else {
                    viewModel.checkPhoneResponse.observe(viewLifecycleOwner) { checkPhoneResponse ->
                        if (checkPhoneResponse.code == myCode) {
                            timer.cancel()
                            binding.codeAlert.text = "인증이 완료되었습니다."
                            binding.codeAlert.setTextColor(Color.BLUE)
                            binding.codeAlert.visibility = View.VISIBLE
                            binding.codeTimer.visibility = View.INVISIBLE

                            binding.resend.isEnabled = false
                            binding.resend.alpha = 0.5f

                            binding.codeAlert.updateLayoutParams<ConstraintLayout.LayoutParams> {
                                topToBottom = R.id.code
                            }
                            binding.commitBtn.visibility = View.VISIBLE

                        } else {
                            binding.codeAlert.text = "인증번호가 틀렸습니다."
                            binding.codeAlert.setTextColor(Color.RED)
                            binding.codeAlert.visibility = View.VISIBLE
                        }
                    }
                }
            }

            binding.commitBtn.setOnSingleClickListener {
                var phone = binding.changePhoneText.text.toString()
                phone = phone.replace("-", "")
                viewModel.updatePhone(access_token, phone)

                viewModel.phoneResponse.observe(viewLifecycleOwner) { phoneResponse ->
                    if (phoneResponse.status == "OK") {
                        binding.codeAlert.text = "변경 완료"
                        binding.codeAlert.setTextColor(Color.BLUE)
                        onPhoneChanged?.invoke(phone)
                    }

                }
            }

            binding.close.setOnSingleClickListener {
                dismiss()
            }

            binding.resend.setOnSingleClickListener {
                sendCode()
                binding.resend.isEnabled = false
                binding.resend.alpha = 0.5f
            }


        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
            timer.cancel()
        }

}