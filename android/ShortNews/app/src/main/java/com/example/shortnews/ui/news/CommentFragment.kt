package com.example.shortnews.ui.news

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shortnews.R
import com.example.shortnews.UserSharedPreferences
import com.example.shortnews.databinding.FragmentCommentBinding
import com.example.shortnews.databinding.FragmentNewsContentBinding
import com.example.shortnews.model.NewsReply
import com.example.shortnews.setOnSingleClickListener
import com.example.shortnews.ui.adapter.ReplyAdapter

interface OnCommentDialogCloseListener {
    fun onCommentDialogClosed()
}
class CommentFragment : DialogFragment() {
    private var onCommentDialogCloseListener: OnCommentDialogCloseListener? = null

    fun setOnCommentDialogCloseListener(listener: OnCommentDialogCloseListener) {
        this.onCommentDialogCloseListener = listener
    }

    private var _binding: FragmentCommentBinding?=null
    private val binding get() = _binding!!

    private val commentViewModel:CommentViewModel by viewModels()
    val newsContentViewModel: NewsContentViewModel by viewModels()
    val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView)
    private val adapter = ReplyAdapter()

    var firstLoad = true

    var reply_count = UserSharedPreferences.sharedPreferences.getString("replyCount", null)

    val news_id = arguments?.getString("news_id").toString()
    val access_token = arguments?.getString("access_token").toString()

    companion object {
        fun create(access_token:String, news_id: String): CommentFragment {
            val fragment = CommentFragment()
            val args = Bundle()
            args.putString("access_token", access_token)
            args.putString("news_id", news_id)
            fragment.arguments = args
            return fragment
        }
    }



    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommentBinding.inflate(inflater, container, false)
        val news = FragmentNewsContentBinding.inflate(inflater, container, false)

        if(firstLoad) {
            binding.recyclerView.adapter = adapter

            val manager = LinearLayoutManager(context)
            binding.recyclerView.layoutManager = manager

            val news_id = arguments?.getString("news_id").toString()
            val access_token = arguments?.getString("access_token").toString()

            commentViewModel.getNewsReply(access_token, news_id, 1)

            commentViewModel.replyList.observe(viewLifecycleOwner) { reply ->
                val data = mutableListOf<NewsReply>()
                if (reply != null) {
                    data.addAll(reply)
                }
                binding.commentCount.text = "(${reply_count})"
                if(data.isNotEmpty()) {
                    adapter.setData(data)
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.replyZeroView.visibility = View.INVISIBLE
                }
                else {
                    binding.recyclerView.visibility = View.INVISIBLE
                    binding.replyZeroView.visibility = View.VISIBLE
                }
            }

            firstLoad = false
        }

        binding.replyClose.setOnClickListener{
            val editor = UserSharedPreferences.sharedPreferences.edit()
            editor.putString("replyCount", reply_count)
            editor.apply()
            dialog?.dismiss()
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 프로필 이미지 세팅
        val profileImage =  UserSharedPreferences.sharedPreferences.getString("profileImage", null)
        val defaultImage = "https://snewsuserprofile.s3.ap-northeast-2.amazonaws.com/default"

        val news_id = arguments?.getString("news_id").toString()
        val access_token = arguments?.getString("access_token").toString()
        binding.sortDateButton.isSelected = true
        binding.sortDateButton.setTextColor(Color.WHITE)

        // 최신순 정렬 버튼 클릭
        binding.sortDateButton.setOnClickListener {
            commentViewModel.getNewsReply(access_token, news_id, 1)
            binding.sortDateButton.isSelected = true
            binding.sortDateButton.setTextColor(Color.WHITE)
            binding.sortLikeButton.isSelected = false
            binding.sortLikeButton.setTextColor(Color.BLACK)
        }
        // 좋아요순 정렬 버튼 클릭
        binding.sortLikeButton.setOnSingleClickListener {
            commentViewModel.getNewsReply(access_token, news_id, -1)
            binding.sortDateButton.isSelected = false
            binding.sortDateButton.setTextColor(Color.BLACK)
            binding.sortLikeButton.isSelected = true
            binding.sortLikeButton.setTextColor(Color.WHITE)
        }

        // 댓글 등록 버튼 클릭
        binding.commentWriteButton.setOnSingleClickListener {
            val reply = binding.commentWrite.text.toString()
            if(reply.isNotEmpty()) {
                reply_count = ((reply_count?.toInt() ?: 0) + 1).toString()
                if(binding.sortDateButton.isSelected)
                    commentViewModel.createReply(access_token, 1, news_id, reply, null, null)
                else if(binding.sortLikeButton.isSelected)
                    commentViewModel.createReply(access_token, -1, news_id, reply, null, null)
                    adapter.setReplyClose()
            }
            binding.commentWrite.text = null
        }

        // 답댓글 등록
        adapter.setReplyListener { position, text ->
            val data = adapter.getItem(position)
            commentViewModel.createReply(access_token, 1, news_id, text, data.reply_id, data.id)
            reply_count = ((reply_count?.toInt() ?: 0) + 1).toString()
        }

        // 댓글 좋아요 싫어요
        adapter.setLikeListener { v, position, lposition, buttonType, replyType ->
            val data = if(lposition == null) adapter.getItem(position) else adapter.getItem(position).lower[lposition]
            var original_type = data.type
            var like = data.like
            var dislike = data.hate
            var type = original_type

            // 해제 상태면 좋아요, 좋아요 상태면 해제
            if (original_type == 0) {
                type = buttonType
            } else if (original_type == buttonType) {
                type = 0    // 해제
            } else {
                type = -type
            }

            Log.d("댓글 클릭", data.toString())
            newsContentViewModel.like(access_token, type, data.news_id, data.reply_id)
            newsContentViewModel.likeReponse.observe(viewLifecycleOwner) { likeResponse ->
                if (likeResponse.status == "OK") {
                    Log.d("댓글 응답 ㅇㅋ", likeResponse.toString())
                    when (type) {
                        1 -> {
                            if (original_type == -1) {
                                like += 1
                                dislike = if (dislike > 0) {
                                    dislike - 1
                                } else {
                                    0
                                }
                            } else if (original_type == 0) {
                                like += 1
                            }
                        }

                        -1 -> {
                            if (original_type == 1) {
                                like = if (like > 0) {
                                    like - 1
                                } else {
                                    0
                                }
                                dislike += 1
                            } else if (original_type == 0) {
                                dislike += 1
                            }
                        }

                        else -> {
                            if (original_type == 1) {
                                like = if (like > 0) {
                                    like - 1
                                } else {
                                    0
                                }
                            } else if (original_type == -1) {
                                dislike = if (dislike > 0) {
                                    dislike - 1
                                } else {
                                    0
                                }
                            }
                        }
                    }
                    original_type = type
                    if(lposition == null) adapter.updateLike(position, like, dislike, type)
                    else adapter.updateRereplyLike(position, lposition, like, dislike, type)
                }
            }
        }

        // 댓글 삭제
        adapter.setReplyDeleteListener { position, lposition ->
            val data = if(lposition == null) adapter.getItem(position) else adapter.getItem(position).lower[lposition]
            if(binding.sortDateButton.isSelected)
                commentViewModel.deleteReply(access_token, 1, data.news_id, data.reply_id)
            else commentViewModel.deleteReply(access_token, -1, data.news_id, data.reply_id)
            reply_count = ((reply_count?.toInt() ?: 0) - 1).toString()
            adapter.updateData()

        }

        // 댓글 수정
        adapter.setReplyUpdateListener { position, lposition, text ->
            val data = if(lposition == null) adapter.getItem(position) else adapter.getItem(position).lower[lposition]
            if(binding.sortDateButton.isSelected)
                commentViewModel.updateReply(access_token, 1, data.news_id, data.reply_id, text)
            else commentViewModel.updateReply(access_token, -1, data.news_id, data.reply_id, text)
        }

        // 댓글 신고
        adapter.setReplyReportListener { position, lposition ->
            val data = if(lposition == null) adapter.getItem(position) else adapter.getItem(position).lower[lposition]
            val dialog = ReportFragment.create(data.news_id, data.reply_id)
            dialog.show(requireActivity().supportFragmentManager, "ReportFragment")
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}