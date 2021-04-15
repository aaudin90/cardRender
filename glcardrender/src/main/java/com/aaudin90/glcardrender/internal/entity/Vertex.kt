package com.aaudin90.glcardrender.internal.entity

internal data class Vertex(
    val vertexIndex: Int,
    val textureIndex: Int = NO_INDEX,
    val normalIndex: Int = NO_INDEX,
    val colorIndex: Int = NO_INDEX
    //val weightsData: VertexSkinData? = null
) {

    private companion object {
        const val NO_INDEX = -1
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vertex) return false

        if (vertexIndex != other.vertexIndex) return false
        if (textureIndex != other.textureIndex) return false
        if (normalIndex != other.normalIndex) return false
        if (colorIndex != other.colorIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vertexIndex
        result = 31 * result + textureIndex
        result = 31 * result + normalIndex
        result = 31 * result + colorIndex
        return result
    }
}