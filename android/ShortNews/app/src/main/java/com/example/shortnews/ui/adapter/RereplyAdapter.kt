package com.example.shortnews.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.shortnews.R
import com.example.shortnews.UserSharedPreferences
import com.example.shortnews.model.NewsReply
import com.example.shortnews.setOnSingleClickListener

class RereplyAdapter(private val data:List<NewsReply>) : RecyclerView.Adapter<RereplyAdapter.RereplyViewHolder>() {

    // 답글 좋아요 리스너
    fun interface OnItemClickListener {
        fun onItemClick(v:View, position:Int, lposition: Int, buttonType: Int)
    }
    private var listener: OnItemClickListener? = null

    fun setLikeListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    // 답글 삭제 리스너
    fun interface OnReplyDeleteClickListener {
        fun onReplyDeleteClick(position: Int, lposition: Int)
    }
    private var replyDeleteListener: OnReplyDeleteClickListener? = null

    fun setReplyDeleteListener(replyDeleteListener: OnReplyDeleteClickListener) {
        this.replyDeleteListener = replyDeleteListener
    }

    // 답글 수정 리스너
    fun interface OnReplyUpdateClickListener {
        fun onReplyUpdateClick(position: Int, lposition: Int, text: String)
    }
    private var replyUpdateListener: OnReplyUpdateClickListener? = null

    fun setReplyUpdateListener(replyUpdateListener: OnReplyUpdateClickListener) {
        this.replyUpdateListener = replyUpdateListener
    }

    // 답글 신고 리스너
    fun interface OnReplyReportClickListener {
        fun onReplyReportClick(position: Int, lposition: Int)
    }
    private var replyReportListener: OnReplyReportClickListener? = null

    fun setReplyReportListener(replyReportListener: OnReplyReportClickListener) {
        this.replyReportListener = replyReportListener
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RereplyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.reply_item, parent, false)
        return RereplyViewHolder(itemView, listener, replyDeleteListener, replyUpdateListener, replyReportListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RereplyViewHolder, position: Int) {
        val item = data[position]

        val year = item.reply_id.substring(0, 4)
        val month = item.reply_id.substring(4, 6)
        val day = item.reply_id.substring(6, 8)
        val hour = item.reply_id.substring(8, 10)
        val min = item.reply_id.substring(10, 12)
        val profileImage = "https://snewsuserprofile.s3.ap-northeast-2.amazonaws.com/${item.id}"
        val defaultImage = "https://snewsuserprofile.s3.ap-northeast-2.amazonaws.com/default"
        val id = UserSharedPreferences.sharedPreferences.getString("id", null).toString()


        holder.reReplyTitle.visibility = GONE
        holder.replyCount.visibility = GONE

        holder.replyNickname.text = item.nickname
        holder.replyContent.text = item.content
        holder.likeCount.text = item.like.toString()
        holder.dislikeCount.text = item.hate.toString()
        holder.replyDate.text = "${year}.${month}.${day} ${hour}:${min}"
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

        holder.commentWrite.visibility = GONE
        holder.commentWriteButton.visibility = GONE
        holder.recyclerView.visibility = GONE
        holder.line.visibility = GONE
        val namelayoutParams = holder.replyNickname.layoutParams
        namelayoutParams.width = 140.dpToPx(holder.itemView.context) // dp를 픽셀로 변환하여 적용합니다.
        holder.replyNickname.layoutParams = namelayoutParams

        holder.threeDotsCardView.visibility = GONE
        holder.replyDeleteButton.visibility = GONE
        holder.replyUpdateButton.visibility = GONE
        holder.replyReportButton.visibility = GONE

        if(item.id == id) holder.isMyReply = true

        if(item.edited == 1) holder.replyUpdated.visibility = View.VISIBLE
        else holder.replyUpdated.visibility = GONE

        if(item.report > 0) {
            holder.replyBlindView.visibility = View.VISIBLE
        } else holder.replyBlindView.visibility = GONE

        val updateLayoutParams = holder.replyUpdateText.layoutParams
        updateLayoutParams.width = 270.dpToPx(holder.itemView.context)
        holder.replyUpdateText.layoutParams = updateLayoutParams

        val blindLayoutParams = holder.replyBlindView.layoutParams
        blindLayoutParams.width = 290.dpToPx(holder.itemView.context)
        holder.replyBlindView.layoutParams = blindLayoutParams

        holder.replyBlindText.textSize = 14f

    }
    fun Int.dpToPx(context: Context): Int {
        val scale = context.resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }


    class RereplyViewHolder(view: View, listener: OnItemClickListener?, replyDeleteListener: OnReplyDeleteClickListener?, replyUpdateListener: OnReplyUpdateClickListener?, replyReportListener: OnReplyReportClickListener?): RecyclerView.ViewHolder(view) {
        var replyNickname: TextView = view.findViewById(R.id.replyNickname)
        val replyDate: TextView = view.findViewById(R.id.replyDate)
        val replyContent: TextView = view.findViewById(R.id.replyContent)
        val replyCount: TextView = view.findViewById(R.id.reReplyCount)
        var like: ImageView = view.findViewById(R.id.like)
        val likeCount: TextView = view.findViewById(R.id.likeCount)
        var dislike: ImageView = view.findViewById(R.id.dislike)
        val dislikeCount: TextView = view.findViewById(R.id.dislikeCount)
        var replyProfile: ImageView = view.findViewById(R.id.replyProfile)
        val reReplyTitle:TextView = view.findViewById(R.id.reReplyTitle)

        val commentWrite:TextView = view.findViewById(R.id.commentWrite)
        val commentWriteButton: Button = view.findViewById(R.id.commentWriteButton)
        val line:View = view.findViewById(R.id.horizontal_line1)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerview)

        var isMyReply = false

        var threeDotsCardView: CardView = view.findViewById(R.id.threeDotsCardView)
        var replyDeleteButton:Button = view.findViewById(R.id.replyDeleteButton)
        var replyUpdateButton:Button = view.findViewById(R.id.replyUpdateButton)
        var replyReportButton:Button = view.findViewById(R.id.replyReportButton)
        val threeDots:ImageView = view.findViewById(R.id.threeDots)
        var isOpenThreeDots = false

        var replyUpdateText:TextView = view.findViewById(R.id.replyUpdateText)
        var replyUpdateTextButton:Button = view.findViewById(R.id.replyUpdateTextButton)
        var replyUpdated:TextView = view.findViewById(R.id.replyUpdated)

        var replyBlindView:CardView = view.findViewById(R.id.replyBlindView)
        var replyBlindText:TextView = view.findViewById(R.id.replyBlindText)


        init {
            like.setOnSingleClickListener {
                listener?.onItemClick(view, oldPosition, this.layoutPosition, 1)
                Log.d("답댓글 좋아요 adapterPosition", this.adapterPosition.toString())
                Log.d("답댓글 좋아요 layoutPosition", this.layoutPosition.toString())
            }
            dislike.setOnSingleClickListener {
                listener?.onItemClick(view, oldPosition, this.layoutPosition, -1)
                Log.d("답댓글 싫어요", adapterPosition.toString())
            }
            threeDots.setOnSingleClickListener {
                if(!isOpenThreeDots) {
                    if(isMyReply) {
                        replyDeleteButton.visibility = View.VISIBLE
                        replyUpdateButton.visibility = View.VISIBLE
                    } else replyReportButton.visibility = View.VISIBLE

                    threeDotsCardView.visibility = View.VISIBLE
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
                replyDeleteListener?.onReplyDeleteClick(oldPosition, this.layoutPosition)
                Log.d("답댓글 삭제", this.layoutPosition.toString())
            }
            replyUpdateButton.setOnSingleClickListener {
                replyUpdateText.visibility = View.VISIBLE
                replyUpdateTextButton.visibility = View.VISIBLE
                replyContent.visibility = View.INVISIBLE
                threeDotsCardView.visibility = GONE

                replyUpdateText.text = replyContent.text
            }
            replyUpdateTextButton.setOnSingleClickListener {
                Log.d("댓글 수정 요청", this.layoutPosition.toString())
                val text = replyUpdateText.text.toString()
                if(text != "") {
                    replyUpdateListener?.onReplyUpdateClick(oldPosition, this.layoutPosition, text)
                    replyContent.text = replyUpdateText.text
                    replyContent.visibility = View.VISIBLE
                    replyUpdateText.visibility = View.INVISIBLE
                    replyUpdateTextButton.visibility = View.INVISIBLE
                }
            }
            replyReportButton.setOnSingleClickListener {
                replyReportListener?.onReplyReportClick(oldPosition, this.layoutPosition)
                replyReportButton.visibility = GONE
                isOpenThreeDots = false
            }
            replyBlindView.setOnSingleClickListener {
                replyBlindView.visibility = GONE
            }
        }


    }

}