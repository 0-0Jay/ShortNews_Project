package com.example.shortnews.ui.member

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.shortnews.R
import com.example.shortnews.databinding.FragmentFindIdSuccessBinding

class FindIdSuccessFragment : Fragment() {

    private var _binding: FragmentFindIdSuccessBinding?=null
    private val binding get()= _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFindIdSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getString("id").toString()
        val phone = arguments?.getString("phone").toString()

        binding.id.text = id
        binding.phone.text = phone + " 님의 아이디는"

        binding.loginBtn.setOnClickListener {
            findNavController().navigate(R.id.action_findIdSuccessFragment_to_loginFragment)
        }

        binding.findPwBtn.setOnClickListener {
            findNavController().navigate(R.id.action_findIdSuccessFragment_to_findPwFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}