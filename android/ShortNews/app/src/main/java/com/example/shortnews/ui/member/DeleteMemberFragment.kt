package com.example.shortnews.ui.member

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.shortnews.R
import com.example.shortnews.databinding.FragmentDeleteMemberBinding
import com.example.shortnews.setOnSingleClickListener

class DeleteMemberFragment : Fragment() {

        private var _binding: FragmentDeleteMemberBinding?=null
        private val binding get() = _binding!!

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            _binding = FragmentDeleteMemberBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            // 기타 사유
            binding.checkBox5.setOnClickListener {
                if (binding.checkBox5.isChecked) binding.other.visibility = View.VISIBLE
                else binding.other.visibility = View.GONE
            }

            // 회원 탈퇴 버튼 클릭
            binding.deleteBtn.setOnSingleClickListener {
                var reason = ""
                if (binding.checkBox.isChecked) reason += binding.checkBox.text.toString() + ","
                if (binding.checkBox2.isChecked) reason += binding.checkBox2.text.toString() + ","
                if (binding.checkBox3.isChecked) reason += binding.checkBox3.text.toString() + ","
                if (binding.checkBox4.isChecked) reason += binding.checkBox4.text.toString() + ","
                if (binding.checkBox5.isChecked && binding.other.text.toString() != "") reason += binding.other.text.toString() + ","
                if (reason != "") reason = reason.substring(0, reason.length-1)
                Log.d("탈퇴 이유 응답", reason)

                val dialog = DeleteMemberDialogFragment(reason)
                dialog.show(requireActivity().supportFragmentManager, "DeleteMemberDialogFragment")
            }

            binding.noBtn.setOnSingleClickListener {
                parentFragmentManager.popBackStack()
            }

        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

}