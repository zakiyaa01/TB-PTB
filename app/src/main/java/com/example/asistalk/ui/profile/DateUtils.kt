package com.example.asistalk.ui.profile

fun getMonthName(month: Int): String {
    return listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )[month]
}
