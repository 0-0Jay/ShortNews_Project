package com.example.shortnews.ui.news

import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Intent
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shortnews.R
import com.example.shortnews.UserSharedPreferences
import com.example.shortnews.databinding.FragmentNewsContentBinding
import com.example.shortnews.setOnSingleClickListener
import com.example.shortnews.ui.adapter.ImageSlideAdapter
import com.example.shortnews.ui.adapter.SourceAdapter
import com.example.shortnews.ui.member.LoginViewModel
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.ItemContent
import com.kakao.sdk.template.model.ItemInfo
import com.kakao.sdk.template.model.Link
import com.kakao.sdk.template.model.Social
import com.kakao.sdk.template.model.TextTemplate
import com.launchdarkly.eventsource.ConnectStrategy
import com.launchdarkly.eventsource.EventSource
import com.launchdarkly.eventsource.MessageEvent
import com.launchdarkly.eventsource.background.BackgroundEventHandler
import com.launchdarkly.eventsource.background.BackgroundEventSource
import okhttp3.Headers
import java.net.URL
import java.util.concurrent.TimeUnit

class NewsContentFragment : Fragment(), BackgroundEventHandler {

    private var _binding: FragmentNewsContentBinding?=null
    private val binding get() = _binding!!
    val newsContentViewModel: NewsContentViewModel by viewModels()
    val newsMainViewModel: NewsMainViewModel by viewModels()
    private val imageSlideAdapter = ImageSlideAdapter()
    private val sourceAdapter = SourceAdapter()
    private val access_token = UserSharedPreferences.getAccessToken()
    private var mediaPlayer: MediaPlayer? = null
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
        _binding = FragmentNewsContentBinding.inflate(inflater, container, false)

        // 알림
        this.SSEConnect = BackgroundEventSource.Builder(this, EventSource.Builder(this.Info))
        this.SSE = this.SSEConnect!!.build()
        this.SSE?.start()
        newsContentViewModel.checkAlarm(access_token, binding)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 로고 클릭
        binding.include.logo.setOnSingleClickListener {
            findNavController().navigate(R.id.action_newsContentFragment_to_newsMainFragment)
        }
        binding.include.bell.setOnSingleClickListener {
            val dialog = AlarmDialogFragment(binding.include)
            dialog.show(requireActivity().supportFragmentManager, "AlarmDialogFragment")
        }

        binding.imageSlide.setClipToPadding(false);
        binding.imageSlide.setClipChildren(false);
        binding.imageSlide.setOffscreenPageLimit(3);

        // 프로필 이미지 세팅
        val profileImage =  UserSharedPreferences.sharedPreferences.getString("profileImage", null)
        val defaultImage = "https://snewsuserprofile.s3.ap-northeast-2.amazonaws.com/default"

        Glide.with(this)
            .load(profileImage)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .error(defaultImage)
            .into(binding.include.profile)

        val news_id = arguments?.getString("news_id").toString()
        var original_type = 0
        var like = 0
        var dislike = 0
        var bookmark = 0

        // 출처 세팅
        newsContentViewModel.source(access_token, news_id)
        newsContentViewModel.sourceResponse.observe(viewLifecycleOwner, Observer { sourceResponse ->
            if (sourceResponse.status == "OK") {

                binding.RecyclerView.adapter = sourceAdapter
                val manager = LinearLayoutManager(context)
                binding.RecyclerView.layoutManager = manager

                val source_list:List<String> = sourceResponse.link.split(",")
                sourceAdapter.setData(source_list)
            }
        })

        binding.imageSlide.adapter = imageSlideAdapter

        newsContentViewModel.selectNews(access_token, news_id)
        newsContentViewModel.select_news.observe(viewLifecycleOwner, Observer { select_news ->

            binding.newsTitle.text = select_news.news.title
            binding.newsContent.text = select_news.news.content
            imageSlideAdapter.setData(select_news.news.imgs)
            binding.likeCount.text = select_news.news.like.toString()
            binding.dislikeCount.text = select_news.news.dislike.toString()
            binding.viewsCount.text = (select_news.news.views + 1).toString()

            val editor = UserSharedPreferences.sharedPreferences.edit()
            editor.putString("replyCount", select_news.news.reply.toString())
            editor.apply()


            if (select_news.news.type == 1) {
                binding.like.setImageResource(R.drawable.like)
            } else if (select_news.news.type == -1){
                binding.dislike.setImageResource(R.drawable.dislike)
            }

            if (select_news.news.bookmark == 1) {
                binding.bookmarkBtn.setImageResource(R.drawable.bookmark_yes)
                bookmark = 1
            } else {
                binding.bookmarkBtn.setImageResource(R.drawable.bookmark_no)
                bookmark = 0
            }

            original_type = select_news.news.type
            like = select_news.news.like
            dislike = select_news.news.dislike

        })

        val likeType = mapOf(
            R.id.like to 1,
            R.id.dislike to -1
        )

        val likeClickListener = View.OnClickListener { view ->
            val access_token = UserSharedPreferences.sharedPreferences.getString("access_token", null).toString()
            var type = original_type
            // 해제 상태면 좋아요, 좋아요 상태면 해제
            if (original_type == 0) {
                type = likeType[view.id]!!
            } else if (original_type == likeType[view.id]!!) {
                type = 0    // 해제
            } else {
                type = -type
            }

            newsContentViewModel.like(access_token, type, news_id, null)

            newsContentViewModel.likeReponse.observe(viewLifecycleOwner, Observer { likeReponse ->
                if (likeReponse.status == "OK") {

                    when (type) {
                        1 -> {
                            binding.like.setImageResource(R.drawable.like)
                            binding.dislike.setImageResource(R.drawable.default_dislike)
                            if (original_type == -1) {  // -1 -> 1
                                binding.likeCount.text = like.plus(1).toString()
                                like += 1
                                dislike = if (dislike > 0) {
                                    dislike - 1
                                } else {
                                    0
                                }
                                binding.dislikeCount.text = dislike.toString()
                            } else if (original_type == 0) {    // 0 -> 1
                                binding.likeCount.text = like.plus(1).toString()
                                like += 1
                                binding.dislikeCount.text = dislike.toString()
                            }
                        }
                        -1 -> {
                            binding.dislike.setImageResource(R.drawable.dislike)
                            binding.like.setImageResource(R.drawable.default_like)
                            if (original_type == 1) {  // 1 -> -1
                                like = if (like > 0) {
                                    like - 1
                                } else {
                                    0
                                }
                                binding.likeCount.text = like.toString()
                                binding.dislikeCount.text = dislike.plus(1).toString()
                                dislike += 1
                            } else if (original_type == 0) {  // 0 -> -1
                                binding.likeCount.text = like.toString()
                                binding.dislikeCount.text = dislike.plus(1).toString()
                                dislike += 1
                            }
                        }
                        else -> {
                            if (original_type == 1) {  // 1 -> 0
                                like = if (like > 0) {
                                    like - 1
                                } else {
                                    0
                                }
                                binding.likeCount.text = like.toString()
                                binding.dislikeCount.text = dislike.toString()
                            } else if (original_type == -1) {  // -1 -> 0
                                dislike = if (dislike > 0) {
                                    dislike - 1
                                } else {
                                    0
                                }
                                binding.likeCount.text = like.toString()
                                binding.dislikeCount.text = dislike.toString()
                            }
                            binding.like.setImageResource(R.drawable.default_like)
                            binding.dislike.setImageResource(R.drawable.default_dislike)
                        }
                    } // when
                    original_type = type
                }
            })
        } // likeClickListener

        // 프로필 이미지 클릭
        binding.include.profile.setOnSingleClickListener {
            findNavController().navigate(R.id.action_newsContentFragment_to_myPageFragment)
        }

        binding.like.setOnSingleClickListener{likeClickListener.onClick(it)}
        binding.dislike.setOnSingleClickListener{likeClickListener.onClick(it)}

        binding.reply.setOnSingleClickListener{
            val dialog = CommentFragment.create(
                access_token,
                news_id
            )

            dialog.show(requireActivity().supportFragmentManager, "CommentFragment")

        }

        // 북마크 버튼 클릭
        binding.bookmarkBtn.setOnSingleClickListener {
            var type = false
            if (bookmark == 1) type = true

            newsMainViewModel.bookmark(access_token, news_id, type)
            newsMainViewModel.bookmarkResponse.observe(viewLifecycleOwner, Observer { bookmarkResponse ->
                if (bookmarkResponse.status == "OK") {
                    bookmark = if (type) 0 else 1

                    if (bookmark == 1) {
                        binding.bookmarkBtn.setImageResource(R.drawable.bookmark_yes)
                    } else if (bookmark == 0){
                        binding.bookmarkBtn.setImageResource(R.drawable.bookmark_no)
                    }
                }
            })
        }

        // 출처 버튼 클릭
        binding.source.setOnSingleClickListener {
            if (binding.sourceView.visibility == View.INVISIBLE) {
                binding.sourceView.visibility = View.VISIBLE
            } else {
                binding.sourceView.visibility = View.INVISIBLE
            }
        }


        // 출처 URL 클릭
        sourceAdapter.setListener {v, position ->
            val url = sourceAdapter.getItem(position)
            val webpage = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }
        }

        fun setPlaybackSpeed(player: MediaPlayer?, speed: Float) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val params = PlaybackParams()
                params.speed = speed
                player?.playbackParams = params
            }
        }

        binding.play.setOnSingleClickListener {
            val model = UserSharedPreferences.sharedPreferences.getString("model", null).toString()
            val speed = UserSharedPreferences.sharedPreferences.getString("speed", null).toString()
            val src = "https://snewstts.s3.ap-northeast-2.amazonaws.com/${news_id}${model}.mp3"

            Log.d("tts 응답", src)
            Log.d("tts 응답 속도", speed)
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()

                mediaPlayer?.setDataSource(src)

                mediaPlayer?.setOnPreparedListener {
                    setPlaybackSpeed(mediaPlayer, speed.toFloat())
                    mediaPlayer?.start()
                }

                mediaPlayer?.setOnCompletionListener {
                    mediaPlayer?.release()
                    mediaPlayer = null
                    binding.play.setImageResource(R.drawable.play)
                    Log.d("tts 끝 응답", src)
                }

                binding.play.setImageResource(R.drawable.stop)

                mediaPlayer?.prepareAsync()
            } else {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                    binding.play.setImageResource(R.drawable.play)
                } else {
                    mediaPlayer?.start()
                    binding.play.setImageResource(R.drawable.stop)
                }
            }
        }

        binding.newsThreeDots.setOnSingleClickListener {
            if(binding.newsReportButton.visibility == VISIBLE)
                binding.newsReportButton.visibility = GONE
            else binding.newsReportButton.visibility = VISIBLE
        }

        binding.newsReportButton.setOnSingleClickListener {
            val dialog = ReportFragment.create(news_id, "")
            dialog.show(requireActivity().supportFragmentManager, "ReportFragment")
            binding.newsReportButton.visibility = GONE
        }


        fun createText(): TextTemplate {

            val kakaoNativeAppKey =  resources.getString(R.string.kakao_native_key)
            KakaoSdk.init(requireContext(), kakaoNativeAppKey)
            val temp = "[${binding.newsTitle.text.toString()}]\n\n" + binding.newsContent.text.toString().trimIndent()

            val defaultText = TextTemplate(
                text = temp,
                link = Link(
                    webUrl = "http://www.shortnews.kr/selectNews/$news_id",
                    mobileWebUrl = "http://www.shortnews.kr/selectNews/$news_id"
                ),
                buttons = listOf(
                    Button(
                        "웹으로 보기",
                        Link(
                            webUrl = "http://www.shortnews.kr/selectNews/$news_id",
                            mobileWebUrl = "http://www.shortnews.kr/selectNews/$news_id"
                        )
                    )
                )
            )

            return defaultText
        }

        // 공유하기 클릭
        binding.share.setOnSingleClickListener {

            val defaultText = createText()
            // 피드 메시지 보내기
            // 카카오톡 설치여부 확인
            if (ShareClient.instance.isKakaoTalkSharingAvailable(requireContext())) {
                // 카카오톡으로 카카오톡 공유 가능
                ShareClient.instance.shareDefault(requireContext(), defaultText) { sharingResult, error ->
                    if (error != null) {
                        Log.e(TAG, "카카오톡 공유 실패", error)
                    }
                    else if (sharingResult != null) {
                        Log.d(TAG, "카카오톡 공유 성공 ${sharingResult.intent}")
                        startActivity(sharingResult.intent)

                        // 카카오톡 공유에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                        Log.w(TAG, "Warning Msg: ${sharingResult.warningMsg}")
                        Log.w(TAG, "Argument Msg: ${sharingResult.argumentMsg}")
                    }
                }
            } else {
                // 카카오톡 미설치: 웹 공유 사용 권장
                // 웹 공유 예시 코드
                val sharerUrl = WebSharerClient.instance.makeDefaultUrl(defaultText)

                // CustomTabs으로 웹 브라우저 열기

                // 1. CustomTabsServiceConnection 지원 브라우저 열기
                // ex) Chrome, 삼성 인터넷, FireFox, 웨일 등
                try {
                    KakaoCustomTabsClient.openWithDefault(requireContext(), sharerUrl)
                } catch(e: UnsupportedOperationException) {
                    // CustomTabsServiceConnection 지원 브라우저가 없을 때 예외처리
                }

                // 2. CustomTabsServiceConnection 미지원 브라우저 열기
                // ex) 다음, 네이버 등
                try {
                    KakaoCustomTabsClient.open(requireContext(), sharerUrl)
                } catch (e: ActivityNotFoundException) {
                    // 디바이스에 설치된 인터넷 브라우저가 없을 때 예외처리
                }
            }
        }

    }

        override fun onDestroyView() {
            super.onDestroyView()
            mediaPlayer?.release()
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