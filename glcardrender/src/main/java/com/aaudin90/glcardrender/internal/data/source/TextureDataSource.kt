package com.aaudin90.glcardrender.internal.data.source

import android.app.Application
import java.io.ByteArrayOutputStream

class TextureDataSource(
    private val application: Application
) {

    fun get(): ByteArray {
        application.assets.open("sample.jpg").use { inputStream ->
            val isData = ByteArray(512)
            val buffer = ByteArrayOutputStream()
            var nRead: Int
            while (inputStream.read(isData, 0, isData.size).also { nRead = it } != -1) {
                buffer.write(isData, 0, nRead)
            }
            return buffer.toByteArray()
        }
    }
}