package com.example.shortnews.ui.member

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
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
import com.example.shortnews.databinding.FragmentPwDialogBinding
import com.example.shortnews.model.RecommendItem
import com.example.shortnews.setOnSingleClickListener

class PwDialogFragment : DialogFragment() {

    private var _binding: FragmentPwDialogBinding?=null
    private val binding get() = _binding!!
    private val viewModel: MyPageViewModel by viewModels()
    private val access_token = UserSharedPreferences.getAccessToken()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPwDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.changePwBtn.setOnSingleClickListener {
            val pw = binding.changePwText.text.toString()
            val newPw = binding.newPwText.text.toString()
            val newPwCheck = binding.newPwCheckText.text.toString()

            if (pw.isEmpty()) {
                binding.pwAlert.text = "기존 비밀번호를 입력해주세요."
                binding.pwAlert.setTextColor(Color.RED)
                binding.pwAlert.visibility = View.VISIBLE
                binding.changePwBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = R.id.pwAlert
                }
            } else if (newPw.length < 6 || newPw.length > 30) {
                binding.pwAlert.text = "6자리 이상 30자리 이하로 입력해 주세요."
                binding.pwAlert.setTextColor(Color.RED)
                binding.pwAlert.visibility = View.VISIBLE
                binding.changePwBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = R.id.pwAlert
                }
            } else if (newPw != newPwCheck) {
                binding.pwAlert.text = "새 비밀번호와 일치하지 않습니다."
                binding.pwAlert.setTextColor(Color.RED)
                binding.pwAlert.visibility = View.VISIBLE
                binding.changePwBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = R.id.pwAlert
                }
            } else {

                viewModel.updatePw(access_token, pw, newPw)

                viewModel.pwResponse.observe(viewLifecycleOwner, Observer { pwResponse ->
                    if (pwResponse.status == "OK") {
                        binding.pwAlert.text = "변경 완료"
                        binding.pwAlert.setTextColor(Color.BLUE)
                    } else {
                        binding.pwAlert.text = "기존 비밀번호가 일치하지 않습니다."
                        binding.pwAlert.setTextColor(Color.RED)
                    }
                    binding.pwAlert.visibility = View.VISIBLE
                    binding.changePwBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        topToBottom = R.id.pwAlert
                    }
                })
            }
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