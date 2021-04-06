package com.example.emergency.ui.info.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import com.example.emergency.R
import com.example.emergency.databinding.InfoSpinnerItemBinding
import com.example.emergency.ui.info.EditInfoAdapter

class BaseSpinnerViewHolder(
    val binding: InfoSpinnerItemBinding,
    inputTextWatcher: EditInfoAdapter.InputTextWatcher,
    isRequired: Boolean = false
) :
    BaseViewHolder(binding, inputTextWatcher) {
    init {
        if (isRequired) {
            binding.infoSpinnerLayout.helperText = "*必填"
            binding.infoSpinnerText.run {
                onFocusChangeListener = View.OnFocusChangeListener { _, b ->
                    if (!b) {
                        if (text.isNullOrBlank()) {
                            binding.infoSpinnerLayout.error = "请输入"
                        }
                    }
                }
                doAfterTextChanged {
                    if (text.toString().isNotEmpty()) {
                        binding.infoSpinnerLayout.error = null
                    }
                }
            }
        }
        binding.infoSpinnerText.addTextChangedListener(inputTextWatcher)
    }


    override fun bind(hint: String, icon: Int, input: String) {

        binding.infoSpinnerLayout.hint = hint
        if (icon != -1) {
            binding.imageView.setImageResource(icon)
        }
        binding.infoSpinnerText.setText(input, false)
    }

    fun setSpinnerList(list: List<String>) {
        val adapter = ArrayAdapter(binding.root.context, R.layout.list_item, list)
        binding.infoSpinnerText.setAdapter(adapter)
    }

    companion object {
        fun create(parent: ViewGroup) =
            InfoSpinnerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }
}