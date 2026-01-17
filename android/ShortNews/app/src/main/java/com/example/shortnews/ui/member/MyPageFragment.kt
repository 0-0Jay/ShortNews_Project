package com.example.shortnews.ui.member

import S3Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.regions.Regions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.shortnews.R
import com.example.shortnews.UserSharedPreferences
import com.example.shortnews.databinding.FragmentMyPageBinding
import com.example.shortnews.model.ActivityNewsItem
import com.example.shortnews.model.ActivityReplyItem
import com.example.shortnews.setOnSingleClickListener
import com.example.shortnews.ui.adapter.ActivityAdapter
import com.example.shortnews.ui.adapter.ActivityReplyAdapter
import com.example.shortnews.ui.news.NewsMainViewModel
import com.example.shortnews.ui.news.AlarmDialogFragment
import com.launchdarkly.eventsource.ConnectStrategy
import com.launchdarkly.eventsource.EventSource
import com.launchdarkly.eventsource.MessageEvent
import com.launchdarkly.eventsource.background.BackgroundEventHandler
import com.launchdarkly.eventsource.background.BackgroundEventSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Headers
import org.apache.commons.lang3.StringUtils.isNumeric
import java.io.File
import java.net.URL
import java.util.concurrent.TimeUnit

class MyPageFragment : Fragment(), BackgroundEventHandler {

    private var _binding: FragmentMyPageBinding?=null
    private val binding get() = _binding!!
    private var select_category = MutableList(8) { false }
    val viewModel: MyPageViewModel by viewModels()
    val newsMainViewModel: NewsMainViewModel by viewModels()
    private val s3service = S3Service
    private val adapter = ActivityAdapter()
    private val reply_adapter = ActivityReplyAdapter()
    var lastSelectedView: TextView? = null
    private var lastActivityView: TextView? = null
    private var selectSortBtn: TextView? = null
    private var origin_data:List<ActivityNewsItem>? = null
    private var reverse_data:List<ActivityNewsItem>? = null
    private var origin_reply_data:List<ActivityReplyItem>? = null
    private var reverse_reply_data:List<ActivityReplyItem>? = null
    private var firstLoad = true
    private var firstActivityLoad = true
    private var flatform = true
    private val access_token = UserSharedPreferences.getAccessToken()

    // SSE
    final val TIME : Long = 1000L * 60L * 60L * 24L
    private val HEADERS: Array<String> = arrayOf("Content-Type", "text/event-stream", "Authorization", access_token)
    val Info = ConnectStrategy
        .http(URL("http://api.shortnews.kr:8090/sub"))
        .headers(Headers.headersOf(HEADERS[0], HEADERS[1], HEADERS[2], HEADERS[3]))
        .readTimeout(TIME, TimeUnit.MILLISECONDS)
        .writeTimeout(TIME, TimeUnit.MILLISECONDS)
        .connectTimeout(TIME, TimeUnit.MILLISECONDS)
    var SSEConnect: BackgroundEventSource.Builder? = null
    var SSE : BackgroundEventSource? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)

        if (firstLoad) {
            binding.userInfo.isSelected = true
            lastSelectedView = binding.userInfo
            val selectedTextColor = ContextCompat.getColor(requireContext(), R.color.white)
            binding.userInfo.setTextColor(selectedTextColor)

            viewModel.nickname.observe(viewLifecycleOwner) { newNickname ->
                binding.changeNicknameText.text = Editable.Factory.getInstance().newEditable(newNickname)
            }

            viewModel.phone.observe(viewLifecycleOwner) { newPhone ->
                val regex = """(\d{3})(\d{4})(\d{4})""".toRegex()
                val phone = regex.replace(newPhone, "$1-$2-$3")
                binding.changePhoneText.text = Editable.Factory.getInstance().newEditable(phone)
            }

            binding.changeNicknameText.text = Editable.Factory.getInstance().newEditable(UserSharedPreferences.sharedPreferences.getString("nickname", null))
            var phone = UserSharedPreferences.sharedPreferences.getString("phone", null).toString()
            val regex = """(\d{3})(\d{4})(\d{4})""".toRegex()
            phone = regex.replace(phone, "$1-$2-$3")
            binding.changePhoneText.text = Editable.Factory.getInstance().newEditable(phone)

            flatform = isNumeric(binding.changePhoneText.text.toString().replace("-", ""))

            if (!flatform) {
                binding.pw.visibility = View.GONE
                binding.changePwBtn.visibility = View.GONE
            }


            // 알림
            this.SSEConnect = BackgroundEventSource.Builder(this, EventSource.Builder(this.Info))
            this.SSE = this.SSEConnect!!.build()
            this.SSE?.start()
            viewModel.checkAlarm(access_token, binding)
            firstLoad = false

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 로고 클릭
        binding.include.logo.setOnSingleClickListener {
            findNavController().navigate(R.id.action_myPageFragment_to_newsMainFragment)
        }
        binding.include.bell.setOnSingleClickListener {
            val dialog = AlarmDialogFragment(binding.include)
            dialog.show(requireActivity().supportFragmentManager, "AlarmDialogFragment")
        }
        // 프로필 이미지 세팅
        fun setProfileImage() {
            val profileImage =  UserSharedPreferences.sharedPreferences.getString("profileImage", null)
            val defaultImage = "https://snewsuserprofile.s3.ap-northeast-2.amazonaws.com/default"

            Glide.with(this)
                .load(profileImage)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(defaultImage)
                .into(binding.include.profile)

            Glide.with(this)
                .load(profileImage)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(defaultImage)
                .into(binding.profileImg)
        }

        setProfileImage()

        fun updateProfile() {
            val profileImage =  UserSharedPreferences.sharedPreferences.getString("profileImage", null)
            val defaultImage = "https://snewsuserprofile.s3.ap-northeast-2.amazonaws.com/default"

            Glide.with(this)
                .load(profileImage)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(defaultImage)
                .into(binding.include.profile)
        }

        // 카테고리 세팅
        val category_str = UserSharedPreferences.sharedPreferences.getString("category", null)

        if (category_str != "") {
            val category = category_str?.split(",")?.map { it.toInt() } ?: emptyList()  // [100, 101, 102, 103, 104, 105, 106, 107]

            for (c in category) {
                select_category[c-100] = true

                when (c) {
                    100 -> binding.category100.setColorFilter(Color.parseColor("#FF5ED2"), PorterDuff.Mode.SRC_ATOP)
                    101 -> binding.category101.setColorFilter(Color.parseColor("#FF4141"), PorterDuff.Mode.SRC_ATOP)
                    102 -> binding.category102.setColorFilter(Color.parseColor("#FFBF44"), PorterDuff.Mode.SRC_ATOP)
                    103 -> binding.category103.setColorFilter(Color.parseColor("#FFEC41"), PorterDuff.Mode.SRC_ATOP)
                    104 -> binding.category104.setColorFilter(Color.parseColor("#3FDC4F"), PorterDuff.Mode.SRC_ATOP)
                    105 -> binding.category105.setColorFilter(Color.parseColor("#37FFF3"), PorterDuff.Mode.SRC_ATOP)
                    106 -> binding.category106.setColorFilter(Color.parseColor("#3783F6"), PorterDuff.Mode.SRC_ATOP)
                    107 -> binding.category107.setColorFilter(Color.parseColor("#8F10F3"), PorterDuff.Mode.SRC_ATOP)
                }
            }
        }

        // tts 세팅
        val model = UserSharedPreferences.sharedPreferences.getString("model", null)
        val speed = UserSharedPreferences.sharedPreferences.getString("speed", null)

        if (model == "_male") binding.ttsMale.isChecked = true
        else binding.ttsFemale.isChecked = true

        if (speed == "0.75") binding.ttsSlow.isChecked = true
        else if (speed == "1") binding.ttsNomal.isChecked = true
        else binding.ttsFast.isChecked = true


        // 회원 정보 버튼
        binding.userInfo.setOnClickListener {

            lastSelectedView?.let {
                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }

            binding.userInfo.isSelected = true
            binding.myActivity.isSelected = false
            lastSelectedView = binding.userInfo

            val selectedTextColor = ContextCompat.getColor(requireContext(), R.color.white)
            binding.userInfo.setTextColor(selectedTextColor)

            binding.userInfoView.visibility = View.VISIBLE
            binding.myActivityView.visibility = View.GONE

        }

        // 나의 활동 버튼
        binding.myActivity.setOnClickListener {

            lastSelectedView?.let {
                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }

            binding.userInfo.isSelected = false
            binding.myActivity.isSelected = true
            lastSelectedView = binding.myActivity

            val selectedTextColor = ContextCompat.getColor(requireContext(), R.color.white)
            binding.myActivity.setTextColor(selectedTextColor)

            binding.userInfoView.visibility = View.GONE
            binding.myActivityView.visibility = View.VISIBLE

            binding.likeActivity.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.dislikeActivity.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.replyActivity.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.likeActivity.isSelected = true
            binding.dislikeActivity.isSelected = false
            binding.replyActivity.isSelected = false

            binding.RecyclerView.adapter = adapter
            val manager = LinearLayoutManager(context)
            binding.RecyclerView.layoutManager = manager

            if (firstActivityLoad) {
                lastActivityView = binding.likeActivity

                viewModel.likeActivity(access_token)
                viewModel.likeActivityResponse.observe(viewLifecycleOwner, Observer { likeActivityResponse ->
                    origin_data = likeActivityResponse.newsLike
                    reverse_data = likeActivityResponse.newsLike.reversed()
                    adapter.setData(origin_data!!)

                    selectSortBtn = binding.newText
                    selectSortBtn?.let {
                        it.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        binding.oldText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    }
                    binding.newText.isSelected = true
                    binding.oldText.isSelected = false

                })

                firstActivityLoad = false
            }
        }

        // 북마크 버튼
        binding.bookmark.setOnSingleClickListener {
            findNavController().navigate(R.id.action_myPageFragment_to_bookmarkFragment)
        }

        // 로그아웃 버튼
        binding.logout.setOnSingleClickListener {

            // popUpTo: 목표로 하는 경로까지 pop
            // popUpToInclusive: popUpTo에 지정된 경로를 포함하여 pop
            val options = NavOptions.Builder()
                .setPopUpTo(R.id.loginFragment, true) // true 하면 도착점도 pop
                .build()

            viewModel.logout(access_token)
//            SseConnect.SSE?.close()
//            SseConnect.SSEConnect = null
//            SseConnect.SSE = null

            findNavController().navigate(R.id.action_myPageFragment_to_loginFragment, null, options)
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
                                UserSharedPreferences.sharedPreferences.getString("id", null),
                                object : TransferListener {
                                    override fun onStateChanged(id: Int, state: TransferState?) {
                                        // 전송 상태
                                        Log.d("이미지 업로드 응답", "전송 ID: $id, 상태: ${state?.toString()}")
                                        if (state?.toString() == "COMPLETED") {
                                            binding.profileImg.setImageURI(uri)
                                            val id = UserSharedPreferences.sharedPreferences.getString("id", null).toString()
                                            val editor = UserSharedPreferences.sharedPreferences.edit()
                                            editor.putString("profileImage", "https://snewsuserprofile.s3.ap-northeast-2.amazonaws.com/${id}")
                                            editor.apply()
                                            updateProfile()
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

        val activityIntent = activity?.intent

        if(activityIntent?.type?.startsWith("image/")==true){
            val uri = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                activityIntent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
            else
                activityIntent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri
            binding.profileImg.setImageURI(uri)
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

        // 닉네임 변경 버튼 클릭
        binding.changeNicknameBtn.setOnSingleClickListener {
            val dialog = NicknameDialogFragment()

            dialog.onNicknameChanged = { newNickname ->
                viewModel.setNickname(newNickname)
            }

            dialog.show(requireActivity().supportFragmentManager, "NicknameDialogFragment")
        }

        // 전화번호 변경 버튼 클릭
        binding.changePhoneBtn.setOnSingleClickListener {
            val dialog = PhoneDialogFragment()

            dialog.onPhoneChanged = { newPhone ->
                viewModel.setPhone(newPhone)
            }

            dialog.show(requireActivity().supportFragmentManager, "PhoneDialogFragment")
        }

        // 비밀번호 변경 버튼 클릭
        binding.changePwBtn.setOnSingleClickListener {
            val dialog = PwDialogFragment()

            dialog.show(requireActivity().supportFragmentManager, "PwDialogFragment")
        }

        // 카테고리 수정
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

                Log.d("선호 카테고리 변경 요청", select_category.toString())
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


        // 카테고리 저장 버튼 클릭
        binding.changeCategoryBtn.setOnSingleClickListener {
            viewModel.updateCategory(access_token, select_category)

            viewModel.categoryResponse.observe(viewLifecycleOwner, Observer { categoryResponse ->
                if (categoryResponse.status == "OK") {

                    val saveCategory = mutableListOf<Int>()

                    for (i in 0..7) {
                        if (select_category[i]) {
                            saveCategory.add(i + 100)
                        }
                    }

                    val editor = UserSharedPreferences.sharedPreferences.edit()

                    val category = saveCategory.joinToString(",")
                    editor.putString("category", category)
                    editor.apply()
                }
            })

            Toast.makeText(requireContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
        }

        // TTS 저장 버튼 클릭
        binding.changeTTSBtn.setOnSingleClickListener {
            val select_model = binding.ttsModel.checkedRadioButtonId
            val select_speed = binding.ttsSpeed.checkedRadioButtonId

            var model = resources.getResourceEntryName(select_model)
            var speed = resources.getResourceEntryName(select_speed)

            if (model == "ttsMale") model = "_male"
            else model = "_female"
            if (speed == "ttsSlow") speed = "0.75"
            else if (speed == "ttsNomal") speed = "1"
            else speed = "1.25"

            viewModel.updateTTS(access_token, model, speed)

            viewModel.ttsResponse.observe(viewLifecycleOwner, Observer { ttsResponse ->
                if (ttsResponse.status == "OK") {

                    val editor = UserSharedPreferences.sharedPreferences.edit()

                    editor.putString("model", model)
                    editor.putString("speed", speed)
                    editor.apply()
                }
            })
            Toast.makeText(requireContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show()

        }

        // 회원 탈퇴 버튼
        binding.delete.setOnClickListener {
            findNavController().navigate(R.id.action_myPageFragment_to_deleteMemberFragment)
        }


        // 좋아요 활동
        binding.likeActivity.setOnSingleClickListener {

            binding.likeActivity.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.dislikeActivity.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.replyActivity.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.likeActivity.isSelected = true
            binding.dislikeActivity.isSelected = false
            binding.replyActivity.isSelected = false

            lastActivityView = binding.likeActivity

            binding.RecyclerView.adapter = adapter
            val manager = LinearLayoutManager(context)
            binding.RecyclerView.layoutManager = manager

            viewModel.likeActivity(access_token)
            viewModel.likeActivityResponse.observe(viewLifecycleOwner, Observer { likeActivityResponse ->
                if (likeActivityResponse.status == "OK") {
                    origin_data = likeActivityResponse.newsLike
                    reverse_data = likeActivityResponse.newsLike.reversed()

                    when (selectSortBtn?.text.toString()) {
                        "최신순" -> {
                            adapter.setData(origin_data!!)
                            selectSortBtn = binding.newText
                            selectSortBtn?.let {
                                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                                binding.oldText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                            }
                            binding.newText.isSelected = true
                            binding.oldText.isSelected = false
                        }
                        else -> {
                            adapter.setData(reverse_data!!)
                            selectSortBtn = binding.oldText
                            selectSortBtn?.let {
                                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                                binding.newText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                            }
                            binding.newText.isSelected = false
                            binding.oldText.isSelected = true
                        }
                    }
                }
            })
        }

        // 싫어요 활동
        binding.dislikeActivity.setOnSingleClickListener {

            binding.likeActivity.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.dislikeActivity.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.replyActivity.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.likeActivity.isSelected = false
            binding.dislikeActivity.isSelected = true
            binding.replyActivity.isSelected = false

            lastActivityView = binding.dislikeActivity

            binding.RecyclerView.adapter = adapter
            val manager = LinearLayoutManager(context)
            binding.RecyclerView.layoutManager = manager

            viewModel.dislikeActivity(access_token)
            viewModel.dislikeActivityResponse.observe(viewLifecycleOwner, Observer { dislikeActivityResponse ->
                if (dislikeActivityResponse.status == "OK") {
                    origin_data = dislikeActivityResponse.newsDislike
                    reverse_data = dislikeActivityResponse.newsDislike.reversed()

                    when (selectSortBtn?.text.toString()) {
                        "최신순" -> {
                            adapter.setData(origin_data!!)
                            selectSortBtn = binding.newText
                            selectSortBtn?.let {
                                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                                binding.oldText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                            }
                            binding.newText.isSelected = true
                            binding.oldText.isSelected = false
                        }
                        else -> {
                            adapter.setData(reverse_data!!)
                            selectSortBtn = binding.oldText
                            selectSortBtn?.let {
                                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                                binding.newText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                            }
                            binding.newText.isSelected = false
                            binding.oldText.isSelected = true
                        }
                    }
                }

            })

        }

        // 댓글 활동
        binding.replyActivity.setOnSingleClickListener {
            binding.likeActivity.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.dislikeActivity.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.replyActivity.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.likeActivity.isSelected = false
            binding.dislikeActivity.isSelected = false
            binding.replyActivity.isSelected = true

            lastActivityView = binding.replyActivity

            binding.RecyclerView.adapter = reply_adapter
            val manager = LinearLayoutManager(context)
            binding.RecyclerView.layoutManager = manager

            viewModel.replyActivity(access_token)
            viewModel.replyActivityResponse.observe(viewLifecycleOwner, Observer { replyActivityResponse ->
                if (replyActivityResponse.status == "OK") {

                    origin_reply_data = replyActivityResponse.result
                    reverse_reply_data = replyActivityResponse.result.reversed()

                    when (selectSortBtn?.text.toString()) {
                        "최신순" -> {
                            reply_adapter.setData(origin_reply_data!!)
                            selectSortBtn = binding.newText
                            selectSortBtn?.let {
                                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                                binding.oldText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                            }
                            binding.newText.isSelected = true
                            binding.oldText.isSelected = false
                        }
                        else -> {
                            reply_adapter.setData(reverse_reply_data!!)
                            selectSortBtn = binding.oldText
                            selectSortBtn?.let {
                                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                                binding.newText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                            }
                            binding.newText.isSelected = false
                            binding.oldText.isSelected = true
                        }
                    }
                }
            })

        }

        // 최신순
        binding.newText.setOnSingleClickListener {
            selectSortBtn = binding.newText

            if (lastActivityView?.text.toString() == "댓글") {
                reply_adapter.setData(origin_reply_data!!)
            } else {
                adapter.setData(origin_data!!)
            }

            selectSortBtn?.let {
                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                binding.oldText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
            binding.newText.isSelected = true
            binding.oldText.isSelected = false
        }

        // 오래된 순
        binding.oldText.setOnSingleClickListener {
            selectSortBtn = binding.oldText

            if (lastActivityView?.text.toString() == "댓글") {
                reply_adapter.setData(reverse_reply_data!!)
            } else {
                adapter.setData(reverse_data!!)
            }

            selectSortBtn?.let {
                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                binding.newText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
            binding.newText.isSelected = false
            binding.oldText.isSelected = true

        }

        // 뉴스 클릭
        adapter.setListener {v, position ->
            val data = adapter.getItem(position)
            val bundle = Bundle().apply {
                putString("news_id", data.news_id)
            }
            findNavController().navigate(R.id.action_myPageFragment_to_newsContentFragment, bundle)
        }

        // 뉴스 클릭
        reply_adapter.setListener {v, position ->
            val data = reply_adapter.getItem(position)
            val bundle = Bundle().apply {
                putString("news_id", data.news_id)
            }
            findNavController().navigate(R.id.action_myPageFragment_to_newsContentFragment, bundle)
        }

        // 북마크 클릭 (나의 활동)
        adapter.setBookmarkClickListener(object : ActivityAdapter.OnBookmarkClickListener {
            override fun onBookmarkClick(position: Int) {
                var type = false
                var data = adapter.getItem(position)
                if (data.bookmark == 1) type = true

                newsMainViewModel.bookmark(access_token, data.news_id, type)
                newsMainViewModel.bookmarkResponse.observe(viewLifecycleOwner, Observer { bookmarkResponse ->
                    if (bookmarkResponse.status == "OK") {
                        data.bookmark = if (type) 0 else 1
                        adapter.notifyDataSetChanged()
                    }
                })
            }
        })

    }

    override fun onResume() {
        super.onResume()

        when (lastSelectedView?.text.toString()) {
            "회원 정보" -> binding.userInfo.performClick()
            "나의 활동" -> {
                binding.myActivity.performClick()

                when (lastActivityView?.text.toString()) {
                    "좋아요" -> binding.likeActivity.performClick()
                    "싫어요" -> binding.dislikeActivity.performClick()
                    "댓글" -> binding.replyActivity.performClick()
                }
            }
        }

        if (!flatform) {
            binding.pw.visibility = View.GONE
            binding.changePwBtn.visibility = View.GONE
        }

    } // onResume

    override fun onDestroyView() {
        super.onDestroyView()
        this.SSE?.close()
        this.SSEConnect = null
        _binding = null
    }

    // SSE 통신 부분
    override fun onOpen() {
        Log.d("통신", this.toString())
    }

    override fun onClosed() {
        Log.d("통신 해제", this.toString())

    }

    override fun onMessage(event: String?, messageEvent: MessageEvent?) {
        Log.d("onMessage", event!!)
        Log.d("onMessage", messageEvent?.data!!)
        binding.include.notificationfragment.notification.visibility = View.VISIBLE
    }

    override fun onComment(comment: String?) {
        Log.d("onComment", comment!!)
    }

    override fun onError(t: Throwable?) {
        Log.e("에러남", t.toString())
        t?.printStackTrace()
    }
}

