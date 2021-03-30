package com.example.emergency.ui.info.viewholder

import android.app.DatePickerDialog
import android.content.Context
import com.example.emergency.R
import com.example.emergency.databinding.InfoInputItemBinding
import com.example.emergency.ui.info.InformationAdapter
import java.text.SimpleDateFormat
import java.util.*

class DatePickerInputViewHolder(
    binding: InfoInputItemBinding,
    inputTextWatcher: InformationAdapter.InputTextWatcher,
    context: Context,
    isRequired: Boolean,
) : BaseInputViewHolder(binding, inputTextWatcher, isRequired) {
    private val myCalendar: Calendar = Calendar.getInstance()

    init {
        binding.infoInputText.isClickable = true
        binding.infoInputText.isCursorVisible = false
        binding.infoInputText.isFocusable = false
        binding.infoInputText.isFocusableInTouchMode = false
        binding.infoInputLayout.isEndIconVisible = true
        binding.infoInputLayout.setEndIconDrawable(R.drawable.ic_baseline_calendar_today_24)

        val date = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }

        fun showDatePicker() {
            DatePickerDialog(
                context, date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.infoInputText.setOnClickListener {
            showDatePicker()
        }
        binding.infoInputLayout.setEndIconOnClickListener {
            showDatePicker()
        }


    }

    private fun updateLabel() {
        val myFormat = "yyyy/MM/dd"
        val sdf = SimpleDateFormat(myFormat, Locale.CHINA)
        binding.infoInputText.setText(sdf.format(myCalendar.time))
    }
}