package com.example.emergency.ui.info.viewholder

import android.content.Context
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity
import com.example.emergency.R
import com.example.emergency.databinding.InfoInputItemBinding
import com.example.emergency.ui.info.EditInfoAdapter
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class DatePickerInputViewHolder(
    binding: InfoInputItemBinding,
    inputTextWatcher: EditInfoAdapter.InputTextWatcher,
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
        val constraintsBuilder = CalendarConstraints.Builder().setValidator(
            DateValidatorPointBackward.before(MaterialDatePicker.todayInUtcMilliseconds())
        )
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("选择您的生日")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            myCalendar.timeInMillis = it
            updateLabel()
        }

        fun showDatePicker() {
            datePicker.show(
                ((context as ContextWrapper).baseContext as FragmentActivity).supportFragmentManager,
                datePicker.toString()
            )
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