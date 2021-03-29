package com.example.emergency.ui.info.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.emergency.R
import com.example.emergency.databinding.InfoEmergencyContactItemBinding

class BaseEmergencyContactViewHolder(val binding: InfoEmergencyContactItemBinding) :
    BaseViewHolder(binding) {
    init {
        binding.infoECPhoneLayout.hint = "紧急联系人"
        binding.infoECRelationshipLayout.hint = "关系"

//            binding.infoECPhoneText.run {
//                inputType = InputType.TYPE_CLASS_NUMBER
//                var before = ""
//                doAfterTextChanged { text ->
//                    if (text.toString().trim().isNotEmpty()) {
//                        if (before == "") {
//                            binding.infoRemoveEC.visibility = View.VISIBLE
//                            InformationAdapter().notifyItemChanged(itemCount)
//                            before = text.toString()
//                        } else {
//                            before = text.toString()
//                            return@doAfterTextChanged
//                        }
//                    } else {
//                        if (adapterPosition == itemCount - 1) {
//                            notifyItemRemoved(lastEmpetyPosition)
//                        } else {
//                            lastEmpetyPosition = adapterPosition
//                            notifyItemRemoved(itemCount)
//                        }
//
//                        before = ""
//                        remove.visibility = View.GONE
//                        emergencyContactNumber--
//
//                    }
//                    Toast.makeText(
//                        context,
//                        adapterPosition.toString(),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
    }

    fun bind(list: List<String>) {
        binding.infoECRelationshipText.setAdapter(
            ArrayAdapter(binding.root.context, R.layout.list_item, list)
        )
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