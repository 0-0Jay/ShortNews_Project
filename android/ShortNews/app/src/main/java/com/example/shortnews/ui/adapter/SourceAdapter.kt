package com.example.shortnews.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shortnews.R

class SourceAdapter: RecyclerView.Adapter<SourceAdapter.NewsViewHolder>() {

        fun interface OnItemClickListener {
            fun onItemClick(v: View, position:Int)
        }
        private var listener: OnItemClickListener? = null

        fun setListener(listener: OnItemClickListener) {
            this.listener = listener
        }

        private var data:List<String> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
            val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.source_item, parent, false)
            return NewsViewHolder(view, listener)
        }

        // data의 개수를 알려줌
        override fun getItemCount(): Int {
            return data.size
        }

        // data 의 내용을 넣는 작업
        override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
            val item = data[position]

            holder.sourceUrl.text = (position + 1).toString() + ". " + item

        }

        class NewsViewHolder(view: View, listener: OnItemClickListener?): RecyclerView.ViewHolder(view){

            val sourceUrl: TextView = view.findViewById(R.id.sourceUrl)

            init {
                view.setOnClickListener {
                    listener?.onItemClick(view, this.layoutPosition)
                }

            }
        }

        fun setData(data:List<String>) {
            this.data = data
            notifyDataSetChanged()
        }

        fun getItem(position:Int): String {
            return data[position]
        }

 }

