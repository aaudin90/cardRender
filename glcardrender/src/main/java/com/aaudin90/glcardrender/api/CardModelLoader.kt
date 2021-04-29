package com.aaudin90.glcardrender.api

import android.app.Application
import android.graphics.Bitmap
import com.aaudin90.glcardrender.internal.InternalServiceLocator
import com.aaudin90.glcardrender.internal.entity.Data3D

class CardModelLoader(
    application: Application
) {
    private val serviceLocator = InternalServiceLocator(application)

    init {
        serviceLocator.wavefrontLoader.init()
    }

    inner class Data3DProvider(
        private val texture: Bitmap?,
        private val specularMap: Bitmap = serviceLocator.textureRepository.get("gloss.png"),
        private val specularMapStrength: Float = 0.5f,
        private val specularMapLightColor: FloatArray = floatArrayOf(1f, 1f, 1f),
        private val specularTextureStrength: Float = 0.3f,
        private val specularTextureLightColor: FloatArray = floatArrayOf(1f, 1f, 1f),
        private val diffuseStrength: Float = 0.7f,
        private val diffuseLightColor: FloatArray = floatArrayOf(1f, 1f, 1f),
        private val ambientStrength: Float = 0.4f,
        private val ambientLightColor: FloatArray = floatArrayOf(1f, 1f, 1f)
    ) {

        internal fun getData3D(): Data3D {
            val mesh = serviceLocator.wavefrontLoader.mesh

            return Data3D(
                mesh.vertexBuffer,
                mesh.normalsBuffer,
                mesh.textureBuffer,
                mesh,
                specularMap,
                texture,
                specularMapStrength,
                specularMapLightColor,
                specularTextureStrength,
                specularTextureLightColor,
                diffuseStrength,
                diffuseLightColor,
                ambientStrength,
                ambientLightColor
            )
        }
    }
}