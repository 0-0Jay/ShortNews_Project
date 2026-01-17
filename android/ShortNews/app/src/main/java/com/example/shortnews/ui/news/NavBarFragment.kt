package com.example.shortnews.ui.news

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.shortnews.UserSharedPreferences

import com.example.shortnews.databinding.FragmentNavBarBinding
import com.example.shortnews.ui.member.LoginViewModel
import com.launchdarkly.eventsource.ConnectStrategy
import com.launchdarkly.eventsource.EventSource
import okhttp3.Headers
import java.net.URL
import java.util.concurrent.TimeUnit

class NavBarFragment : Fragment() {

        private var _binding: FragmentNavBarBinding?=null
        private val binding get() = _binding!!
        val loginViewModel: LoginViewModel by viewModels()


        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            _binding = FragmentNavBarBinding.inflate(inflater, container, false)

            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)


        }

        override fun onDestroyView() {
            super.onDestroyView()

            _binding = null
        }

}
