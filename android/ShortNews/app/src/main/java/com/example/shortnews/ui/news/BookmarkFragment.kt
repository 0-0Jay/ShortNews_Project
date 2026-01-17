package com.example.shortnews.ui.news

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.shortnews.R
import com.example.shortnews.UserSharedPreferences
import com.example.shortnews.databinding.FragmentBookmarkBinding
import com.example.shortnews.model.ActivityNewsItem
import com.example.shortnews.model.BookmarkNewsItem
import com.example.shortnews.setOnSingleClickListener
import com.example.shortnews.ui.adapter.BookmarkAdapter
import com.example.shortnews.ui.adapter.NewsAdapter
import com.example.shortnews.ui.member.MyPageViewModel
import com.launchdarkly.eventsource.ConnectStrategy
import com.launchdarkly.eventsource.EventSource
import com.launchdarkly.eventsource.MessageEvent
import com.launchdarkly.eventsource.background.BackgroundEventHandler
import com.launchdarkly.eventsource.background.BackgroundEventSource
import okhttp3.Headers
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class BookmarkFragment : Fragment(), BackgroundEventHandler {

    private var _binding: FragmentBookmarkBinding?=null
    private val binding get() = _binding!!
    private var lastSelectedView: TextView? = null
    private var selectSortBtn: TextView? = null
    private val adapter = BookmarkAdapter()
    private val viewModel: MyPageViewModel by viewModels()
    private val bookmarkViewModel: BookmarkViewModel by viewModels()
    private val newsMainViewModel: NewsMainViewModel by viewModels()
    private var categoryGroup: Map<Int, List<BookmarkNewsItem>> = mutableMapOf()
    private var firstLoad = true
    private var origin_data:List<BookmarkNewsItem>? = null
    private var reverse_data:List<BookmarkNewsItem>? = null
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
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        if (firstLoad) {
            selectSortBtn = binding.newText
            selectSortBtn?.let {
                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }

            binding.all.isSelected = true
            binding.all.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            lastSelectedView = binding.all
            binding.newText.isSelected = true

            val manager = LinearLayoutManager(context)
            binding.RecyclerView.layoutManager = manager
            binding.RecyclerView.adapter = adapter

            viewModel.getBookmarkNews(access_token)
            viewModel.bookmarkNewsResponse.observe(viewLifecycleOwner, Observer { bookmarkNewsResponse ->
                val data = bookmarkNewsResponse.bookmark
                origin_data = data
                reverse_data = data.toList().reversed()
                adapter.setData(data)
                // 카테고리 분류
                categoryGroup = data.groupBy { it.cate_id }
                Log.d("카테고리 그룹 응답", categoryGroup.toString())
            })


            // 알림
            this.SSEConnect = BackgroundEventSource.Builder(this, EventSource.Builder(this.Info))
            this.SSE = this.SSEConnect!!.build()
            this.SSE?.start()
            bookmarkViewModel.checkAlarm(access_token, binding)
            firstLoad = false
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val manager = LinearLayoutManager(context)
        binding.RecyclerView.layoutManager = manager
        binding.RecyclerView.adapter = adapter


        binding.include.bell.setOnSingleClickListener {
            val dialog = AlarmDialogFragment(binding.include)
            dialog.show(requireActivity().supportFragmentManager, "AlarmDialogFragment")
        }

        // 프로필 이미지 세팅
        val profileImage =  UserSharedPreferences.sharedPreferences.getString("profileImage", null)
        val defaultImage = "https://snewsuserprofile.s3.ap-northeast-2.amazonaws.com/default"

        Glide.with(this)
            .load(profileImage)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .error(defaultImage)
            .into(binding.include.profile)

        // 로고 클릭
        binding.include.logo.setOnSingleClickListener {
            findNavController().navigate(R.id.action_bookmarkFragment_to_newsMainFragment)
        }

        // 프로필 이미지 클릭
        binding.include.profile.setOnSingleClickListener {
            findNavController().navigate(R.id.action_bookmarkFragment_to_myPageFragment)
        }

        val categoryMap = mapOf(
            R.id.politics to 100,
            R.id.economy to 101,
            R.id.society to 102,
            R.id.culture to 103,
            R.id.world to 104,
            R.id.it to 105,
            R.id.entertainment to 106,
            R.id.sports to 107,
        )

        val categoryClickListener = View.OnClickListener { view ->

            lastSelectedView?.let {
                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }

            binding.all.isSelected = false
            binding.politics.isSelected = false
            binding.economy.isSelected = false
            binding.society.isSelected = false
            binding.culture.isSelected = false
            binding.world.isSelected = false
            binding.it.isSelected = false
            binding.entertainment.isSelected = false
            binding.sports.isSelected = false

            // 프로필 이미지 클릭
            binding.include.profile.setOnSingleClickListener {
                findNavController().navigate(R.id.action_bookmarkFragment_to_myPageFragment)
            }
            binding.include.bell.setOnSingleClickListener {
                val dialog = AlarmDialogFragment(binding.include)
                dialog.show(requireActivity().supportFragmentManager, "AlarmDialogFragment")
            }

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

            viewModel.getBookmarkNews(access_token)
            viewModel.bookmarkNewsResponse.observe(viewLifecycleOwner, Observer { bookmarkNewsResponse ->
                if (bookmarkNewsResponse.status == "OK") {
                    val data = bookmarkNewsResponse.bookmark
                    // 카테고리 분류
                    categoryGroup = data.groupBy { it.cate_id }

                    // 카테고리에 해당하는 북마크가 있으면 보여주고 없으면 안 보여줌
                    categoryGroup[category]?.let { data ->
                        origin_data = data
                        reverse_data = data.toList().reversed()
                        val sort_data = if (selectSortBtn?.text.toString() == "최신순") data else  data.reversed()
                        adapter.setData(sort_data)
                    } ?: run {
                        adapter.setData(emptyList())
                    }

                }

            })

        }

        // 전체 클릭
        binding.all.setOnSingleClickListener {

            lastSelectedView?.let {
                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }

            binding.all.isSelected = true
            binding.politics.isSelected = false
            binding.economy.isSelected = false
            binding.society.isSelected = false
            binding.culture.isSelected = false
            binding.world.isSelected = false
            binding.it.isSelected = false
            binding.entertainment.isSelected = false
            binding.sports.isSelected = false
            lastSelectedView = binding.all
            binding.all.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            viewModel.getBookmarkNews(access_token)
            viewModel.bookmarkNewsResponse.observe(viewLifecycleOwner, Observer { bookmarkNewsResponse ->
                if (bookmarkNewsResponse.status == "OK") {
                    val data = bookmarkNewsResponse.bookmark
                    origin_data = data
                    reverse_data = data.toList().reversed()

                    val sort_data = if (selectSortBtn?.text.toString() == "최신순") data else data.reversed()
                    adapter.setData(sort_data)

                }

            })


        }

        binding.politics.setOnSingleClickListener{categoryClickListener.onClick(it)}
        binding.economy.setOnSingleClickListener{categoryClickListener.onClick(it)}
        binding.society.setOnSingleClickListener{categoryClickListener.onClick(it)}
        binding.culture.setOnSingleClickListener{categoryClickListener.onClick(it)}
        binding.world.setOnSingleClickListener{categoryClickListener.onClick(it)}
        binding.it.setOnSingleClickListener{categoryClickListener.onClick(it)}
        binding.entertainment.setOnSingleClickListener{categoryClickListener.onClick(it)}
        binding.sports.setOnSingleClickListener{categoryClickListener.onClick(it)}

        // 카테고리 뉴스 클릭
        adapter.setListener {v, position ->
            val data = adapter.getItem(position)
            val bundle = Bundle().apply {
                putString("news_id", data.news_id)
            }
            findNavController().navigate(R.id.action_bookmarkFragment_to_newsContentFragment, bundle)
        }

        // 최신순
        binding.newText.setOnSingleClickListener {
            selectSortBtn = binding.newText

            if (lastSelectedView == null || lastSelectedView == binding.all) {
                origin_data?.let {
                    adapter.setData(it)
                }
            } else {
                val category = categoryMap[lastSelectedView?.id]
                categoryGroup[category]?.let { data ->
                    origin_data?.let {
                        adapter.setData(it)
                    }
                }
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

            if (lastSelectedView == null || lastSelectedView == binding.all) {
                reverse_data?.let {
                    adapter.setData(it)
                }
            } else {
                val category = categoryMap[lastSelectedView?.id]
                categoryGroup[category]?.let { data ->
                    reverse_data?.let {
                        adapter.setData(it)
                    }
                }
            }
            selectSortBtn?.let {
                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                binding.newText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
            binding.newText.isSelected = false
            binding.oldText.isSelected = true

        }

        // 북마크 클릭 (카테고리)
        adapter.setBookmarkClickListener(object : BookmarkAdapter.OnBookmarkClickListener {
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
            "전체" -> binding.all.performClick()
            "정치" -> binding.politics.performClick()
            "경제" -> binding.economy.performClick()
            "사회" -> binding.society.performClick()
            "생활/문화" -> binding.culture.performClick()
            "세계" -> binding.world.performClick()
            "IT/과학" -> binding.it.performClick()
            "연예" -> binding.entertainment.performClick()
            "스포츠" -> binding.sports.performClick()
        }

        when (selectSortBtn?.text.toString()) {
            "최신순" -> binding.newText.performClick()
            else -> binding.oldText.performClick()
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