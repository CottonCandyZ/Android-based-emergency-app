package com.example.emergency.ui.info

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.emergency.databinding.InfoEmergencyContactItemBinding
import com.example.emergency.model.EmergencyContact
import com.example.emergency.ui.INPUT_ARRAY_SIZE
import com.example.emergency.ui.MyViewModel
import com.example.emergency.ui.info.viewholder.BaseEmergencyContactViewHolder
import com.example.emergency.ui.info.viewholder.BaseInputViewHolder
import com.example.emergency.ui.info.viewholder.BaseSpinnerViewHolder
import com.example.emergency.ui.info.viewholder.DatePickerInputViewHolder


enum class InputLayoutType {
    INPUT_TEXT, INPUT_TEXT_REQUIRED, INPUT_TEXT_DATE_REQUIRED,
    INPUT_TEXT_PHONE_REQUIRED,
    SPINNER, EMERGENCY_CONTACT,
}

class InputHint {
    companion object {
        const val REAL_NAME = 0
        const val SEX = 1
        const val BIRTHDATE = 2
        const val PHONE = 3
        const val WEIGHT = 4
        const val BLOOD_TYPE = 5
        const val MEDICAL_CONDITIONS = 6
        const val MEDICAL_NOTES = 7
        const val ALLERGY = 8
        const val MEDICATIONS = 9
        const val ADDRESS = 10
    }
}


class EditInfoAdapter(
    private val spinnerList: (Int) -> List<String>,
    private val inputType: (Int) -> Int,
    private val myViewModel: MyViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//    private var lastEmptyPosition = -1

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            // SPINNER
            InputHint.SEX,
            InputHint.BLOOD_TYPE -> InputLayoutType.SPINNER.ordinal

            // INPUT_TEXT_REQUIRED
            InputHint.REAL_NAME -> InputLayoutType.INPUT_TEXT_REQUIRED.ordinal

            // INPUT_TEXT_DATE_REQUIRED
            InputHint.BIRTHDATE -> InputLayoutType.INPUT_TEXT_DATE_REQUIRED.ordinal

            // INPUT_TEXT_PHONE_REQUIRED
            InputHint.PHONE -> InputLayoutType.INPUT_TEXT_PHONE_REQUIRED.ordinal

            // INPUT_TEXT
            in InputHint.REAL_NAME..InputHint.ADDRESS
            -> InputLayoutType.INPUT_TEXT.ordinal

            // EMERGENCY_CONTACT
            else -> InputLayoutType.EMERGENCY_CONTACT.ordinal
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            InputLayoutType.INPUT_TEXT.ordinal ->
                BaseInputViewHolder(
                    BaseInputViewHolder.create(parent),
                    InputTextWatcher(),
                )

            InputLayoutType.SPINNER.ordinal ->
                BaseSpinnerViewHolder(
                    BaseSpinnerViewHolder.create(parent),
                    InputTextWatcher()
                )

            InputLayoutType.INPUT_TEXT_REQUIRED.ordinal ->
                BaseInputViewHolder(
                    BaseInputViewHolder.create(parent),
                    InputTextWatcher(),
                    isRequired = true,
                )

            InputLayoutType.INPUT_TEXT_DATE_REQUIRED.ordinal ->
                DatePickerInputViewHolder(
                    BaseInputViewHolder.create(parent),
                    InputTextWatcher(),
                    parent.context,
                    isRequired = true,
                )

            InputLayoutType.INPUT_TEXT_PHONE_REQUIRED.ordinal ->
                BaseInputViewHolder(
                    BaseInputViewHolder.create(parent),
                    InputTextWatcher(),
                    isRequired = true,
                    isPhoneNumber = true
                )
            else ->
                BaseEmergencyContactViewHolder(
                    BaseEmergencyContactViewHolder.create(parent),
                    EmergencyPhoneTextWatcher(),
                    EmergencyOnClickDelete(),
                    EmergencyRelationshipTextWatcher()
                )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BaseInputViewHolder -> {
                holder.inputTextWatcher.updatePosition(position)
                holder.bind(
                    myViewModel.inputHints[position],
                    inputType(position),
                    myViewModel.inputInfo[position]
                )

            }
            is BaseSpinnerViewHolder -> {
                holder.inputTextWatcher.updatePosition(position)
                holder.bind(
                    myViewModel.inputHints[position],
                    spinnerList(position),
                    myViewModel.inputInfo[position]
                )
            }
            is BaseEmergencyContactViewHolder -> {
                holder.emergencyPhoneTextWatcher.updatePosition(holder.adapterPosition)
                holder.emergencyRelationshipTextWatcher.updatePosition(holder.adapterPosition)
                holder.emergencyOnClickDelete.updatePosition(holder.adapterPosition)
                holder.bind(
                    spinnerList(position),
                    myViewModel.emergencyNumber[position - INPUT_ARRAY_SIZE]
                )

            }
        }
    }

    override fun getItemCount(): Int = INPUT_ARRAY_SIZE + myViewModel.emergencyNumber.size

    inner class InputTextWatcher : TextWatcher {
        private var position = -1
        fun updatePosition(position: Int) {
            this.position = position
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            myViewModel.inputInfo[position] = text.toString()
        }

        override fun afterTextChanged(p0: Editable?) {}
    }

    inner class EmergencyPhoneTextWatcher : TextWatcher {
        private var position = -1

        private lateinit var binding: InfoEmergencyContactItemBinding

        fun updatePosition(position: Int) {
            this.position = position
        }

        fun setBinding(binding: InfoEmergencyContactItemBinding) {
            this.binding = binding
        }

        private fun getIndex(position: Int): Int {
            return position - INPUT_ARRAY_SIZE
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(text: Editable?) {
            if (text.toString().trim().isNotEmpty()) {
                if (myViewModel.emergencyNumber[getIndex(position)].phone == "") {
                    binding.infoRemoveEC.visibility = View.VISIBLE
                    myViewModel.emergencyNumber[getIndex(position)].phone = text.toString()
                    myViewModel.emergencyNumber.add(EmergencyContact())
                } else {
                    myViewModel.emergencyNumber[getIndex(position)].phone = text.toString()
                    return
                }
                notifyItemChanged(itemCount)
            } else {
                val empty =
                    myViewModel.emergencyNumber.mapIndexed { index, it -> if (it.phone == "") index else null }
                        .filterNotNull()[0]
                myViewModel.emergencyNumber[getIndex(position)] = EmergencyContact()
                myViewModel.emergencyNumber.removeAt(empty)
                notifyItemRemoved(empty + INPUT_ARRAY_SIZE)
                notifyItemRangeChanged(
                    empty + INPUT_ARRAY_SIZE,
                    itemCount - empty + INPUT_ARRAY_SIZE
                )
                binding.infoRemoveEC.visibility = View.GONE
            }
        }
    }

    inner class EmergencyOnClickDelete : View.OnClickListener {
        private var position = -1
        fun updatePosition(position: Int) {
            this.position = position
        }

        private fun getIndex(position: Int): Int {
            return position - INPUT_ARRAY_SIZE
        }

        override fun onClick(p0: View?) {
            myViewModel.emergencyNumber.removeAt(getIndex(position))
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount - position)
        }
    }

    inner class EmergencyRelationshipTextWatcher : TextWatcher {
        private var position = -1
        fun updatePosition(position: Int) {
            this.position = position
        }

        private fun getIndex(position: Int): Int {
            return position - INPUT_ARRAY_SIZE
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(text: Editable?) {
            myViewModel.emergencyNumber[getIndex(position)].relationship = text.toString()
        }
    }
}