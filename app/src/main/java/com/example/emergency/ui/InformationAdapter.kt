package com.example.emergency.ui

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
//    private val getDataInput: (Int) -> String,
//    private val inputTextWatcher: InformationFragment.InputTextWatcher
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
                    InformationFragment.InputTextWatcher(mDataset)
                )

            INPUT_TYPE.SPINNER_VIEW.ordinal ->
                BaseSpinnerViewHolder(BaseSpinnerViewHolder.create(parent))

            INPUT_TYPE.INPUT_TEXT_REQUIRED.ordinal ->
                BaseInputViewHolder(
                    BaseInputViewHolder.create(parent),
                    InformationFragment.InputTextWatcher(mDataset),
                    true
                )

            else -> BaseEmergencyContactViewHolder(BaseEmergencyContactViewHolder.create(parent))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // 下面的内容不应该写在这里，后期将会更改

        when (holder) {
            is BaseInputViewHolder -> {
                holder.bind(
                    inputHints[position],
                    inputType(position),
                    mDataset[position]
                )
                holder.inputTextWatcher.updatePosition(position)
            }
            is BaseSpinnerViewHolder -> {
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

}