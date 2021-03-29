package com.example.emergency.ui

import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.emergency.ui.info.viewholder.BaseEmergencyContactViewHolder
import com.example.emergency.ui.info.viewholder.BaseInputViewHolder
import com.example.emergency.ui.info.viewholder.BaseSpinnerViewHolder
import com.example.emergency.ui.info.viewholder.DatePickerInputViewHolder


enum class InputHint {
    REAL_NAME, SEX, BIRTHDATE,
    PHONE, WEIGHT, BLOOD_TYPE, MEDICAL_CONDITIONS,
    MEDICAL_NOTES, ALLERGY, MEDICATIONS, ADDRESS
}

enum class InputType {
    INPUT_TEXT, INPUT_TEXT_REQUIRED, INPUT_TEXT_DATE_REQUIRED,
    INPUT_TEXT_PHONE_REQUIRED,
    SPINNER_VIEW, EMERGENCY_CONTACT,
}


class InformationAdapter(
    private val inputHints: Array<String>,
    private val spinnerList: (Int) -> List<String>,
    private val inputType: (Int) -> Int,
    private val mDataset: ArrayList<String>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (position) {


            // SPINNER
            InputHint.SEX.ordinal,
            InputHint.BLOOD_TYPE.ordinal -> InputType.SPINNER_VIEW.ordinal

            // INPUT_TEXT_REQUIRED
            InputHint.REAL_NAME.ordinal -> InputType.INPUT_TEXT_REQUIRED.ordinal

            // INPUT_TEXT_DATE_REQUIRED
            InputHint.BIRTHDATE.ordinal -> InputType.INPUT_TEXT_DATE_REQUIRED.ordinal

            // INPUT_TEXT_PHONE_REQUIRED
            InputHint.PHONE.ordinal -> InputType.INPUT_TEXT_PHONE_REQUIRED.ordinal


            in InputHint.REAL_NAME.ordinal..InputHint.ADDRESS.ordinal
            -> InputType.INPUT_TEXT.ordinal


            else -> InputType.EMERGENCY_CONTACT.ordinal
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {


            InputType.INPUT_TEXT.ordinal ->
                BaseInputViewHolder(
                    BaseInputViewHolder.create(parent),
                    InputTextWatcher(),
                )

            InputType.SPINNER_VIEW.ordinal ->
                BaseSpinnerViewHolder(
                    BaseSpinnerViewHolder.create(parent),
                    InputTextWatcher()
                )

            InputType.INPUT_TEXT_REQUIRED.ordinal ->
                BaseInputViewHolder(
                    BaseInputViewHolder.create(parent),
                    InputTextWatcher(),
                    isRequired = true,
                )

            InputType.INPUT_TEXT_DATE_REQUIRED.ordinal ->
                DatePickerInputViewHolder(
                    BaseInputViewHolder.create(parent),
                    InputTextWatcher(),
                    parent.context,
                    isRequired = true,
                )

            InputType.INPUT_TEXT_PHONE_REQUIRED.ordinal ->
                BaseInputViewHolder(
                    BaseInputViewHolder.create(parent),
                    InputTextWatcher(),
                    isRequired = true,
                    isPhoneNumber = true
                )

            else -> BaseEmergencyContactViewHolder(BaseEmergencyContactViewHolder.create(parent))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BaseInputViewHolder -> {
                holder.inputTextWatcher.updatePosition(position)
                holder.bind(
                    inputHints[position],
                    inputType(position),
                    mDataset[position]
                )

            }
            is BaseSpinnerViewHolder -> {
                holder.inputTextWatcher.updatePosition(position)
                holder.bind(
                    inputHints[position],
                    spinnerList(position),
                    mDataset[position]
                )
            }
            is BaseEmergencyContactViewHolder -> holder.bind(
                spinnerList(position)
            )
        }
    }

    override fun getItemCount(): Int = inputHints.size + 1


    inner class InputTextWatcher : TextWatcher {
        private var position = -1
        fun updatePosition(position: Int) {
            this.position = position
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mDataset[position] = p0.toString()
        }

        override fun afterTextChanged(p0: Editable?) {
        }

    }
}