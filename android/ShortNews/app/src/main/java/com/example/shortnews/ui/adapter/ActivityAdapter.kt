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
import com.example.shortnews.model.ActivityNewsItem
import com.example.shortnews.model.BookmarkNewsItem
import com.example.shortnews.model.NewsItem

class ActivityAdapter:RecyclerView.Adapter<ActivityAdapter.NewsViewHolder>() {

    fun interface OnItemClickListener {
        fun onItemClick(v:View, position:Int)
    }
    private var listener: OnItemClickListener? = null

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    // 북마크 클릭
    interface OnBookmarkClickListener {
        fun onBookmarkClick(position:Int)
    }
    private var bookmarkClickListener: OnBookmarkClickListener? = null

    fun setBookmarkClickListener(listener: OnBookmarkClickListener) {
        this.bookmarkClickListener = listener
    }

    private var data:List<ActivityNewsItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(view, listener, bookmarkClickListener)
    }

    // data의 개수를 알려줌
    override fun getItemCount(): Int {
        return data.size
    }

    // data 의 내용을 넣는 작업
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val item = data[position]

        val newsImage = item.imgs
        val defaultImage = "https://snewsimgs.s3.ap-northeast-2.amazonaws.com/news-default.png"

        Glide.with(holder.itemView.context)
            .load(newsImage)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .error(defaultImage)
            .into(holder.newsImg)

        holder.newsTitle.text = item.title
        holder.likeCount.text = item.like.toString()
        holder.dislikeCount.text = item.dislike.toString()
        holder.replyCount.text = item.reply.toString()
        holder.viewsCount.text = item.views.toString()

        if (item.bookmark == 1) {
            holder.bookmarkBtn.setImageResource(R.drawable.bookmark_yes)
        } else {
            holder.bookmarkBtn.setImageResource(R.drawable.bookmark_no)
        }
    }

    class NewsViewHolder(view: View, listener: OnItemClickListener?, bookmarkClickListener: OnBookmarkClickListener?): RecyclerView.ViewHolder(view){

        val newsTitle:TextView = view.findViewById(R.id.newsTitle)
        val newsImg:ImageView = view.findViewById(R.id.newsImg)
        val likeCount:TextView = view.findViewById(R.id.likeCount)
        val dislikeCount:TextView = view.findViewById(R.id.dislikeCount)
        val replyCount:TextView = view.findViewById(R.id.replyCount)
        val viewsCount:TextView = view.findViewById(R.id.viewsCount)
        val bookmarkBtn:ImageView = view.findViewById(R.id.bookmarkBtn)

        init {
            view.setOnClickListener {
                listener?.onItemClick(view, this.layoutPosition)
            }

            bookmarkBtn.setOnClickListener {
                bookmarkClickListener?.onBookmarkClick(this.layoutPosition)
            }
        }
    }

    fun setData(data:List<ActivityNewsItem>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun getItem(position:Int):ActivityNewsItem {
        return data[position]
    }



}
