package com.example.emergency.ui.info.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.example.emergency.databinding.InfoInputItemBinding
import com.example.emergency.ui.InformationFragment

class BaseInputViewHolder(
    val binding: InfoInputItemBinding,
    val inputTextWatcher: InformationFragment.InputTextWatcher,
    isRequired: Boolean = false
) :
    BaseViewHolder(binding) {
    init {
        if (isRequired) {
            binding.infoInputLayout.helperText = "*必填"
            binding.infoInputText.run {
                onFocusChangeListener = View.OnFocusChangeListener { _, b ->
                    if (!b) {
                        if (text.isNullOrBlank()) {
                            binding.infoInputLayout.error = "请输入"
                        }
                    }
                }
                doAfterTextChanged {
                    if (text.toString().isNotEmpty()) {
                        binding.infoInputLayout.error = null
                    }
                }
            }
        }
        binding.infoInputText.addTextChangedListener(inputTextWatcher)
    }

    fun bind(hint: String, inputType: Int, input: String) {
        binding.infoInputLayout.hint = hint
        binding.infoInputText.setText(input)
        binding.infoInputText.inputType = inputType
    }


    companion object {
        fun create(parent: ViewGroup) =
            InfoInputItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }
}