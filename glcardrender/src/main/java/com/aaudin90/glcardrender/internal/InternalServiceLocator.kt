package com.aaudin90.glcardrender.internal

import android.app.Application
import com.aaudin90.glcardrender.internal.data.repository.TextureRepository
import com.aaudin90.glcardrender.internal.data.repository.WavefrontMeshRepository
import com.aaudin90.glcardrender.internal.data.source.TextureDataSource
import com.aaudin90.glcardrender.internal.data.source.WavefrontMeshDataSource
import com.aaudin90.glcardrender.internal.domain.FixNormalsMapper

internal class InternalServiceLocator(
    private val application: Application
) {
    val wavefrontLoader by lazy {
        WavefrontLoader(
            wavefrontDataRepository,
            fixNormalsMapper
        )
    }

    val textureRepository by lazy {
        TextureRepository(
            TextureDataSource(application)
        )
    }

    private val fixNormalsMapper by lazy {
        FixNormalsMapper()
    }

    private val wavefrontDataRepository by lazy {
        WavefrontMeshRepository(
            wavefrontMeshDataSource
        )
    }

    private val wavefrontMeshDataSource by lazy {
        WavefrontMeshDataSource(application)
    }
}