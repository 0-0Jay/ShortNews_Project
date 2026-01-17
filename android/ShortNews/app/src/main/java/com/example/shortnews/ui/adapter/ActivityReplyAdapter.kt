package com.example.shortnews.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.shortnews.R
import com.example.shortnews.UserSharedPreferences
import com.example.shortnews.model.ActivityReplyItem

class ActivityReplyAdapter:RecyclerView.Adapter<ActivityReplyAdapter.NewsViewHolder>() {

    fun interface OnItemClickListener {
        fun onItemClick(v: View, position:Int)
    }
    private var listener: OnItemClickListener? = null

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }


    private var data:List<ActivityReplyItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.activity_reply_item, parent, false)
        return NewsViewHolder(view, listener)
    }

    // data의 개수를 알려줌
    override fun getItemCount(): Int {
        return data.size
    }

    // data 의 내용을 넣는 작업
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val item = data[position]

        // 뉴스 이미지
        val newsImage = item.imgs
        val defaultImage = "https://snewsimgs.s3.ap-northeast-2.amazonaws.com/news-default.png"

        Glide.with(holder.itemView.context)
            .load(newsImage)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .error(defaultImage)
            .into(holder.newsImg)

        // 뉴스 제목
        holder.newsTitle.text = item.title

        // 상위 댓글 프로필
        val id = item.id
        val profileImage =  "https://snewsuserprofile.s3.ap-northeast-2.amazonaws.com/${id}"
        val default_profileImage = "https://snewsuserprofile.s3.ap-northeast-2.amazonaws.com/default"

        Glide.with(holder.itemView.context)
            .load(profileImage)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .error(default_profileImage)
            .into(holder.profile1)

        // 상위 댓글 닉네임
        holder.nickname1.text = item.nickname
        // 상위 댓글 내용
        holder.content1.text = item.content
        // 상위 댓글 날짜
        val date = item.reply_id
        val formattedDate = "${date.substring(0, 4)}.${date.substring(4, 6)}.${date.substring(6, 8)}"
        holder.date1.text = formattedDate

        if (item.low_rid != null) {
            // 하위 댓글 프로필
            val id = item.low_uid
            val profileImage =  "https://snewsuserprofile.s3.ap-northeast-2.amazonaws.com/${id}"
            val default_profileImage = "https://snewsuserprofile.s3.ap-northeast-2.amazonaws.com/default"

            Glide.with(holder.itemView.context)
                .load(profileImage)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(default_profileImage)
                .into(holder.profile2)

            // 하위 댓글 닉네임
            holder.nickname2.text = item.low_nickname
            // 하위 댓글 내용
            holder.content2.text = item.low_content
            // 하위 댓글 날짜
            val date = item.low_rid
            val formattedDate = "${date.substring(0, 4)}.${date.substring(4, 6)}.${date.substring(6, 8)}"
            holder.date2.text = formattedDate

            holder.profile2.visibility = View.VISIBLE
            holder.nickname2.visibility = View.VISIBLE
            holder.content2.visibility = View.VISIBLE
            holder.date2.visibility = View.VISIBLE
        }

    }

    class NewsViewHolder(view: View, listener: OnItemClickListener?): RecyclerView.ViewHolder(view){

        val newsTitle: TextView = view.findViewById(R.id.newsTitle)
        val newsImg: ImageView = view.findViewById(R.id.newsImg)
        val profile1: ImageView = view.findViewById(R.id.profile1)
        val profile2: ImageView = view.findViewById(R.id.profile2)
        val nickname1: TextView = view.findViewById(R.id.nickname1)
        val nickname2: TextView = view.findViewById(R.id.nickname2)
        val content1: TextView = view.findViewById(R.id.content1)
        val content2: TextView = view.findViewById(R.id.content2)
        val date1: TextView = view.findViewById(R.id.date1)
        val date2: TextView = view.findViewById(R.id.date2)
        init {
            view.setOnClickListener {
                listener?.onItemClick(view, this.layoutPosition)
            }

        }
    }

    fun setData(data:List<ActivityReplyItem>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun getItem(position:Int): ActivityReplyItem {
        return data[position]
    }
}