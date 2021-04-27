package com.aaudin90.glcardrender.internal.entity

import android.graphics.Bitmap
import java.nio.FloatBuffer

internal data class Data3D(
    val vertexBuffer: FloatBuffer,
    val normalsBuffer: FloatBuffer,
    val textureBuffer: FloatBuffer,
    val meshData: MeshData,
    val specularMap: Bitmap,
    val texture: Bitmap?
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Data3D) return false

        if (vertexBuffer != other.vertexBuffer) return false
        if (normalsBuffer != other.normalsBuffer) return false
        if (textureBuffer != other.textureBuffer) return false
        if (meshData != other.meshData) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vertexBuffer.hashCode()
        result = 31 * result + normalsBuffer.hashCode()
        result = 31 * result + textureBuffer.hashCode()
        result = 31 * result + meshData.hashCode()
        return result
    }
}