package com.example.emergency.ui.history

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.emergency.data.entity.History
import com.example.emergency.databinding.HistoryItemBinding
import java.text.SimpleDateFormat
import java.util.*


class HistoryAdapter : ListAdapter<History, HistoryAdapter.MyViewHolder>(DIFFCALLBACK) {

    class MyViewHolder(
        private val binding: HistoryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: History) {
            with(binding) {
                patientName.text = history.patientName
                locationName.text = history.locationName ?: "尚未提交"
                createTime.text = convertDateToString(history.createTime)
                handler.text = history.handler ?: "尚未处理"
                responseTime.text =
                    if (history.responseTime == null) "尚未处理" else convertDateToString(history.responseTime)
                status.text = history.status
                when (history.status) {
                    "呼救中" -> {
                        status.setTextColor(Color.RED)
                    }
                    "已取消" -> {
                        status.setTextColor(Color.GRAY)
                    }
                    "已处理" -> {
                        status.setTextColor(Color.BLUE)
                    }
                }
            }

        }

        fun convertDateToString(date: Date): String {
            val format = "yyyy.MM.dd 'at' HH:mm:ss"
            return SimpleDateFormat(format, Locale.CHINA).format(date.time)
        }

        companion object {
            fun create(parent: ViewGroup) =
                HistoryItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(MyViewHolder.create(parent))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object DIFFCALLBACK : DiffUtil.ItemCallback<History>() {
        override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem == newItem
        }

    }
}