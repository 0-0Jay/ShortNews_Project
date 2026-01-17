package com.example.shortnews.ui.member

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.shortnews.R
import com.example.shortnews.UserSharedPreferences
import com.example.shortnews.databinding.FragmentNicknameDialogBinding
import com.example.shortnews.setOnSingleClickListener

class NicknameDialogFragment : DialogFragment() {

        private var _binding: FragmentNicknameDialogBinding?=null
        private val binding get() = _binding!!
        private val viewModel: MyPageViewModel by viewModels()
        var onNicknameChanged: ((String) -> Unit)? = null
        private val access_token = UserSharedPreferences.getAccessToken()

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            _binding = FragmentNicknameDialogBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            fun updateNickname() {
                val pattern = Regex("^[a-zA-Z가-힣0-9]{2,}$")

                val nickname = binding.changeNicknameText.text.toString()

                if (nickname == "" || nickname.length < 2) {
                    binding.nicknameAlert.text = "2자리 이상 입력해 주세요."
                    binding.nicknameAlert.setTextColor(Color.RED)
                }
                else if (!pattern.matches(nickname)) {
                    binding.nicknameAlert.text = "영어, 한글, 숫자만 입력해 주세요."
                    binding.nicknameAlert.setTextColor(Color.RED)
                } else {
                    // 요청
                    viewModel.updateNickname(access_token, nickname)

                    viewModel.nicknameResponse.observe(viewLifecycleOwner, Observer { nicknameResponse ->

                        if (nicknameResponse.status != "OK") {
                            binding.nicknameAlert.text = "중복된 닉네임입니다."
                            binding.nicknameAlert.setTextColor(Color.RED)
                        } else {
                            binding.nicknameAlert.text = "변경 완료"
                            binding.nicknameAlert.setTextColor(Color.BLUE)

                            onNicknameChanged?.invoke(nickname)
//                            dismiss()
                        }

                    })
                }
                binding.nicknameAlert.visibility = View.VISIBLE
                binding.changeNicknameBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = R.id.nicknameAlert
                }
            }

            // 닉네임 변경 버튼 클릭
            binding.changeNicknameBtn.setOnSingleClickListener {
                updateNickname()
            }

            binding.close.setOnSingleClickListener {
                dismiss()
            }


        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

}