package com.example.task3.ui

import java.text.NumberFormat
import java.util.Locale

fun formatInteger(value: Long): String {
    return NumberFormat.getIntegerInstance(Locale.forLanguageTag("ru-RU")).format(value)
}

fun formatArea(value: Double): String {
    return NumberFormat.getNumberInstance(Locale.forLanguageTag("ru-RU")).apply {
        maximumFractionDigits = 0
    }.format(value)
}
