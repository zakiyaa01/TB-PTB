package com.example.asistalk.ui.profile

import android.app.DatePickerDialog
import android.content.Context
import java.util.Calendar

fun showDatePicker(
    context: Context,
    onDateSelected: (year: Int, month: Int, day: Int) -> Unit
) {
    val calendar = Calendar.getInstance()

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected(year, month, dayOfMonth)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}
