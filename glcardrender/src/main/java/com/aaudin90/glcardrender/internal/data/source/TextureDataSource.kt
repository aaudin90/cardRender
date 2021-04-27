package com.aaudin90.glcardrender.internal.data.source

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class TextureDataSource(
    private val application: Application
) {

    fun get(assetName: String): Bitmap =
        application.assets.open(assetName).use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
}