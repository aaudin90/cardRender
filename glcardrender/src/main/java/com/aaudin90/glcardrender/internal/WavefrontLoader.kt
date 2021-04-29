package com.aaudin90.glcardrender.internal

import com.aaudin90.glcardrender.internal.data.repository.WavefrontMeshRepository
import com.aaudin90.glcardrender.internal.domain.FixNormalsMapper
import com.aaudin90.glcardrender.internal.entity.MeshData

internal class WavefrontLoader(
    private val wavefrontMeshRepository: WavefrontMeshRepository,
    private val fixNormalsMapper: FixNormalsMapper
) {

    @Volatile
    lateinit var mesh: MeshData
        private set

    fun init() {
        mesh = wavefrontMeshRepository
            .get()
            .let {
                fixNormalsMapper.map(it)
            }
    }
}