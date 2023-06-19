package com.karsatech.storyapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.withDateFormat(): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    val date = inputFormat.parse(this) ?: return ""

    val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.US)
    return outputFormat.format(date)
}

fun String.withCurrentDateFormat() : String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val date = dateFormat.parse(this)
    val currentTime = Date()

    val diff = currentTime.time - date.time
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> "$days hari yang lalu"
        hours > 0 -> "$hours jam yang lalu"
        minutes > 0 -> "$minutes menit yang lalu"
        else -> "$seconds detik yang lalu"
    }
}