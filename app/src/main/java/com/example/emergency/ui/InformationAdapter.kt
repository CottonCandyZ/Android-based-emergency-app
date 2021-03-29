package com.example.emergency.ui

import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.emergency.ui.info.viewholder.BaseEmergencyContactViewHolder
import com.example.emergency.ui.info.viewholder.BaseInputViewHolder
import com.example.emergency.ui.info.viewholder.BaseSpinnerViewHolder


enum class INPUT_HINT {
    REAL_NAME, SEX, BIRTHDATE,
    PHONE, WEIGHT, BLOOD_TYPE, MEDICAL_CONDITIONS,
    MEDICAL_NOTES, ALLERGY, MEDICATIONS, ADDRESS
}

enum class INPUT_TYPE {
    INPUT_TEXT, SPINNER_VIEW, EMERGENCY_CONTACT,
    INPUT_TEXT_REQUIRED, SPINNER_VIEW_REQUIRED
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
            INPUT_HINT.SEX.ordinal,
            INPUT_HINT.BLOOD_TYPE.ordinal -> INPUT_TYPE.SPINNER_VIEW.ordinal

            // INPUT_TEXT
            INPUT_HINT.REAL_NAME.ordinal,
            INPUT_HINT.BIRTHDATE.ordinal,
            INPUT_HINT.PHONE.ordinal
            -> INPUT_TYPE.INPUT_TEXT_REQUIRED.ordinal


            in INPUT_HINT.REAL_NAME.ordinal..INPUT_HINT.ADDRESS.ordinal
            -> INPUT_TYPE.INPUT_TEXT.ordinal


            else -> INPUT_TYPE.EMERGENCY_CONTACT.ordinal
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {


            INPUT_TYPE.INPUT_TEXT.ordinal ->
                BaseInputViewHolder(
                    BaseInputViewHolder.create(parent),
                    InputTextWatcher()
                )

            INPUT_TYPE.SPINNER_VIEW.ordinal ->
                BaseSpinnerViewHolder(
                    BaseSpinnerViewHolder.create(parent),
                    InputTextWatcher()
                )

            INPUT_TYPE.INPUT_TEXT_REQUIRED.ordinal ->
                BaseInputViewHolder(
                    BaseInputViewHolder.create(parent),
                    InputTextWatcher(),
                    true
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
        private var position = 0
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