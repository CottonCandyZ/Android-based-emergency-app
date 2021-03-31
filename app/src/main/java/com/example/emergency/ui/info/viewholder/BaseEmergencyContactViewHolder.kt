package com.example.emergency.ui.info.viewholder

import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.emergency.R
import com.example.emergency.databinding.InfoEmergencyContactItemBinding
import com.example.emergency.ui.info.InformationAdapter

class BaseEmergencyContactViewHolder(
    val binding: InfoEmergencyContactItemBinding,
    val emergencyPhoneTextWatcher: InformationAdapter.EmergencyPhoneTextWatcher,
    val emergencyOnClickDelete: InformationAdapter.EmergencyOnClickDelete,
    val emergencyRelationshipTextWatcher: InformationAdapter.EmergencyRelationshipTextWatcher
) :
    BaseViewHolder(binding) {
    init {
        binding.infoECPhoneLayout.hint = "紧急联系人"
        binding.infoECRelationshipLayout.hint = "关系"
        binding.infoRemoveEC.setOnClickListener(emergencyOnClickDelete)
        binding.infoECPhoneText.run {
            inputType = InputType.TYPE_CLASS_PHONE
            emergencyPhoneTextWatcher.setBinding(binding)
        }
        binding.infoECRelationshipText.addTextChangedListener(emergencyRelationshipTextWatcher)
    }

    fun bind(list: List<String>, inputText: Array<String>) {
        binding.infoECRelationshipText.setAdapter(
            ArrayAdapter(binding.root.context, R.layout.list_item, list)
        )
        binding.infoECPhoneText.removeTextChangedListener(emergencyPhoneTextWatcher)
        if (inputText[0] != "") {
            binding.infoRemoveEC.visibility = View.VISIBLE
        } else {
            binding.infoRemoveEC.visibility = View.GONE
        }
        binding.infoECPhoneText.setText(inputText[0])
        binding.infoECRelationshipText.setText(inputText[1], false)
        binding.infoECPhoneText.addTextChangedListener(emergencyPhoneTextWatcher)
    }


    companion object {
        fun create(parent: ViewGroup) =
            InfoEmergencyContactItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
    }
}