package com.example.shortnews.ui.member

import S3Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.regions.Regions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.shortnews.R
import com.example.shortnews.UserSharedPreferences
import com.example.shortnews.databinding.FragmentSignupDialogBinding
import com.example.shortnews.setOnSingleClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class SignupDialogFragment(info:MutableList<String>, platform:String) : DialogFragment() {

    private var _binding: FragmentSignupDialogBinding?=null
    private val binding get()= _binding!!
    private var select_category = MutableList(8) { false }
    private val viewModel: SignupViewModel by viewModels()
    private val info = info
    private val s3service = S3Service
    private val platform = platform
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentSignupDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activityIntent = activity?.intent

        if(activityIntent?.type?.startsWith("image/")==true){
            val uri = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                activityIntent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
            else
                activityIntent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri
            binding.profileImg.setImageURI(uri)
        }

        // Uri를 실제 파일 경로로 변환하는 함수
        fun getRealPathFromURI(uri: Uri): String? {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = requireContext().contentResolver.query(uri, projection, null, null, null)
            val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()
            val path = cursor?.getString(columnIndex ?: 0)
            cursor?.close()
            return path
        }

        val requestPhoto =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

                if (uri != null) {
                    // 여기에서 uri를 사용하여 작업 수행
                    val filePath = getRealPathFromURI(uri)
                    if (filePath != null) {
                        val imageFile = File(filePath)

                        s3service.instance
                            .setKeys(resources.getString(R.string.access_key), resources.getString(R.string.secret_key))
                            .setRegion(Regions.AP_NORTHEAST_2)
                            .uploadWithTransferUtility(
                                requireContext(),
                                "snewsuserprofile",
                                "",
                                imageFile,
                                info[0],
                                object : TransferListener {
                                    override fun onStateChanged(id: Int, state: TransferState?) {
                                        // 전송 상태
                                        Log.d("이미지 업로드 응답", "전송 ID: $id, 상태: ${state?.toString()}")
                                        if (state?.toString() == "COMPLETED") {
                                            binding.profileImg.setImageURI(uri)
                                        }
                                    }

                                    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                                        // 필요한 경우 진행률 변경 처리
                                    }

                                    override fun onError(id: Int, ex: Exception?) {
                                        Log.e("이미지 업로드 응답", "전송 중 오류 ID: $id", ex)
                                    }
                                }
                            )
                    }
                }
            }

        fun setProfileImage() {
            val defaultImage = "https://snewsuserprofile.s3.ap-northeast-2.amazonaws.com/default"

            Glide.with(this)
                .load(defaultImage)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(defaultImage)
                .into(binding.profileImg)
        }

        // 사진 변경 버튼 클릭
        binding.changeProfile.setOnSingleClickListener {
            requestPhoto.launch("image/*")
        }

        // 기본 이미지 버튼 클릭
        binding.defaultProfile.setOnSingleClickListener {
            val id = UserSharedPreferences.sharedPreferences.getString("id", null).toString()

            // 백그라운드에서 작업
            GlobalScope.launch(Dispatchers.IO) {
                s3service.instance
                    .setKeys(resources.getString(R.string.access_key), resources.getString(R.string.secret_key))
                    .setRegion(Regions.AP_NORTHEAST_2)
                    .deleteObjectFromS3("snewsuserprofile", id)
                // 백그라운드 작업 끝나면 메인에서 이미지 업데이트
                withContext(Dispatchers.Main) {
                    setProfileImage()
                }
            }
        }




        fun setCategoryClickListener(view: View, index: Int, colorCode: String) {
            view.setOnClickListener {
                val c = if (select_category[index] == false) {
                    colorCode
                } else {
                    "#000000"
                }
                val color = Color.parseColor(c)
                select_category[index] = !select_category[index]
                (view as ImageView).setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            }
        }

        setCategoryClickListener(binding.category100, 0, "#FF5ED2")
        setCategoryClickListener(binding.category101, 1, "#FF4141")
        setCategoryClickListener(binding.category102, 2, "#FFBF44")
        setCategoryClickListener(binding.category103, 3, "#FFEC41")
        setCategoryClickListener(binding.category104, 4, "#3FDC4F")
        setCategoryClickListener(binding.category105, 5, "#37FFF3")
        setCategoryClickListener(binding.category106, 6, "#3783F6")
        setCategoryClickListener(binding.category107, 7, "#8F10F3")


        // 건너뛰기
        binding.skip.setOnSingleClickListener {
            select_category = MutableList(8) { true }

            viewModel.selectCategory(info[0], info[1], info[2], info[3], platform, select_category)

            viewModel.selectCategoryResponse.observe(viewLifecycleOwner) {selectCategoryResponse ->
                if (selectCategoryResponse.status == "OK") {
                    if (platform == "I") {
                        findNavController().navigate(R.id.action_signupFragment_to_newsMainFragment)
                    } else {
                        findNavController().navigate(R.id.action_loginFragment_to_newsMainFragment)
                    }

                    dismiss()
                }
            }
        }

        // 완료
        binding.completion.setOnSingleClickListener {

            viewModel.selectCategory(info[0], info[1], info[2], info[3], platform, select_category)

            viewModel.selectCategoryResponse.observe(viewLifecycleOwner) {selectCategoryResponse ->
                if (selectCategoryResponse.status == "OK") {
                    if (platform == "I") {
                        findNavController().navigate(R.id.action_signupFragment_to_newsMainFragment)
                    } else {
                        findNavController().navigate(R.id.action_loginFragment_to_newsMainFragment)
                    }
                    dismiss()
                }
            }
        }

    } // onViewCreated

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}