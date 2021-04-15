package com.aaudin90.glcardrender.internal.entity

import java.nio.FloatBuffer

internal data class Data3D(
    var vertexBuffer: FloatBuffer,
    val normalsBuffer: FloatBuffer,
    val textureBuffer: FloatBuffer,
    val meshData: MeshData,
    val element: Element
)