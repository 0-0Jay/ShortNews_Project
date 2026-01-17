package com.example.shortnews.ui.member

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.shortnews.R
import com.example.shortnews.UserSharedPreferences
import com.example.shortnews.databinding.FragmentDeleteMemberDialogBinding
import com.example.shortnews.setOnSingleClickListener
import com.example.shortnews.ui.news.NewsMainViewModel

class DeleteMemberDialogFragment(reason:String) : DialogFragment() {

        private var _binding: FragmentDeleteMemberDialogBinding?=null
        private val binding get() = _binding!!
        private val reason = reason
        private val viewModel: MyPageViewModel by viewModels()
        private val access_token = UserSharedPreferences.getAccessToken()

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            _binding = FragmentDeleteMemberDialogBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            binding.yes.setOnSingleClickListener {
                viewModel.deleteMember(access_token, reason)
                viewModel.deleteMemberResponse.observe(viewLifecycleOwner, Observer { deleteMemberResponse ->
                    if (deleteMemberResponse.status == "OK") {
                        val editor = UserSharedPreferences.sharedPreferences.edit()
                        editor.clear().apply()
                        findNavController().navigate(R.id.action_deleteMemberFragment_to_loginFragment)
                        dismiss()
                    }
                })
            }

            binding.no.setOnSingleClickListener {
                dismiss()
            }

        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

}