package com.example.emergency.ui.info

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.emergency.databinding.InfoEmergencyShowItemBinding
import com.example.emergency.databinding.InfoShowItemBinding
import com.example.emergency.ui.INPUT_ARRAY_SIZE
import com.example.emergency.ui.MyViewModel


enum class ShowLayoutType {
    INFO, EMERGENCY_NUMBER, TITLE
}

class ShowInfoAdapter(
    private val myViewModel: MyViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var _infoList: Array<String> = arrayOf()
    private var _emergencyNumberList: List<Array<String>> = listOf()
    private var title = 0

    @SuppressLint("NotifyDataSetChanged")
    fun updateDataList(infoList: Array<String>, emergencyNumberList: List<Array<String>>) {
        _infoList = infoList
        _emergencyNumberList = emergencyNumberList
        title = 1
        notifyDataSetChanged()
    }

    class InfoViewHolder(private val binding: InfoShowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(title: String, detail: String) {
            binding.infoTitle.text = title
            binding.infoDetial.text = detail
        }

        companion object {
            fun create(parent: ViewGroup) =
                InfoShowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }
    }

    class TitleViewHolder(private val binding: InfoEmergencyShowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.showInfoEditText.textSize = 24f
        }

        fun bind(title: String) {
            binding.showInfoEditText.text = title
        }

        companion object {
            fun create(parent: ViewGroup) =
                InfoEmergencyShowItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
        }
    }

    class EmergencyNumberViewHolder(private val binding: InfoEmergencyShowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(phone: String, relationship: String) {
            binding.showInfoEditText.text = "电话：${phone} 关系：${relationship}"
        }

        companion object {
            fun create(parent: ViewGroup) =
                InfoEmergencyShowItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            in InputHint.REAL_NAME..InputHint.ADDRESS -> ShowLayoutType.INFO.ordinal
            InputHint.ADDRESS + 1 -> ShowLayoutType.TITLE.ordinal
            else -> ShowLayoutType.EMERGENCY_NUMBER.ordinal
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ShowLayoutType.INFO.ordinal -> InfoViewHolder(InfoViewHolder.create(parent))
            ShowLayoutType.TITLE.ordinal -> TitleViewHolder(TitleViewHolder.create(parent))
            else -> EmergencyNumberViewHolder(EmergencyNumberViewHolder.create(parent))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is InfoViewHolder -> {
                val text = if (_infoList[position] == "") "尚未填写" else _infoList[position]
                holder.bind(myViewModel.inputHints[position], text)
            }
            is TitleViewHolder -> holder.bind("紧急联系人")
            is EmergencyNumberViewHolder -> holder.bind(
                _emergencyNumberList[position - INPUT_ARRAY_SIZE - 1][1],
                _emergencyNumberList[position - INPUT_ARRAY_SIZE - 1][0]
            )
        }
    }

    override fun getItemCount(): Int = _infoList.size + _emergencyNumberList.size + title

}