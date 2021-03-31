package com.example.emergency.ui.myPage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.emergency.databinding.MyPagePersonalInfoItemBinding
import com.example.emergency.model.AbstractInfo


class MyPageAdapter :
    RecyclerView.Adapter<MyPageAdapter.ViewHolder>() {
    private var _dataList = listOf<AbstractInfo>()
    fun updateDataList(newList: List<AbstractInfo>) {
        _dataList = newList
    }

    class ViewHolder(private val binding: MyPagePersonalInfoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(abstractInfo: AbstractInfo) {
            binding.myPagePersonalName.text = abstractInfo.realName
            binding.myPagePersonalPhone.text = abstractInfo.phone
        }

        companion object {
            fun create(parent: ViewGroup) =
                MyPagePersonalInfoItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
        }
    }


    /**
     * 每当 [RecyclerView] 需要创建新的 [ViewHolder] 时，它都会调用此方法。
     * 此方法会创建并初始化 [ViewHolder] 及其关联的 [View]，但不会填充视图的内容，因为 [ViewHolder] 此时尚未绑定到具体数据。
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ViewHolder.create(parent))
    }


    /**
     * [RecyclerView] 调用此方法将 [ViewHolder] 与数据相关联。
     * 此方法会提取适当的数据，并使用该数据填充 [ViewHolder] 的布局。
     * 例如，如果 [RecyclerView] 显示的是一个名称列表，该方法可能会在列表中查找适当的名称，并填充 ViewHolder 的 TextView 微件。
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(_dataList[position])
    }

    override fun getItemCount() = _dataList.size
}

