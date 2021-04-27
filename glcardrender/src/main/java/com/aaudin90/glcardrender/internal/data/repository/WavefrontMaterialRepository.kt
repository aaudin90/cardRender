package com.aaudin90.glcardrender.internal.data.repository

import com.aaudin90.glcardrender.internal.data.source.WavefrontMaterialDataSource
import com.aaudin90.glcardrender.internal.entity.Material

//Если потребуется использовать несколько материалов для карты
internal class WavefrontMaterialRepository(
    private val wavefrontMaterialDataSource: WavefrontMaterialDataSource
) {

    fun get(): Material =
        wavefrontMaterialDataSource.get()
}