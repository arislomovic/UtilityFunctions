package com.example.myapplication

import android.os.AsyncTask
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class FileDownload(private val url: String, val file: String? = null, val onFileRetrieved: (file: File) -> Unit) :
    AsyncTask<String, String, String>() {
    override fun doInBackground(vararg params: String?): String {
        val root = MyApplication.getApplicationContext().filesDir.toString()
        val fileName = file?: "$root/downloaded_file"
        if (File(fileName).exists()) File(fileName).delete()
        try {
            val url = URL(url)
            url.openConnection().connect()
            BufferedInputStream(url.openStream(), 8192).use { input ->
                FileOutputStream(fileName).use { output ->
                    val data = ByteArray(1024)
                    var total = 0
                    var count = 0
                    while (count != -1) {
                        count = input.read(data)
                        if (count == -1) break
                        total += count
                        output.write(data, 0, count)
                    }
                    output.flush()
                }

            }
            onFileRetrieved(File(fileName))
            return ""
        } finally {
        }
    }
}