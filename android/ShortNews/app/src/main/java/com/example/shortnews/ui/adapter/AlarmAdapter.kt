package com.example.shortnews.ui.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shortnews.R
import com.example.shortnews.model.AlarmChild
import com.example.shortnews.model.Alarms
import com.example.shortnews.model.RecommendItem

class AlarmAdapter:RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {
    private var data:List<AlarmChild> = mutableListOf()
    fun interface OnItemClickListener {
        fun onItemClick(v:View, position:Int)
    }
    private var listener: OnItemClickListener? = null

    fun setListener(listener: OnItemClickListener) {
        this.listener = listener
    }


    // 알람 삭제
    interface OnAlarmDeleteListener {
        fun onAlarmDelete(position:Int)
    }
    private var AlarmDeleteListener: OnAlarmDeleteListener? = null

    fun setAlarmDeleteListener(listener: OnAlarmDeleteListener) {
        this.AlarmDeleteListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.alarm_item, parent, false)
        return AlarmViewHolder(view, listener, AlarmDeleteListener)
    }

    // data의 개수를 알려줌
    override fun getItemCount(): Int {
        return data.size
    }

    fun getItem(position : Int) : AlarmChild {
        return data[position]
    }

    fun setDrop():Unit{
        data = emptyList()
        notifyDataSetChanged()
    }

    // data 의 내용을 넣는 작업
    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val item = data[position]

        val type = item.type
        val nick = item.nickname
        var alarmTitle = when(type) {
            1 -> "'${nick}'님이 추천을 눌렀습니다."
            0 -> "'${nick}'님이 답글을 달았습니다."
            else -> "'${nick}'님이 비추천을 눌렀습니다."
        }
        holder.alarmTitle.text = alarmTitle
        holder.alarmTime.text = item.time.split(".")[0]
        holder.alarmItemLayout.setBackgroundColor(Color.parseColor("#f2f2f2"))
        holder.alarmTitle.setBackgroundColor(Color.parseColor("#f2f2f2"))
        holder.alarmTime.setBackgroundColor(Color.parseColor("#f2f2f2"))
        holder.getDelete().setBackgroundColor(Color.parseColor("#f2f2f2"))
//        if (item.status == 1) {
//            val color = Color.parseColor("#e0e0e0")
//            holder.alarmTitle.setTextColor(color)
//            holder.alarmTime.setTextColor(color)
//        }
    }
    fun setData(data:List<AlarmChild>) {
        this.data = data
        Log.d("Alarm data 응답", data.toString())
        notifyDataSetChanged()
    }


    class AlarmViewHolder(view: View, listener: OnItemClickListener?, alarmDeleteListener : OnAlarmDeleteListener?): RecyclerView.ViewHolder(view){
        val alarmTitle:TextView = view.findViewById(R.id.alarmtitle)
        val alarmTime:TextView = view.findViewById(R.id.alarmtime)
        val alarmItemLayout : ConstraintLayout = view.findViewById(R.id.alarmItemLayout)
        private val alarmDelete:ImageView = view.findViewById(R.id.alarmdelete)
        init {
            view.setOnClickListener {
                listener?.onItemClick(view, this.layoutPosition)
            }
            alarmDelete.setOnClickListener {
                alarmDeleteListener?.onAlarmDelete(this.layoutPosition)
            }
        }

        fun getDelete(): ImageView {
            return this.alarmDelete
        }
    }
}