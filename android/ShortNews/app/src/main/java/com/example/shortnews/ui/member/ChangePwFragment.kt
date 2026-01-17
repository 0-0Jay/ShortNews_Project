package com.example.shortnews.ui.member

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.shortnews.R
import com.example.shortnews.databinding.FragmentChangePwBinding
import com.example.shortnews.setOnSingleClickListener

class ChangePwFragment : Fragment() {

        private var _binding: FragmentChangePwBinding?=null
        private val binding get()= _binding!!
        private val viewModel: FindPwViewModel by viewModels()


        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            _binding = FragmentChangePwBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            binding.changePwBtn.setOnSingleClickListener {
                val pw = binding.changePw.text.toString()
                val pwCheck = binding.changePwCheck.text.toString()
                val id = arguments?.getString("id").toString()

                if (pw.length < 6 || pw.length > 30) {
                    binding.changePwAlert.text = "6자리 이상 30자리 이하로 입력해 주세요."
                    binding.changePwAlert.setTextColor(Color.RED)
                    binding.changePwAlert.visibility = View.VISIBLE
                    binding.changePwBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        topToBottom = R.id.changePwAlert
                    }
                } else if (pw == pwCheck) {
                    viewModel.updatePw(id, pw)

                    viewModel.updatePwResponse.observe(viewLifecycleOwner) { updatePwResponse ->
                        if (updatePwResponse.status == "OK") {
                            findNavController().navigate(R.id.action_changePwFragment_to_loginFragment)
                        }
                    }
                } else {
                    binding.changePwAlert.visibility = View.VISIBLE
                    binding.changePwBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        topToBottom = R.id.changePwAlert
                    }
                }
            }

        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

}