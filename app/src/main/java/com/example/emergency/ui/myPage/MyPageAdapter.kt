package com.example.emergency.ui.myPage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.emergency.R
import com.example.emergency.data.entity.AbstractInfo
import com.example.emergency.databinding.MyPagePersonalInfoItemBinding
import com.example.emergency.model.InfoState
import com.example.emergency.model.MyViewModel
import com.example.emergency.ui.myPage.MyPageAdapter.MyViewHolder.Companion.isChecked
import com.example.emergency.ui.myPage.MyPageAdapter.MyViewHolder.Companion.setCheck


class MyPageAdapter constructor(
    private val myViewModel: MyViewModel
) :
    ListAdapter<AbstractInfo, MyPageAdapter.MyViewHolder>(DIFFCALLBACK) {

    class MyViewHolder(
        private val binding: MyPagePersonalInfoItemBinding,
        val detailOnClickListener: DetailOnClickListener,
        val onClickListener: OnClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.detail.setOnClickListener(detailOnClickListener)
            binding.container.setOnClickListener(onClickListener)
            detailOnClickListener.setBinding(binding)
            onClickListener.setBinding(binding)
        }

        fun bind(abstractInfo: AbstractInfo) {
            with(binding) {
                myPagePersonalName.text = abstractInfo.realName
                myPagePersonalPhone.text = abstractInfo.phone
                checkIcon.setCheck(abstractInfo.chosen)
            }


        }

        companion object {
            fun create(parent: ViewGroup) =
                MyPagePersonalInfoItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

            fun ImageView.setCheck(check: Boolean) {
                visibility = if (check) View.VISIBLE else View.INVISIBLE
            }

            fun ImageView.isChecked() = visibility == View.VISIBLE
        }
    }


    /**
     * 每当 [RecyclerView] 需要创建新的 [MyViewHolder] 时，它都会调用此方法。
     * 此方法会创建并初始化 [MyViewHolder] 及其关联的 [View]，但不会填充视图的内容，因为 [MyViewHolder] 此时尚未绑定到具体数据。
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            MyViewHolder.create(parent),
            DetailOnClickListener(),
            OnClickListener()
        )
    }

    /**
     * [RecyclerView] 调用此方法将 [MyViewHolder] 与数据相关联。
     * 此方法会提取适当的数据，并使用该数据填充 [MyViewHolder] 的布局。
     * 例如，如果 [RecyclerView] 显示的是一个名称列表，该方法可能会在列表中查找适当的名称，并填充 ViewHolder 的 TextView 微件。
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val abstractInfo = getItem(position)
//        if (abstractInfo.chosen) {
//            myViewModel.changeLastCheckedInfo(abstractInfo)
//        }
        holder.bind(abstractInfo)
        holder.detailOnClickListener.setAbstractInfo(abstractInfo)
        holder.onClickListener.setAbstractInfo(abstractInfo)

    }


    inner class DetailOnClickListener : View.OnClickListener {
        private var abstractInfo: AbstractInfo? = null
        private lateinit var binding: MyPagePersonalInfoItemBinding
        fun setAbstractInfo(abstractInfo: AbstractInfo) {
            this.abstractInfo = abstractInfo
        }

        fun setBinding(binding: MyPagePersonalInfoItemBinding) {
            this.binding = binding
        }

        override fun onClick(v: View?) {
            showDetail(abstractInfo!!, binding)
        }
    }

    private fun showDetail(abstractInfo: AbstractInfo, binding: MyPagePersonalInfoItemBinding) {
        myViewModel.showInfoId = abstractInfo.id
        myViewModel.changeInfoTitle("${abstractInfo.realName}的信息")
        myViewModel.changeInfoState(InfoState.SHOW)
        binding.root.findNavController().navigate(R.id.action_user_to_informationFragment)
    }

    inner class OnClickListener : View.OnClickListener {
        private lateinit var binding: MyPagePersonalInfoItemBinding
        private var abstractInfo: AbstractInfo? = null
//        private var position = -1

        fun setAbstractInfo(abstractInfo: AbstractInfo) {
            this.abstractInfo = abstractInfo
//            this.position = position
        }

        fun setBinding(binding: MyPagePersonalInfoItemBinding) {
            this.binding = binding
        }

        override fun onClick(p0: View?) {
            if (binding.checkIcon.isChecked()) {
                showDetail(abstractInfo!!, binding)
            } else {
                binding.checkIcon.setCheck(true)
                val change = abstractInfo!!.copy()
                change.chosen = true
                myViewModel.updateAbstractInfo(change)
            }
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

