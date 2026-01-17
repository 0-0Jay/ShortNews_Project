package com.example.shortnews.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.example.shortnews.databinding.FragmentCalendarBinding


class CalendarFragment: DialogFragment() {

        private var _binding: FragmentCalendarBinding?=null
        private val binding get() = _binding!!
        var viewModel: CalendarViewModel? = null

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            _binding = FragmentCalendarBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            binding.calendarView.setTitleFormatter { day ->
                val inputText = day.date
                val calendarHeaderElements = inputText.toString().split("-")
                val calendarHeaderBuilder = StringBuilder()

                calendarHeaderBuilder.append(calendarHeaderElements[0]).append("년 ")
                    .append(calendarHeaderElements[1]).append("월")

                calendarHeaderBuilder.toString()
            }


            binding.selectDate.setOnClickListener {
                val selectedDate = binding.calendarView.selectedDate
                if (selectedDate != null) {
                    val year = selectedDate.year.toString()
                    val month = selectedDate.month.toString()
                    val day = selectedDate.day.toString()

                    viewModel?.selectedDate?.value = "${year}년 ${month}월 ${day}일"
                }
                dismiss()
            }

        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

}