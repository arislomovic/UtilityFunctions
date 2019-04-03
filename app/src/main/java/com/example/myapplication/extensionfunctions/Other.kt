package com.example.myapplication.extensionfunctions

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.text.format.DateFormat
import android.util.Base64
import androidx.core.content.FileProvider
import androidx.print.PrintHelper
import com.example.myapplication.BuildConfig
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

fun Calendar.asString() = DateFormat.format("MM/dd/yyyy", time).toString()
fun Float.isBetween(i1: Double, i2: Double) = this > i1 && this < i2
fun Int.isBetween(int1: Int, int2: Int) = this in (int1 + 1)..(int2 - 1)
fun File.getFileProvidedUri(context: Context): Uri =
    FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.myfileprovider", this)

fun Activity.printBitmap(bitmap: Bitmap) {
    PrintHelper(this).apply {
        scaleMode = PrintHelper.SCALE_MODE_FIT
        printBitmap("PrintShop", bitmap)
    }
}

fun Array<out Any>.doesNotContain(any: Any) = !contains(any)
fun Collection<Any>.doesNotContain(any: Any) = !contains(any)

fun Bitmap.toByte64(): String {
    var byteArray = byteArrayOf()
    ByteArrayOutputStream().use {
        compress(Bitmap.CompressFormat.PNG, 100, it)
        byteArray = it.toByteArray()
    }
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}
