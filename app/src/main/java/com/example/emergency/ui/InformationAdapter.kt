package com.example.emergency.ui

import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.emergency.R
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class InformationAdapter(
    private val textHint: Array<String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    class TextInputViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textInputLayout: TextInputLayout = itemView.findViewById(R.id.infoInputLayout)
        val textInputEditText: TextInputEditText = itemView.findViewById(R.id.infoInputText)
    }

    class SpinnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textInputLayout: TextInputLayout = itemView.findViewById(R.id.infoSpinnerLayout)
        val textInputEditText: MaterialAutoCompleteTextView =
            itemView.findViewById(R.id.infoSpinnerText)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0, 2, 3, 4, 6, 7, 8, 9, 10 -> 0
            else -> 1
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
                            3, 4 -> inputType = InputType.TYPE_CLASS_NUMBER
                            in 6..10 -> inputType =
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
                (holder as SpinnerViewHolder).textInputLayout.hint = textHint[position]
            }
        }


    }

    override fun getItemCount(): Int = textHint.size
}