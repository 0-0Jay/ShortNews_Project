package com.example.shortnews.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shortnews.R


class ImageSlideAdapter:RecyclerView.Adapter<ImageSlideAdapter.ImageSlideViewHolder>() {

    private var data:MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSlideViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.image_item, parent, false)
        return ImageSlideViewHolder(view)
    }

    // data의 개수를 알려줌
    override fun getItemCount(): Int {
        if (data.isEmpty()) return 1
        else return data.size
    }

    // data 의 내용을 넣는 작업
    override fun onBindViewHolder(holder: ImageSlideViewHolder, position: Int) {

        val defaultImage = "https://snewsimgs.s3.ap-northeast-2.amazonaws.com/news-default.png"

        if (data.isEmpty()) {
            Glide.with(holder.itemView.context)
                .load(defaultImage)
                .error(defaultImage)
                .into(holder.newsImg)
        } else {
            val item = data[position]
            val newsImage = item

            Glide.with(holder.itemView.context)
                .load(newsImage)
                .error(defaultImage)
                .into(holder.newsImg)
        }
    }

    class ImageSlideViewHolder(view: View): RecyclerView.ViewHolder(view){
        val newsImg:ImageView = view.findViewById(R.id.imageView)
    }

    fun setData(data:MutableList<String>) {
        this.data = data
        Log.d("data img 응답", data.toString())
        notifyDataSetChanged()
    }

    fun getData():String {
        if (data.isEmpty()) return "https://snewsimgs.s3.ap-northeast-2.amazonaws.com/news-default.png"
        else return data[0]
    }


}