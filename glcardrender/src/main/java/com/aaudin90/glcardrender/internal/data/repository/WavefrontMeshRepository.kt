package com.aaudin90.glcardrender.internal.data.repository

import com.aaudin90.glcardrender.internal.data.source.WavefrontMeshDataSource
import com.aaudin90.glcardrender.internal.entity.MeshData

internal class WavefrontMeshRepository(
    private val wavefrontMeshDataSource: WavefrontMeshDataSource
) {

    fun get(): MeshData =
        wavefrontMeshDataSource.get()
}