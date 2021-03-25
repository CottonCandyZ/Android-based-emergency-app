package com.example.emergency.ui

import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.emergency.R
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


enum class INPUT_HINT {
    REAL_NAME, SEX, RELATIONSHIP, BIRTHDATE,
    PHONE, WEIGHT, BLOOD_TYPE, MEDICAL_CONDITIONS,
    MEDICAL_NOTES, ALLERGY, MEDICATIONS, ADDRESS
}


class InformationAdapter(
    private val textHint: Array<String>,
    private val context: Context,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    class TextInputViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textInputLayout: TextInputLayout = itemView.findViewById(R.id.infoInputLayout)
        val textInputEditText: TextInputEditText = itemView.findViewById(R.id.infoInputText)
    }

    class SpinnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textInputLayout: TextInputLayout = itemView.findViewById(R.id.infoSpinnerLayout)
        val autoCompleteTextView: MaterialAutoCompleteTextView =
            itemView.findViewById(R.id.infoSpinnerText)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            INPUT_HINT.SEX.ordinal,
            INPUT_HINT.BLOOD_TYPE.ordinal -> 1
            else -> 0
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> TextInputViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.info_input_item, parent, false)
            )
            else -> SpinnerViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.info_spinner_item, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder.itemViewType) {
            0 -> {
                with(holder as TextInputViewHolder) {
                    textInputLayout.hint = textHint[position]
                    with(textInputEditText) {
                        when (position) {

                            INPUT_HINT.PHONE.ordinal,
                            INPUT_HINT.WEIGHT.ordinal -> inputType = InputType.TYPE_CLASS_NUMBER
                            in INPUT_HINT.MEDICAL_CONDITIONS.ordinal..INPUT_HINT.ADDRESS.ordinal
                            -> inputType =
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                        }


                    }
                    with(imageView) {
                        when (position) {
                            0 -> setImageResource(R.drawable.ic_bottom_bar_user_24)
                        }
                    }

                }
            }
            1 -> {
                with(holder as SpinnerViewHolder) {
                    textInputLayout.hint = textHint[position]
                    // 下拉菜单
                    when (position) {
                        INPUT_HINT.SEX.ordinal -> createSpinner(
                            autoCompleteTextView,
                            listOf("男", "女")
                        )
                        INPUT_HINT.BLOOD_TYPE.ordinal -> createSpinner(
                            autoCompleteTextView,
                            listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
                        )
                    }

                }

            }
        }


    }

    override fun getItemCount(): Int = textHint.size

    private fun createSpinner(view: AutoCompleteTextView, list: List<String>) {
        val adapter = ArrayAdapter(context, R.layout.list_item, list)
        view.setAdapter(adapter)
    }

}