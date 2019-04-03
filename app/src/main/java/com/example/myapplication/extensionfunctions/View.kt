package com.example.myapplication.extensionfunctions

import android.graphics.Typeface
import android.text.InputFilter
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible

fun TextView.validateWithError(error: Int) =
    validateWithAction { setError(context.getString(error)) }

fun TextView.validateWithError(error: Int, length: Int) =
    validateLength(length) { setError(context.getString(error)) }

fun TextView.validateLength(length: Int, onError: () -> Unit): String? {
    val input = text.toString().trim()
    if (input.length != length) {
        onError()
        return null
    }
    return input
}

fun TextView.validateWithAction(onError: () -> Unit): String? {
    val input = text.toString().trim()
    if (input.isEmpty()) {
        onError()
        return null
    }
    return input
}

fun TextView.setForPassword(){
    inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
    transformationMethod = PasswordTransformationMethod()
}

fun TextView.setForPhoneNumbers() {
    inputType = InputType.TYPE_CLASS_PHONE
    setMaxLength(10)
}
fun TextView.setNumberTextLength(length:Int) {
    inputType = InputType.TYPE_CLASS_NUMBER
    setMaxLength(length)
}

fun TextView.setForZipCode() {
    setNumberTextLength(5)
}

fun TextView.setMaxLength(length:Int){
    filters = Array<InputFilter>(1) { InputFilter.LengthFilter(length) }
}

fun TextView.putValidText(text: String?) {
    if (!text.isNullOrBlank()) setText(text)
}

fun TextView.removeError() {
    error = null
}

fun TextView.bold(){
    setTypeface(null, Typeface.BOLD)
}
fun TextView.italicize(){
    setTypeface(null, Typeface.ITALIC)
}
fun TextView.boldAndItalicize(){
    setTypeface(null, Typeface.BOLD_ITALIC)
}
fun TextView.removeTypeFace(){
    setTypeface(null, Typeface.NORMAL)
}


fun View.disable() {
    enable(false)
}

fun View.enable(enable: Boolean = true) {
    isClickable = enable
    isFocusable = enable
}

fun View.swapVisibility() {
    isVisible = !isVisible
}