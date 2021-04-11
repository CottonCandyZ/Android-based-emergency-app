package com.example.emergency.ui.myPage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.emergency.R
import com.example.emergency.data.entity.AbstractInfo
import com.example.emergency.databinding.MyPagePersonalInfoItemBinding
import com.example.emergency.ui.InfoState
import com.example.emergency.ui.MyViewModel


class MyPageAdapter constructor(
    private val myViewModel: MyViewModel
) :
    ListAdapter<AbstractInfo, MyPageAdapter.ViewHolder>(DIFFCALLBACK) {

    class ViewHolder(
        private val binding: MyPagePersonalInfoItemBinding,
        val onClickListener: OnClickListener,
        val onClickChangeListener: OnClickChangeListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            onClickListener.setBiding(binding)
            onClickChangeListener.setBiding(binding)
            binding.leftContainter.setOnClickListener(onClickListener)
            binding.infoSwitch.setOnCheckedChangeListener(onClickChangeListener)
        }

        fun bind(abstractInfo: AbstractInfo) {
            binding.myPagePersonalName.text = abstractInfo.realName
            binding.myPagePersonalPhone.text = abstractInfo.phone
            val infoSwitchText = if (abstractInfo.chosen) "已选择" else "未选择"
            binding.infoSwitch.text = infoSwitchText
            // 修改状态时需要先解绑监听器
            binding.infoSwitch.setOnCheckedChangeListener(null)
            binding.infoSwitch.isChecked = abstractInfo.chosen
            binding.infoSwitch.setOnCheckedChangeListener(onClickChangeListener)
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
        return ViewHolder(ViewHolder.create(parent), OnClickListener(), OnClickChangeListener())
    }


    /**
     * [RecyclerView] 调用此方法将 [ViewHolder] 与数据相关联。
     * 此方法会提取适当的数据，并使用该数据填充 [ViewHolder] 的布局。
     * 例如，如果 [RecyclerView] 显示的是一个名称列表，该方法可能会在列表中查找适当的名称，并填充 ViewHolder 的 TextView 微件。
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.onClickListener.setAbstractInfo(getItem(position))
        holder.onClickChangeListener.setAbstractInfo(getItem(position), position)
    }
    inner class OnClickListener : View.OnClickListener {
        private var abstractInfo: AbstractInfo? = null
        private lateinit var binding: MyPagePersonalInfoItemBinding
        fun setAbstractInfo(abstractInfo: AbstractInfo) {
            this.abstractInfo = abstractInfo
        }

        fun setBiding(binding: MyPagePersonalInfoItemBinding) {
            this.binding = binding
        }

        override fun onClick(v: View?) {
            myViewModel.showInfoId = abstractInfo!!.id
            myViewModel.changeInfoTitle("${abstractInfo!!.realName}的信息")
            myViewModel.changeInfoState(InfoState.SHOW)
            binding.root.findNavController().navigate(R.id.action_user_to_informationFragment)
        }
    }

    inner class OnClickChangeListener : CompoundButton.OnCheckedChangeListener {
        private lateinit var binding: MyPagePersonalInfoItemBinding
        private var abstractInfo: AbstractInfo? = null
        private var position = -1
        fun setAbstractInfo(abstractInfo: AbstractInfo, position: Int) {
            this.abstractInfo = abstractInfo
            this.position = position
        }

        fun setBiding(binding: MyPagePersonalInfoItemBinding) {
            this.binding = binding
        }

        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            binding.infoSwitch.text = if (isChecked) "已选择" else "未选择"
            abstractInfo!!.chosen = isChecked
            myViewModel.updateAbstractInfo(abstractInfo!!)
        }

    }

    object DIFFCALLBACK : DiffUtil.ItemCallback<AbstractInfo>() {
        override fun areItemsTheSame(oldItem: AbstractInfo, newItem: AbstractInfo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AbstractInfo, newItem: AbstractInfo): Boolean {
            return oldItem == newItem
        }
    }
}

