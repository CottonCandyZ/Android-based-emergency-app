package com.example.emergency.ui.info.viewholder

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.emergency.ui.info.EditInfoAdapter

open class BaseViewHolder(
    binding: ViewBinding,
    val inputTextWatcher: EditInfoAdapter.InputTextWatcher
) : RecyclerView.ViewHolder(binding.root) {
    open fun bind(hint: String, icon: Int, input: String) {}
}

