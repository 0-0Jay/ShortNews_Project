package com.example.shortnews.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shortnews.R
import com.example.shortnews.model.RecommendItem

class RecommendNewsAdapter:RecyclerView.Adapter<RecommendNewsAdapter.RecommendNewsViewHolder>() {
    fun interface OnItemClickListener {
        fun onItemClick(v:View, position:Int)
    }
    private var listener: OnItemClickListener? = null

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    private var data:MutableList<RecommendItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendNewsViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.news_item, parent, false)
        return RecommendNewsViewHolder(view, listener)
    }

    // data의 개수를 알려줌
    override fun getItemCount(): Int {
        return data.size
    }

    // data 의 내용을 넣는 작업
    override fun onBindViewHolder(holder: RecommendNewsViewHolder, position: Int) {
        val item = data[position]

        val newsImage = item.imgs
        val defaultImage = "https://snewsimgs.s3.ap-northeast-2.amazonaws.com/news-default.png"

        Glide.with(holder.itemView.context)
            .load(newsImage)
            .error(defaultImage)
            .into(holder.newsImg)

        holder.newsTitle.text = item.title
        holder.likeCount.text = item.like.toString()
        holder.dislikeCount.text = item.dislike.toString()
        holder.replyCount.text = item.reply.toString()
        holder.viewsCount.text = item.views.toString()

        holder.bookmarkBtn.visibility = View.INVISIBLE
    }

    class RecommendNewsViewHolder(view: View, listener: OnItemClickListener?): RecyclerView.ViewHolder(view){
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

        }
    }

    fun setData(data:MutableList<RecommendItem>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun getItem(position:Int):RecommendItem {
        return data[position]
    }

}