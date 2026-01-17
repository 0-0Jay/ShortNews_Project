package com.example.shortnews.ui.news

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.example.shortnews.R
import com.example.shortnews.UserSharedPreferences
import com.example.shortnews.databinding.FragmentAlarmDialogBinding
import com.example.shortnews.databinding.FragmentNavBarBinding
import com.example.shortnews.model.AlarmChild
import com.example.shortnews.ui.adapter.AlarmAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlarmDialogFragment(private val navBinding : FragmentNavBarBinding): DialogFragment() {

    private var _binding: FragmentAlarmDialogBinding?=null
    private val binding get() = _binding!!
    private val adapter = AlarmAdapter()
    private val viewModel: AlarmDialogViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            _binding = FragmentAlarmDialogBinding.inflate(inflater, container, false)

            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            binding.alarmswitch.isChecked = UserSharedPreferences.sharedPreferences.getString("alarm", null).toString() == "1"

            val manager = LinearLayoutManager(context)
            binding.RecyclerView.layoutManager = manager
            binding.RecyclerView.adapter = adapter
            
            val token = UserSharedPreferences.sharedPreferences.getString("access_token", null).toString()
            viewModel.getAlarm(token)
            viewModel.alarm.observe(viewLifecycleOwner, Observer { alarm ->
                if (alarm.isNotEmpty()) {
                    binding.alarmIsNullLayout.visibility = View.INVISIBLE
                    binding.alarmisnullText.visibility = View.INVISIBLE
                    alarm.map { alarmChild ->
                        // 9시간 더함, 시간 양식 바꿈
                        changeTimeFormat(alarmChild)
                    }
                }else {
                    binding.alarmdrop.visibility = View.INVISIBLE
                    binding.alarmIsNullLayout.visibility = View.VISIBLE
                    binding.alarmisnullText.visibility = View.VISIBLE
                }
                adapter.setData(alarm)
            })

            binding.alarmswitch.setOnClickListener {
                val flag = UserSharedPreferences.sharedPreferences.getString("alarm", null).toString() == "1"

                val editor = UserSharedPreferences.sharedPreferences.edit()
                editor.remove("alarm")
                editor.putString("alarm", if (flag) "0" else "1")
                viewModel.switchAlarm(token, if (flag) 1 else 0)
                editor.apply()
            }

            binding.alarmdrop.setOnClickListener {
                viewModel.dropAlarm(token)
                adapter.setDrop()
                binding.alarmisnullText.visibility = View.VISIBLE
                binding.alarmIsNullLayout.visibility = View.VISIBLE
                navBinding.notificationfragment.notification.visibility = View.INVISIBLE
                
            }


            // 알림 x 클릭
            adapter.setAlarmDeleteListener(object : AlarmAdapter.OnAlarmDeleteListener {
                override fun onAlarmDelete(position: Int) {
                    val target = adapter.getItem(position);
                    Log.d("삭제", position.toString())
                    viewModel.deleteAlarm(token, target, navBinding)
                }
            })


            // 뉴스 클릭
            adapter.setListener {v, position ->
                val data = adapter.getItem(position)
                viewModel.selectAlarm(token,  data.time, data.link)
                val bundle = Bundle().apply {
                    putString("news_id", data.link)
                }
                dismiss()
                try {
                    findNavController().navigate(R.id.action_newsMainFragment_to_newsContentFragment, bundle)
                }
                catch(e: IllegalArgumentException) {
                    findNavController().navigate(R.id.action_newsContentFragment_self, bundle)
                }
            }


        }

    override fun onResume() {
        super.onResume()
    }
    override fun onPause() {
        super.onPause()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun changeTimeFormat(alarmChild : AlarmChild,)  {
        var time = alarmChild.time
        Log.d("시간 바꿈", time)
        if (time.lastIndexOf("+") == -1) return
        time = time.substring(0, time.lastIndexOf("+"))
        time = time.replace("T", " ")
        time = time.replace("-", "/")
        // 현재 날짜 포맷
        val currentFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.getDefault())

        // 문자열을 Date 객체로 파싱
        val parsedDate = currentFormat.parse(time)
        val updatedDate = Date(parsedDate.time + 1000 * 60 * 60 * 9)

        // 변환된 시간을 갖는 새로운 문자열로 포맷
        val updatedFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.getDefault())
        val updatedTimeString = updatedFormat.format(updatedDate)

        Log.d("시간", updatedTimeString)
        alarmChild.time = updatedTimeString
    }
}