package com.aaudin90.glcardrender.internal

import com.aaudin90.glcardrender.internal.data.repository.TextureRepository
import com.aaudin90.glcardrender.internal.data.repository.WavefrontMaterialRepository
import com.aaudin90.glcardrender.internal.data.repository.WavefrontMeshRepository
import com.aaudin90.glcardrender.internal.domain.FixNormalsMapper
import com.aaudin90.glcardrender.internal.entity.Data3D
import com.aaudin90.glcardrender.internal.entity.Material
import com.aaudin90.glcardrender.internal.entity.MeshData

internal class WavefrontLoader(
    private val wavefrontMeshRepository: WavefrontMeshRepository,
    private val wavefrontMaterialRepository: WavefrontMaterialRepository,
    private val textureRepository: TextureRepository,
    private val fixNormalsMapper: FixNormalsMapper
) {

    @Volatile
    lateinit var mesh: MeshData
        private set

    @Volatile
    private lateinit var material: Material

    fun init() {
        mesh = wavefrontMeshRepository
            .get()
            .let {
                fixNormalsMapper.map(it)
            }
        material = wavefrontMaterialRepository.get()
    }

    fun getData3D(): Data3D {
        val material = material.copy(textureData = textureRepository.get())

        return Data3D(
            mesh.vertexBuffer,
            mesh.normalsBuffer,
            mesh.textureBuffer,
            mesh,
            mesh.element.copy(material = material)
        )
    }
}