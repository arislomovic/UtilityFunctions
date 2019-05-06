package com.example.myapplication.extensionfunctions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.Base64
import android.util.Log
import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun CharSequence.isValidEmail() = EMAIL_ADDRESS.matcher(this).matches()

fun String.fromYYYYMMDD(): String {
    if (isEmpty()) return this
    val a = separated()

    var year = a[0]
    if (year.count() == 2) {
        year = "20$year"
    }
    if (a.size == 1) return year

    var month = a[1]
    if (month.count() == 1) {
        month = "0$month"
    }
    if (a.size == 2) return "$month/$year"

    var day = a[2]
    if (day.count() == 1) {
        day = "0$day"
    }

    return "$month/$day/$year"
}

fun String.toWindowsDate(): String {
    val components = separated()
    if (components.size < 3) return ""
    var year = components[2]
    if (year.length == 2) {
        year = "20$year"
    }
    return "$year-${components[0]}-${components[1]}T00:00:00.000Z"
}


fun String.toYYYYMMDD(): String {
    if (isEmpty()) return this
    val a = separated()
    if (a.size == 1) return a[0]
    if (a.size == 2) return "${a[1]}-${a[0]}"
    return "${a[2]}-${a[1]}-${a[0]}"
}

fun String.fromWindowsToString(): String {
    if (isEmpty()) return this
    val a = split("T").toTypedArray()
    return a[0].fromYYYYMMDD()
}

fun String.fromWindowsToStringWithDash(): String {
    if (isEmpty()) return this
    val a = split("T").toTypedArray()
    return a[0].fromYYYYMMDD().replace("/", "-")
}

fun String.separated(): Array<String> {
    val a = split("/").toTypedArray()
    if (a.size == 1) return split("-").toTypedArray()
    return a
}

fun String.phoneToDigits(): String {
    return replace("\\D".toRegex(), "")
}

fun String.toDate(): Date {
    val justDate = fromWindowsToString()
    val delim = if (contains("/")) "/" else if (contains("-")) "-" else ""
    if (justDate.isBlank() || delim.isBlank()) return Date()
    val format = SimpleDateFormat("mm${delim}dd${delim}yyyy", Locale.getDefault())
    return try {
        format.parse(justDate)
    } catch (e: Exception) {
        Date()
    }
}

fun String.toCalendar(): Calendar {
    val delim = if (contains("/")) "/" else if (contains("-")) "-" else ""
    if (isBlank() || delim.isBlank()) return Calendar.getInstance()
    val cal = Calendar.getInstance()
    try {
        cal.time = SimpleDateFormat("MM${delim}dd${delim}yyyy", Locale.getDefault()).parse(this)
    } catch (e: ParseException) {
        Log.e("Parse error", e.toString())
    }
    return cal
}

fun String.fromWindowsToDate(): Calendar {
    if (isBlank()) return Calendar.getInstance()
    val cal = Calendar.getInstance()
    try {
        cal.time = SimpleDateFormat("yyy-MM-dd", Locale.getDefault()).parse(split("T").toTypedArray()[0])
    } catch (e: ParseException) {
        Log.e("Parse error", e.toString())
    }
    return cal
}

fun String.toBitmap(): Bitmap {
    val decodedString = to64BaseByteArray()
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
}

fun String.to64BaseByteArray(): ByteArray = Base64.decode(this, Base64.DEFAULT)!!

fun String.toHTML(): Spanned? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
    else Html.fromHtml(this)

fun String.isBetween(s1: String, s2: String) = this in s1..s2