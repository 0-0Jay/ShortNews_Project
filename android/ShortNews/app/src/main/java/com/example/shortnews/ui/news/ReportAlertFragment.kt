package com.example.shortnews.ui.news

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.shortnews.R
import com.example.shortnews.databinding.FragmentReportAlertBinding
import com.example.shortnews.setOnSingleClickListener

class ReportAlertFragment : DialogFragment() {
    private var _binding: FragmentReportAlertBinding?=null
    private val binding get() = _binding!!

    companion object {
        private var instance: ReportAlertFragment? = null

        fun getInstanceFragment(): ReportAlertFragment {
            if (instance == null) {
                instance = ReportAlertFragment()
            }
            return instance!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportAlertBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnSingleClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}