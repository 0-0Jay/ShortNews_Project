package com.example.shortnews.ui

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shortnews.R
import com.example.shortnews.UserSharedPreferences
import com.example.shortnews.databinding.FragmentNotificationIconBinding
import com.example.shortnews.ui.news.AlarmDialogViewModel
import com.launchdarkly.eventsource.ConnectStrategy
import com.launchdarkly.eventsource.MessageEvent
import com.launchdarkly.eventsource.background.BackgroundEventHandler
import com.launchdarkly.eventsource.background.BackgroundEventSource
import okhttp3.Headers
import java.net.URL
import java.util.concurrent.TimeUnit

open class NotificationIconFragment : Fragment() {
    private var _binding: FragmentNotificationIconBinding?=null
    private val alarmViewMode : AlarmDialogViewModel by viewModels();
    internal val binding get() = _binding!!
    private val access_token = UserSharedPreferences.getAccessToken()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentNotificationIconBinding.inflate(inflater, container, false)
        alarmViewMode.getAlarm_forCheck(access_token, binding)
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