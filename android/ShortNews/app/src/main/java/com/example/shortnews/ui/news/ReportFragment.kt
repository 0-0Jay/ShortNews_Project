package com.example.shortnews.ui.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.shortnews.UserSharedPreferences
import com.example.shortnews.databinding.FragmentReportBinding
import com.example.shortnews.setOnSingleClickListener

class ReportFragment : DialogFragment() {
    private var _binding: FragmentReportBinding?=null
    private val binding get() = _binding!!
    val reportViewModel: ReportViewModel by viewModels()
    val access_token = UserSharedPreferences.sharedPreferences.getString("access_token", null).toString()

    companion object {
        fun create(news_id: String, reply_id: String): ReportFragment {
            val fragment = ReportFragment()
            val args = Bundle()
            args.putString("news_id", news_id)
            args.putString("reply_id", reply_id)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportBinding.inflate(inflater, container, false)

        binding.reportCancleButton.setOnSingleClickListener {
            dialog?.dismiss()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.reportCheck6.setOnSingleClickListener {
            if(binding.reportCheck6.isChecked) binding.reportWrite.visibility = VISIBLE
            else binding.reportWrite.visibility = GONE
        }

        binding.reportButton.setOnSingleClickListener {
            var type = ""
            var content = ""
            val news_id = arguments?.getString("news_id").toString()
            val reply_id = arguments?.getString("reply_id").toString()

            if (binding.reportCheck1.isChecked) type += binding.reportCheck1.text.toString() + ","
            if (binding.reportCheck2.isChecked) type += binding.reportCheck2.text.toString() + ","
            if (binding.reportCheck3.isChecked) type += binding.reportCheck3.text.toString() + ","
            if (binding.reportCheck4.isChecked) type += binding.reportCheck4.text.toString() + ","
            if (binding.reportCheck5.isChecked) type += binding.reportCheck5.text.toString() + ","
            if (binding.reportCheck6.isChecked && binding.reportWrite.text.toString() != "") {
                type += binding.reportCheck6.text.toString() + ","
                content += binding.reportWrite.text.toString()
            }

            if (type != "") type = type.substring(0, type.length - 1)
            Log.d("신고 타입", type)
            Log.d("신고 기타 사유", content)

            if (type != "") {
                reportViewModel.report(access_token, content, type, reply_id, news_id)
                reportViewModel.reportResponse.observe(viewLifecycleOwner, Observer {reportResponse ->
                    if(reportResponse.report) {
                        dismiss()
                        Toast.makeText(requireContext(), "신고되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        val alertDialog = ReportAlertFragment.getInstanceFragment()
                        if (!alertDialog.isAdded) {
                            alertDialog.show(
                                requireActivity().supportFragmentManager,
                                "ReportAlertFragment"
                            )
                        }
                    }
                })
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}