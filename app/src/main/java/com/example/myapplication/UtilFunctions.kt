package com.example.myapplication

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.print.PrintAttributes
import android.print.PrintManager
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.myapplication.extensionfunctions.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

object UtilFunctions {


    const val emptyGuide = "00000000-0000-0000-0000-000000000000"


    fun toast(text: String) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show()
    }

    fun toast(text: Int) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show()
    }

    fun showDialogMessage(context: Context, message: Int = 0, stringMessage: String = "", title: Int = 0) {
        val dialog = AlertDialog
            .Builder(context)
            .setTitle(title)
            .setNeutralButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
        if (message == 0) dialog.setMessage(stringMessage) else dialog.setMessage(message)
        dialog.show()
    }

    fun getTextFromHTML(input: String) =
        AppCompatTextView(getContext()).apply { text = input.toHTML() }.text.toString()


    fun stringListToString(mutableList: MutableList<String>): String {
        val a = StringBuilder("")
        for (i in mutableList) a.append(i).append(" ")
        return a.toString()
    }

    @Throws(IOException::class)
    fun readBytes(inputStream: InputStream): ByteArray { // this dynamically extends to take the bytes you read

        val buffer = ByteArray(1024)
        var len = 0
        var byteArray = byteArrayOf()
        ByteArrayOutputStream().use {
            while (len != -1) {
                len = inputStream.read(buffer)
                if (len != -1) it.write(buffer, 0, len)
            }
            byteArray = it.toByteArray()
        }
        return byteArray
    }


    fun getFileName(uri: Uri): String {
        var result = ""
        if (uri.scheme == "content") {
            val cursor = getContext().contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (it!!.moveToFirst()) {
                    result = it.getString(cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result.isEmpty()) {
            result = uri.path ?: ""
            val cut = result.lastIndexOf('/')
            if (cut != -1) result = result.substring(cut + 1)
        }
        return result
    }

    fun uriToBase64(uri: Uri): String = try {
        val ips = getContext().contentResolver.openInputStream(uri)!!
        Base64.encodeToString(readBytes(ips), Base64.DEFAULT) ?: ""
    } catch (e: IOException) {
        Log.e("Email Us", e.localizedMessage)
        ""
    }

    fun enableViews(vararg views: View) {
        for (i in views) i.enable()
    }

    fun disableViews(vararg views: View) {
        for (i in views) i.disable()
    }

    fun hideKeyboard(view: View) {
        (view
            .context
            .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun removeErrors(vararg views: TextView) {
        for (i in views) i.removeError()
    }


    fun getDateInXMonths(months: Int): Calendar {
        val cal = Calendar.getInstance()!!
        cal.add(Calendar.MONTH, months)
        return cal
    }


    fun getRangeOfInts(i1: Int, i2: Int): Array<String> =
        if (i2 < i1) arrayOf("") else (i1..i2).map { it.toString() }.toTypedArray()

    fun getDoubleDigitRangeOfInts(i1: Int, i2: Int): Array<String> =
        if (i2 < i1) arrayOf("")
        else (i1..i2).map { if (it.isBetween(0, 10)) "0$it" else it.toString() }.toTypedArray()

    fun getNextXYears(years: Int): Array<String> =
        if (years < 0) arrayOf("")
        else getRangeOfInts(
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.YEAR) + years
        )

    fun swapViewsVisibility(vararg views: View) {
        for (i in views) i.swapVisibility()
    }

    fun getColorCompat(id: Int) = ResourcesCompat.getColor(getContext().resources, id, null)

    fun getColor(id: Int): ColorStateList =
        ColorStateList.valueOf(ContextCompat.getColor(getContext(), id))

    fun uriFromIntent(data: Intent, context: Context, mimeTypes: Int): Uri? {
        val mData = data.data ?: return null
        val mimeType = context.contentResolver.getType(mData) ?: return null
        if (context.resources.getStringArray(mimeTypes).doesNotContain(mimeType)) {
            UtilFunctions.showDialogMessage(
                context,
                title = R.string.error,
                message = R.string.invalid_attachment
            )
            return null
        }
        return mData
    }

    fun printHtmlString(activity: Activity, input: String) {
        val webView = WebView(activity)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) = false
            override fun onPageFinished(view: WebView, url: String) {
                val jobName = "${activity.getString(R.string.app_name)} Document"
                (activity.getSystemService(Context.PRINT_SERVICE) as? PrintManager)?.print(
                    jobName, webView.createPrintDocumentAdapter(jobName), PrintAttributes.Builder().build()
                )
            }
        }
        webView.loadDataWithBaseURL(null, input, "text/HTML", "UTF-8", null)
    }

    fun getUTCTime()  = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date())!!


    fun pdfToBitmaps(file: File): ArrayList<Bitmap> {
        val list = ArrayList<Bitmap>()
        PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)).use {
            for (pageNumber in 0 until it.pageCount) {
                it.openPage(pageNumber).use { page ->
                    val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    list.add(bitmap)
                }
            }
        }
        return list
    }

    fun getContext() = MyApplication.getApplicationContext()
}
