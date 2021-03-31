package com.example.emergency.ui.info.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import com.example.emergency.R
import com.example.emergency.databinding.InfoSpinnerItemBinding
import com.example.emergency.ui.info.InfoAdapter

class BaseSpinnerViewHolder(
    val binding: InfoSpinnerItemBinding,
    val inputTextWatcher: InfoAdapter.InputTextWatcher,
    isRequired: Boolean = false
) :
    BaseViewHolder(binding) {
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


    fun bind(hint: String, list: List<String>, input: String) {
        val adapter = ArrayAdapter(binding.root.context, R.layout.list_item, list)
        binding.infoSpinnerText.setAdapter(adapter)
        binding.infoSpinnerLayout.hint = hint
        binding.infoSpinnerText.setText(input)

    }

    companion object {
        fun create(parent: ViewGroup) =
            InfoSpinnerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }
}