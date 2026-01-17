package com.example.shortnews.ui.news

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.shortnews.R
import com.example.shortnews.UserSharedPreferences
import com.example.shortnews.databinding.FragmentNewsMainBinding
import com.example.shortnews.model.BookmarkNewsItem
import com.example.shortnews.model.Category
import com.example.shortnews.model.RecommendItem
import com.example.shortnews.setOnSingleClickListener
import com.example.shortnews.ui.adapter.KeywordAdapter
import com.example.shortnews.ui.NotificationIconFragment
import com.example.shortnews.ui.NotificationIconViewModel
import com.example.shortnews.ui.adapter.NewsAdapter
import com.example.shortnews.ui.adapter.RecommendNewsAdapter
import com.example.shortnews.ui.adapter.SearchAdapter
import com.launchdarkly.eventsource.ConnectStrategy
import com.launchdarkly.eventsource.EventSource
import com.launchdarkly.eventsource.MessageEvent
import com.launchdarkly.eventsource.background.BackgroundEventHandler
import com.launchdarkly.eventsource.background.BackgroundEventSource
import okhttp3.Headers
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class NewsMainFragment : Fragment(), BackgroundEventHandler {

        private var _binding: FragmentNewsMainBinding?=null
        private val binding get() = _binding!!

        private val newsMainViewModel: NewsMainViewModel by viewModels()
        private val calendarviewModel: CalendarViewModel by viewModels()

        private val adapter = NewsAdapter()
        private val recommendNewsAdapter = RecommendNewsAdapter()
        private val searchAdapter = SearchAdapter()
        private val keywordAdapter = KeywordAdapter()

        private var firstLoad = true
        private var lastCategory = ""
        private var lastDate = ""
        private var lastSelectedView: TextView? = null

        private val access_token = UserSharedPreferences.getAccessToken()

        private var openKeyword = false
        private var groupedByCategory: Map<String, List<Category>>? = null

        private var dialog:AlarmDialogFragment? = null

        var allKeyword: List<Category>? = null

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
            _binding = FragmentNewsMainBinding.inflate(inflater, container, false)
            //로딩 화면 표시
//            showLoadingScreen()
            if (firstLoad) {

                binding.RecyclerView.adapter = recommendNewsAdapter
                lastCategory = "recommend"

                newsMainViewModel.getRecommend(access_token)
                newsMainViewModel.recommend.observe(viewLifecycleOwner, Observer { recommend ->
                    val data = mutableListOf<RecommendItem>()
                    recommend.selectedList?.let { data.addAll(it) }
                    recommend.nonselectedList?.let { data.addAll(it) }
                    recommend.subList?.let { data.addAll(it) }
                    recommendNewsAdapter.setData(data)
                })

                binding.recommendation.isSelected = true
                lastSelectedView = binding.recommendation
                val selectedTextColor = ContextCompat.getColor(requireContext(), R.color.white)
                binding.recommendation.setTextColor(selectedTextColor)

                // 오늘의 키워드 세팅
                val today = Date()
                val outputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                val date = outputFormat.format(today)

                newsMainViewModel.keyword(access_token, date)
                newsMainViewModel.keywordResponse.observe(viewLifecycleOwner, Observer { keywordResponse ->
                    if (keywordResponse.wordlist.isNotEmpty()) {
                        allKeyword = keywordResponse.wordlist.sortedByDescending { it.value }.subList(0, 30)
                        groupedByCategory = keywordResponse.wordlist.groupBy { it.cate }

                        allKeyword?.let { it -> keywordAdapter.setData(it) }
                    }

                })

                // 알림
                this.SSEConnect = BackgroundEventSource.Builder(this, EventSource.Builder(this.Info))
                this.SSE = this.SSEConnect!!.build()
                this.SSE?.start()
                newsMainViewModel.checkAlarm(access_token, binding)
                firstLoad = false

//                Handler(Looper.getMainLooper()).postDelayed({
//                    // 로딩 화면 숨김
//                    hideLoadingScreen()
//                }, 2000) // 2000 밀리초(2초)의 지연
            }
            return binding.root
        }

    private fun showLoadingScreen() {
    }

    private fun hideLoadingScreen() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            binding.include.bell.setOnSingleClickListener {
                 dialog = AlarmDialogFragment(binding.include)
                 dialog!!.show(requireActivity().supportFragmentManager, "AlarmDialogFragment")
            }

            // 뒤로가기 버튼 두 번 누르면 앱 종료
            var backPressedTime: Long = 0

            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val currentTime = System.currentTimeMillis()

                    if (currentTime - backPressedTime < 2000) {
                        requireActivity().finish()
                    } else {
                        Toast.makeText(requireContext(), "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                        backPressedTime = currentTime
                    }
                }
            }

            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

            // 프로필 이미지 세팅
            val profileImage =  UserSharedPreferences.sharedPreferences.getString("profileImage", null)
            val defaultImage = "https://snewsuserprofile.s3.ap-northeast-2.amazonaws.com/default"

            Glide.with(this)
                .load(profileImage)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(defaultImage)
                .into(binding.include.profile)

            val manager = LinearLayoutManager(context)
            binding.RecyclerView.layoutManager = manager
            val manager2 = LinearLayoutManager(context)
            binding.keywordRecyclerView.layoutManager = manager2
            binding.keywordRecyclerView.adapter = keywordAdapter

            // 오늘의 키워드 세팅
            val today = Date()
            val outputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val date = outputFormat.format(today)

            newsMainViewModel.keyword(access_token, date)
            newsMainViewModel.keywordResponse.observe(viewLifecycleOwner, Observer { keywordResponse ->
                if (keywordResponse.wordlist.isNotEmpty())
                    groupedByCategory = keywordResponse.wordlist.groupBy { it.cate }
            })


            // 선호 카테고리 세팅
            val category_str = UserSharedPreferences.sharedPreferences.getString("category", null)
            if (category_str != "") {
                val category = category_str?.split(",")?.map { it.toInt() } ?: emptyList()  // [100, 101, 102, 103, 104, 105, 106, 107]

                for (c in category) {
                    when (c) {
                        100 -> binding.politicsBtn.visibility = View.VISIBLE
                        101 -> binding.economyBtn.visibility = View.VISIBLE
                        102 -> binding.societyBtn.visibility = View.VISIBLE
                        103 -> binding.cultureBtn.visibility = View.VISIBLE
                        104 -> binding.worldBtn.visibility = View.VISIBLE
                        105 -> binding.itBtn.visibility = View.VISIBLE
                        106 -> binding.entertainmentBtn.visibility = View.VISIBLE
                        107 -> binding.sportsBtn.visibility = View.VISIBLE
                    }
                }
            }

            // 로고 클릭
            binding.include.logo.setOnSingleClickListener {
                binding.recommendation.performClick()
            }

            // 북마크 클릭 (카테고리)
            adapter.setBookmarkClickListener(object : NewsAdapter.OnBookmarkClickListener {
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

            // 북마크 클릭 (검색)
            searchAdapter.setBookmarkClickListener(object : SearchAdapter.OnBookmarkClickListener {
                override fun onBookmarkClick(position: Int) {
                    var type = false
                    var data = searchAdapter.getItem(position)
                    if (data.bookmark == 1) type = true

                    newsMainViewModel.bookmark(access_token, data.news_id, type)
                    newsMainViewModel.bookmarkResponse.observe(viewLifecycleOwner, Observer { bookmarkResponse ->
                        if (bookmarkResponse.status == "OK") {
                            data.bookmark = if (type) 0 else 1
                            searchAdapter.notifyDataSetChanged()
                        }
                    })
                }
            })

            // 카테고리 뉴스 클릭
            adapter.setListener {v, position ->
                val data = adapter.getItem(position)
                val bundle = Bundle().apply {
                    putString("news_id", data.news_id)
                }
                findNavController().navigate(R.id.action_newsMainFragment_to_newsContentFragment, bundle)
            }

            // 추천 뉴스 클릭
            recommendNewsAdapter.setListener {v, position ->
                val data = recommendNewsAdapter.getItem(position)
                val bundle = Bundle().apply {
                    putString("news_id", data.news_id)
                }
                findNavController().navigate(R.id.action_newsMainFragment_to_newsContentFragment, bundle)
            }

            // 검색 뉴스 클릭
            searchAdapter.setListener {v, position ->
                val data = searchAdapter.getItem(position)
                val bundle = Bundle().apply {
                    putString("news_id", data.news_id)
                }
                findNavController().navigate(R.id.action_newsMainFragment_to_newsContentFragment, bundle)
            }

            val calendar = Calendar.getInstance()

            val dateFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
            val formattedDate = dateFormat.format(calendar.time)

            binding.date.text = formattedDate

            val categoryMap = mapOf(
                R.id.politics to "100",
                R.id.economy to "101",
                R.id.society to "102",
                R.id.culture to "103",
                R.id.world to "104",
                R.id.it to "105",
                R.id.entertainment to "106",
                R.id.sports to "107",
            )

            // 카테고리 선택
            val categoryClickListener = View.OnClickListener { view ->

                lastSelectedView?.let {
                    it.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }

                binding.recommendation.isSelected = false
                binding.politics.isSelected = false
                binding.economy.isSelected = false
                binding.society.isSelected = false
                binding.culture.isSelected = false
                binding.world.isSelected = false
                binding.it.isSelected = false
                binding.entertainment.isSelected = false
                binding.sports.isSelected = false

                view.isSelected = true

                if (view is TextView) {
                    lastSelectedView = view
                    val selectedTextColor = if (view.isSelected) {
                        ContextCompat.getColor(requireContext(), R.color.white)
                    } else {
                        ContextCompat.getColor(requireContext(), R.color.black)
                    }
                    view.setTextColor(selectedTextColor)
                }

                val category = categoryMap[view.id]
                if (category != null) {
                    binding.RecyclerView.adapter = adapter

                    val inputFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                    val date_obj = inputFormat.parse(binding.date.text.toString())
                    val date = outputFormat.format(date_obj)

                    // 뉴스 가져 오는 요청 보내기
                    lastCategory = category
                    lastDate = date
                    newsMainViewModel.selectCategory(access_token, category, date)
                    newsMainViewModel.news.observe(viewLifecycleOwner, Observer { news ->
                        adapter.setData(news.news_list)
                    })

                    // 오늘의 키워드 세팅
                    binding.keywordRecyclerView.adapter = keywordAdapter
                    groupedByCategory?.get(category)?.let { keywordAdapter.setData(it) }

                }
            }

            // 추천 버튼
            binding.recommendation.setOnSingleClickListener {

                lastSelectedView?.let {
                    it.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }

                binding.recommendation.isSelected = false
                binding.politics.isSelected = false
                binding.economy.isSelected = false
                binding.society.isSelected = false
                binding.culture.isSelected = false
                binding.world.isSelected = false
                binding.it.isSelected = false
                binding.entertainment.isSelected = false
                binding.sports.isSelected = false

                binding.recommendation.isSelected = true
                lastSelectedView = binding.recommendation

                val selectedTextColor = ContextCompat.getColor(requireContext(), R.color.white)
                binding.recommendation.setTextColor(selectedTextColor)

                binding.RecyclerView.adapter = recommendNewsAdapter

                lastCategory = "recommend"
                newsMainViewModel.getRecommend(access_token)
                newsMainViewModel.recommend.observe(viewLifecycleOwner, Observer { recommend ->
                    val data = mutableListOf<RecommendItem>()
                    recommend.selectedList?.let { data.addAll(it) }
                    recommend.nonselectedList?.let { data.addAll(it) }
                    recommend.subList?.let { data.addAll(it) }
                    recommendNewsAdapter.setData(data)
                })

                // 오늘의 키워드 세팅
                binding.keywordRecyclerView.adapter = keywordAdapter
                allKeyword?.let { it -> keywordAdapter.setData(it) }
            }

            binding.politics.setOnSingleClickListener{categoryClickListener.onClick(it)}
            binding.economy.setOnSingleClickListener{categoryClickListener.onClick(it)}
            binding.society.setOnSingleClickListener{categoryClickListener.onClick(it)}
            binding.culture.setOnSingleClickListener{categoryClickListener.onClick(it)}
            binding.world.setOnSingleClickListener{categoryClickListener.onClick(it)}
            binding.it.setOnSingleClickListener{categoryClickListener.onClick(it)}
            binding.entertainment.setOnSingleClickListener{categoryClickListener.onClick(it)}
            binding.sports.setOnSingleClickListener{categoryClickListener.onClick(it)}


            // 달력에서 날짜 선택
            binding.calendar.setOnSingleClickListener {
                val dialog = CalendarFragment()
                dialog.viewModel = calendarviewModel
                dialog.show(requireActivity().supportFragmentManager, "CalendarFragment")
            }

            calendarviewModel.selectedDate.observe(viewLifecycleOwner, Observer { selectedDate ->
                binding.date.text = selectedDate

                val inputFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
                val outputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                val date_obj = inputFormat.parse(binding.date.text.toString())

                val category = lastCategory
                val date = outputFormat.format(date_obj)

                newsMainViewModel.selectCategory(access_token, category, date)
                newsMainViewModel.news.observe(viewLifecycleOwner, Observer { news ->
                    adapter.setData(news.news_list)
                })

                // 키워드 요청 보내기
                if (category != "recommend") {
                    newsMainViewModel.keyword(access_token, date)
                    newsMainViewModel.keywordResponse.observe(viewLifecycleOwner, Observer { keywordResponse ->
                        if (keywordResponse.wordlist.isNotEmpty()) {
                            groupedByCategory = keywordResponse.wordlist.groupBy { it.cate }
                            groupedByCategory!![category]?.let { keywordAdapter.setData(it) }
                        }
                    })
                }

            })

            // 검색 돋보기 클릭
            binding.searchSendBtn.setOnSingleClickListener {

                lastSelectedView?.let {
                    it.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
                binding.recommendation.isSelected = false
                binding.politics.isSelected = false
                binding.economy.isSelected = false
                binding.society.isSelected = false
                binding.culture.isSelected = false
                binding.world.isSelected = false
                binding.it.isSelected = false
                binding.entertainment.isSelected = false
                binding.sports.isSelected = false

                if (!binding.searchText.text.toString().isEmpty()) {
                    binding.RecyclerView.adapter = searchAdapter
                    val keyword = binding.searchText.text.toString()

                    newsMainViewModel.search(access_token, keyword, 1, 10000000)
                    newsMainViewModel.searchResponse.observe(viewLifecycleOwner, Observer { searchResponse ->
                        searchAdapter.setData(searchResponse.news_list)
                        binding.searchText.text = Editable.Factory.getInstance().newEditable(keyword)
                    })

                    lastCategory = "search"
                }
            }

            // 프로필 이미지 클릭
            binding.include.profile.setOnSingleClickListener {
                findNavController().navigate(R.id.action_newsMainFragment_to_myPageFragment)
            }

            // 오늘의 키워드 클릭 (열기)
            binding.keyword.setOnSingleClickListener {
                if (binding.keywordRecyclerView.visibility != View.VISIBLE) {
                    binding.keywordRecyclerView.visibility = View.VISIBLE
                    binding.drop.setImageResource(R.drawable.drop_up)
                    openKeyword = true
                }
                else {
                    binding.keywordRecyclerView.visibility = View.GONE
                    binding.drop.setImageResource(R.drawable.drop_down)
                    openKeyword = false
                }
            }
            
            // 키워드 클릭 (검색)
            keywordAdapter.setListener {v, position ->
                val data = keywordAdapter.getItem(position)
                binding.RecyclerView.adapter = searchAdapter

                val keyword = data.text
                newsMainViewModel.search(access_token, keyword, 1, 10000000)
                newsMainViewModel.searchResponse.observe(viewLifecycleOwner, Observer { searchResponse ->
                    searchAdapter.setData(searchResponse.news_list)
                    binding.searchText.text = Editable.Factory.getInstance().newEditable(keyword)
                    lastCategory = "search"
                })
            }


        }

        override fun onPause() {
           super.onPause()
        }

        override fun onResume() {

            super.onResume()

            // 다시 메인 화면으로 돌아오면 마지막 선택한 카테고리 클릭
            val category = lastCategory

            if (category == "recommend") {
                binding.recommendation.performClick()
            } else if (category == "search") {
                binding.RecyclerView.adapter = searchAdapter
                val keyword = binding.searchText.text.toString()
                newsMainViewModel.search(access_token, keyword, 1, 10000000)
                newsMainViewModel.searchResponse.observe(viewLifecycleOwner, Observer { searchResponse ->
                    searchAdapter.setData(searchResponse.news_list)
                })
            } else {
                when  {
                    lastCategory == "100" && binding.politicsBtn.visibility == View.VISIBLE -> binding.politics.performClick()
                    lastCategory == "101" && binding.economyBtn.visibility == View.VISIBLE -> binding.economy.performClick()
                    lastCategory == "102" && binding.societyBtn.visibility == View.VISIBLE -> binding.society.performClick()
                    lastCategory == "103" && binding.cultureBtn.visibility == View.VISIBLE -> binding.culture.performClick()
                    lastCategory == "104" && binding.worldBtn.visibility == View.VISIBLE -> binding.world.performClick()
                    lastCategory == "105" && binding.itBtn.visibility == View.VISIBLE -> binding.it.performClick()
                    lastCategory == "106" && binding.entertainmentBtn.visibility == View.VISIBLE -> binding.entertainment.performClick()
                    lastCategory == "107" && binding.sportsBtn.visibility == View.VISIBLE -> binding.sports.performClick()
                    else -> {
                        binding.recommendation.performClick()
                    }
                }

            }

            if (openKeyword) {
                binding.keywordRecyclerView.visibility = View.VISIBLE
                binding.drop.setImageResource(R.drawable.drop_up)
            }
            else {
                binding.keywordRecyclerView.visibility = View.GONE
                binding.drop.setImageResource(R.drawable.drop_down)
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
            val data = JSONObject(messageEvent?.data!!)
            Log.d("onMessage", event!!)
            Log.d("onMessage", messageEvent?.data!!)
            binding.include.notificationfragment.notification.visibility = View.VISIBLE

            // 알림 생성 및 헤드업 알림 설정
            val notificationIntent = Intent(requireContext(), this::class.java)
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                requireContext(),
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val type = data.getInt("type")
            val nick = data.getString("nickname")
            var title = when(type) {
                1 -> "'${nick}'님이 추천을 눌렀습니다."
                0 -> "'${nick}'님이 답글을 달았습니다."
                else -> "'${nick}'님이 비추천을 눌렀습니다."
            }
            NotificationIconViewModel.NotificationHelper.showNotification(requireContext(), title, pendingIntent)
        }

        override fun onComment(comment: String?) {
            Log.d("onComment", comment!!)
        }

        override fun onError(t: Throwable?) {
            Log.e("에러남", t.toString())
            t?.printStackTrace()
        }
}