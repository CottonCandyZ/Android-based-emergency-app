package com.example.emergency.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.emergency.R


class MyPageAdapter(private val dataSet: Array<String>) :
    RecyclerView.Adapter<MyPageAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.myPagePersonalName)
        val textViewPhone: TextView = itemView.findViewById(R.id.myPagePersonalPhone)
    }


    /**
     * 每当 [RecyclerView] 需要创建新的 [ViewHolder] 时，它都会调用此方法。
     * 此方法会创建并初始化 [ViewHolder] 及其关联的 [View]，但不会填充视图的内容，因为 [ViewHolder] 此时尚未绑定到具体数据。
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_page_personal_info_item, parent, false)
        return ViewHolder(view)
    }


    /**
     * [RecyclerView] 调用此方法将 [ViewHolder] 与数据相关联。
     * 此方法会提取适当的数据，并使用该数据填充 [ViewHolder] 的布局。
     * 例如，如果 [RecyclerView] 显示的是一个名称列表，该方法可能会在列表中查找适当的名称，并填充 ViewHolder 的 TextView 微件。
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

    override fun getItemCount() = dataSet.size
}

