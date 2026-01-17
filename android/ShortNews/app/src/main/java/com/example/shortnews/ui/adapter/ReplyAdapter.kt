package com.example.shortnews.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.shortnews.R
import com.example.shortnews.UserSharedPreferences
import com.example.shortnews.model.LikeReponse
import com.example.shortnews.model.NewsReply
import com.example.shortnews.setOnSingleClickListener
import com.example.shortnews.ui.news.ReportFragment

class ReplyAdapter() : RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder>() {
    private var data = mutableListOf<NewsReply>()
    private val likeResponse = MutableLiveData<LikeReponse>()

    var replyStates: MutableMap<String, Boolean> = mutableMapOf()

    // 좋아요 리스너
    fun interface OnItemClickListener {
        fun onItemClick(v:View, position:Int, lposition: Int?, buttonType: Int, replyType: Int)
    }
    private var listener: OnItemClickListener? = null

    fun setLikeListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    // 답글 등록 리스너
    fun interface OnReplyClickListener {
        fun onReplyClick(position: Int, text: String)
    }
    private var replyListener: OnReplyClickListener? = null
    fun setReplyListener(replyListener: OnReplyClickListener) {
        this.replyListener = replyListener
    }

    // 삭제 리스너
    fun interface OnReplyDeleteClickListener {
        fun onReplyDeleteClick(position: Int, lposition: Int?)
    }
    private var replyDeleteListener: OnReplyDeleteClickListener? = null

    fun setReplyDeleteListener(replyDeleteListener: OnReplyDeleteClickListener) {
        this.replyDeleteListener = replyDeleteListener
    }

    // 수정 리스너
    fun interface OnReplyUpdateClickListener {
        fun onReplyUpdateClick(position: Int, lposition: Int?, text: String)
    }
    private var replyUpdateListener: OnReplyUpdateClickListener? = null

    fun setReplyUpdateListener(replyUpdateListener: OnReplyUpdateClickListener) {
        this.replyUpdateListener = replyUpdateListener
    }

    // 신고 리스너
    fun interface OnReplyReportClickListener {
        fun onReplyReportClick(position: Int, lposition: Int?)
    }
    private var replyReportListener: OnReplyReportClickListener? = null

    fun setReplyReportListener(replyReportListener: OnReplyReportClickListener) {
        this.replyReportListener = replyReportListener
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.reply_item, parent,false)
        data.forEach { item ->
            replyStates[item.reply_id] = false
        }
        return ReplyViewHolder(view, listener, replyListener, replyDeleteListener, replyUpdateListener, replyReportListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        val item = data[position]

        val year = item.reply_id.substring(0, 4)
        val month = item.reply_id.substring(4, 6)
        val day = item.reply_id.substring(6, 8)
        val hour = item.reply_id.substring(8, 10)
        val min = item.reply_id.substring(10, 12)
        val profileImage = "https://snewsuserprofile.s3.ap-northeast-2.amazonaws.com/${item.id}"
        val defaultImage = "https://snewsuserprofile.s3.ap-northeast-2.amazonaws.com/default"
        val access_token = UserSharedPreferences.sharedPreferences.getString("access_token", null).toString()
        val id = UserSharedPreferences.sharedPreferences.getString("id", null).toString()


        holder.replyNickname.text = item.nickname
        holder.replyContent.text = item.content
        holder.likeCount.text = item.like.toString()
        holder.dislikeCount.text = item.hate.toString()
        holder.replyDate.text = "${year}.${month}.${day} ${hour}:${min}"
        holder.replyCount.text = "(${item.lower.size})"
        Glide.with(holder.itemView.context)
            .load(profileImage)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .error(defaultImage)
            .into(holder.replyProfile)

        if(item.type == 1) {
            holder.like.setImageResource(R.drawable.like)
            holder.dislike.setImageResource(R.drawable.default_dislike)
        } else if(item.type == -1) {
            holder.dislike.setImageResource(R.drawable.dislike)
            holder.like.setImageResource(R.drawable.default_like)
        } else if(item.type == 0) {
            holder.dislike.setImageResource(R.drawable.default_dislike)
            holder.like.setImageResource(R.drawable.default_like)
        }

        holder.isOpenRereply = replyStates[item.reply_id]!!

        if(holder.isOpenRereply == false) {
            holder.commentWrite.visibility = GONE
            holder.commentWriteButton.visibility = GONE
            holder.recyclerView.visibility = GONE
        }

        if(item.lower.isNotEmpty()) holder.getLowerReply(item.lower, listener, replyDeleteListener, replyUpdateListener, replyReportListener)

        holder.reReplyTitle.setOnSingleClickListener {
            holder.getLowerReply(item.lower, listener, replyDeleteListener, replyUpdateListener, replyReportListener)
            if(!replyStates[item.reply_id]!!) {
                holder.commentWrite.visibility = VISIBLE
                holder.commentWriteButton.visibility = VISIBLE
                holder.recyclerView.visibility = VISIBLE
                replyStates[item.reply_id] = true
                holder.isOpenRereply = true

            } else {
                holder.commentWrite.visibility = GONE
                holder.commentWriteButton.visibility = GONE
                holder.recyclerView.visibility = GONE
                replyStates[item.reply_id] = false
                holder.isOpenRereply = false
            }
        }
        holder.replyCount.setOnSingleClickListener {
            holder.getLowerReply(item.lower, listener, replyDeleteListener, replyUpdateListener, replyReportListener)
            if(!replyStates[item.reply_id]!!) {
                holder.commentWrite.visibility = VISIBLE
                holder.commentWriteButton.visibility = VISIBLE
                holder.recyclerView.visibility = VISIBLE
                replyStates[item.reply_id] = true
                holder.isOpenRereply = true

            } else {
                holder.commentWrite.visibility = GONE
                holder.commentWriteButton.visibility = GONE
                holder.recyclerView.visibility = GONE
                replyStates[item.reply_id] = false
                holder.isOpenRereply = false
            }
        }


        holder.threeDotsCardView.visibility = GONE
        holder.replyDeleteButton.visibility = GONE
        holder.replyUpdateButton.visibility = GONE
        holder.replyReportButton.visibility = GONE



        if(item.edited == 1) holder.replyUpdated.visibility = VISIBLE
        else holder.replyUpdated.visibility = GONE

        if(item.report > 0) {
            holder.replyBlindView.visibility = VISIBLE
        } else holder.replyBlindView.visibility = GONE

    }


    class ReplyViewHolder(view: View, listener: OnItemClickListener?, replyListener: OnReplyClickListener?, replyDeleteListener: OnReplyDeleteClickListener?, replyUpdateListener: OnReplyUpdateClickListener?, replyReportListener: OnReplyReportClickListener?): RecyclerView.ViewHolder(view) {
        val replyNickname:TextView = view.findViewById(R.id.replyNickname)
        val replyDate:TextView = view.findViewById(R.id.replyDate)
        val replyContent:TextView = view.findViewById(R.id.replyContent)
        val replyCount:TextView = view.findViewById(R.id.reReplyCount)
        var like:ImageView = view.findViewById(R.id.like)
        val likeCount:TextView = view.findViewById(R.id.likeCount)
        var dislike:ImageView = view.findViewById(R.id.dislike)
        val dislikeCount:TextView = view.findViewById(R.id.dislikeCount)
        var replyProfile:ImageView = view.findViewById(R.id.replyProfile)
        val reReplyTitle:TextView = view.findViewById(R.id.reReplyTitle)

        val commentWrite:TextView = view.findViewById(R.id.commentWrite)
        val commentWriteButton: Button = view.findViewById(R.id.commentWriteButton)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerview)
        var isOpenRereply = false

        var threeDotsCardView:CardView = view.findViewById(R.id.threeDotsCardView)
        var replyDeleteButton:Button = view.findViewById(R.id.replyDeleteButton)
        var replyUpdateButton:Button = view.findViewById(R.id.replyUpdateButton)
        var replyReportButton:Button = view.findViewById(R.id.replyReportButton)
        val threeDots:ImageView = view.findViewById(R.id.threeDots)
        var isOpenThreeDots = false

        var replyUpdateText:TextView = view.findViewById(R.id.replyUpdateText)
        var replyUpdateTextButton:Button = view.findViewById(R.id.replyUpdateTextButton)
        var replyUpdated:TextView = view.findViewById(R.id.replyUpdated)

        var replyBlindView:CardView = view.findViewById(R.id.replyBlindView)

        fun getLowerReply(rereply: List<NewsReply>, listener: OnItemClickListener?, replyDeleteListener: OnReplyDeleteClickListener?, replyUpdateListener: OnReplyUpdateClickListener?, replyReportListener: OnReplyReportClickListener?) {
            var sortedReplies = rereply
                .let { replies ->
                    replies.sortedByDescending { it.reply_id.toLongOrNull() ?: 0 }
                }

            val layoutManager = LinearLayoutManager(itemView.context, RecyclerView.VERTICAL, false)
            recyclerView.layoutManager = layoutManager
            var rereplyAdapter = RereplyAdapter(sortedReplies)
            recyclerView.adapter = rereplyAdapter
            Log.d("답댓글 값 전달", sortedReplies.toString())

            // 답글 좋아요 싫어요
            rereplyAdapter.setLikeListener {v, position, lposition, buttonType ->
                val tmp = if(lposition - rereply.size < 0) -(lposition - rereply.size) else lposition - rereply.size
                listener?.onItemClick(v, this.layoutPosition, tmp-1, buttonType, 2)
            }

            // 답글 삭제
            rereplyAdapter.setReplyDeleteListener { position, lposition ->
                val tmp = if(lposition - rereply.size < 0) -(lposition - rereply.size) else lposition - rereply.size
                replyDeleteListener?.onReplyDeleteClick(this.layoutPosition, tmp-1)
            }

            // 답글 수정
            rereplyAdapter.setReplyUpdateListener { position, lposition, text ->
                val tmp = if(lposition - rereply.size < 0) -(lposition - rereply.size) else lposition - rereply.size
                replyUpdateListener?.onReplyUpdateClick(this.layoutPosition, tmp-1, text)
            }

            // 답글 신고
            rereplyAdapter.setReplyReportListener { position, lposition ->
                val tmp = if(lposition - rereply.size < 0) -(lposition - rereply.size) else lposition - rereply.size
                replyReportListener?.onReplyReportClick(this.layoutPosition, tmp-1)
            }

        }

        init {
            like.setOnSingleClickListener {
                listener?.onItemClick(view, this.layoutPosition, null, 1, 1)
                Log.d("댓글 좋아요 layoutPosition", this.layoutPosition.toString())
            }
            dislike.setOnSingleClickListener {
                listener?.onItemClick(view, this.layoutPosition, null, -1, 1)
                Log.d("댓글 싫어요", this.layoutPosition.toString())
            }

            commentWriteButton.setOnSingleClickListener {
                Log.d("답댓글 요청", this.layoutPosition.toString())
                val replyText = commentWrite.text.toString()
                if(replyText.isNotEmpty()) {
                    replyListener?.onReplyClick(this.layoutPosition, replyText)
                    isOpenRereply = true
                    commentWrite.text = null
                }
            }
            threeDots.setOnSingleClickListener {
                val nickname = UserSharedPreferences.sharedPreferences.getString("nickname", null).toString()
                if(!isOpenThreeDots) {
                    if(replyNickname.text == nickname) {
                        replyDeleteButton.visibility = VISIBLE
                        replyUpdateButton.visibility = VISIBLE
                    } else replyReportButton.visibility = VISIBLE

                    threeDotsCardView.visibility = VISIBLE
                    isOpenThreeDots = true
                } else {
                    threeDotsCardView.visibility = GONE
                    replyDeleteButton.visibility = GONE
                    replyUpdateButton.visibility = GONE
                    replyReportButton.visibility = GONE
                    isOpenThreeDots = false
                }
            }
            replyDeleteButton.setOnSingleClickListener {
                Log.d("댓글 삭제 요청", this.layoutPosition.toString())
                replyDeleteListener?.onReplyDeleteClick(this.layoutPosition, null)
            }
            replyUpdateButton.setOnSingleClickListener {
                replyUpdateText.visibility = VISIBLE
                replyUpdateTextButton.visibility = VISIBLE
                replyContent.visibility = INVISIBLE
                threeDotsCardView.visibility = GONE
                replyUpdated.visibility = GONE

                replyUpdateText.text = replyContent.text
            }
            replyUpdateTextButton.setOnSingleClickListener {
                Log.d("댓글 수정 요청", this.layoutPosition.toString())
                val text = replyUpdateText.text.toString()
                if(text != "") {
                    replyUpdateListener?.onReplyUpdateClick(this.layoutPosition, null, text)
                    replyContent.text = replyUpdateText.text
                    replyContent.visibility = VISIBLE
                    replyUpdateText.visibility = INVISIBLE
                    replyUpdateTextButton.visibility = INVISIBLE
                }
            }
            replyReportButton.setOnSingleClickListener {
                replyReportListener?.onReplyReportClick(this.layoutPosition, null)
                replyReportButton.visibility = GONE
                isOpenThreeDots = false
            }
            replyBlindView.setOnSingleClickListener {
                replyBlindView.visibility = GONE
            }

        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data:MutableList<NewsReply>) {
        this.data = data
        Log.d("댓글 data 응답", data.toString())
        notifyDataSetChanged()
    }

    fun getItem(position: Int):NewsReply {
        return data[position]
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData() {
        notifyDataSetChanged()
    }

    fun updateLike(position: Int, like: Int, dislike: Int, type: Int) {
        this.data[position].like = like
        this.data[position].hate = dislike
        this.data[position].type = type
        setReplyClose()
        this.data[position].isOpenRereply = this.replyStates[this.data[position].reply_id]
        notifyItemChanged(position)
//        notifyDataSetChanged()
        Log.d("댓글 updateLike", replyStates.toString())
    }

    fun updateRereplyLike(position: Int, lposition: Int, like: Int, dislike: Int, type: Int) {
        this.data[position].lower[lposition].like = like
        this.data[position].lower[lposition].hate = dislike
        this.data[position].lower[lposition].type = type
        setReplyClose()
        this.data[position].isOpenRereply = this.replyStates[this.data[position].reply_id]
        notifyItemChanged(position)
        Log.d("답댓글 updateLike", this.data[position].lower[lposition].toString())
    }

    fun setReplyClose() {
        val newData = data.map { it.copy(isOpenRereply = false) }
        data.clear()
        data.addAll(newData)
//        notifyDataSetChanged()
        notifyItemRangeChanged(0, data.count())
    }

}