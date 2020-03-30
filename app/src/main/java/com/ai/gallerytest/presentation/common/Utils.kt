package com.ai.gallerytest.presentation.common

import com.ai.gallerytest.presentation.App
import java.text.DecimalFormat
import kotlin.math.log10

inline fun <T, R> withNotNull(receiver: T?, block: T.() -> R): R? {
    return receiver?.block()
}

fun getString(resId: Int) = App.instance.resources.getString(resId)

fun formatFileSizeString(size: Long): String {
    if (size <= 0) return "0"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(size.toDouble()) / log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
}