package com.example.eduaituitor.utils

import java.text.SimpleDateFormat
import java.util.*

fun Long.toFormattedDate(): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}

fun String.truncate(maxLength: Int): String {
    return if (this.length > maxLength) "${this.substring(0, maxLength)}..." else this
}
