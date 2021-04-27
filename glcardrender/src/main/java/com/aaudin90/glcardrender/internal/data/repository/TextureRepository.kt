package com.aaudin90.glcardrender.internal.data.repository

import android.graphics.Bitmap
import com.aaudin90.glcardrender.internal.data.source.TextureDataSource

class TextureRepository(
    private val textureDataSource: TextureDataSource
) {

    fun get(assetName: String): Bitmap =
        textureDataSource.get(assetName)
}