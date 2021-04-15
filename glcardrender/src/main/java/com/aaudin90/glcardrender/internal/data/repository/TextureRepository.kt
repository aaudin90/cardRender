package com.aaudin90.glcardrender.internal.data.repository

import com.aaudin90.glcardrender.internal.data.source.TextureDataSource

class TextureRepository(
    private val textureDataSource: TextureDataSource
) {

    fun get():ByteArray =
        textureDataSource.get()
}