package com.aaudin90.glcardrender.internal.entity

internal data class Material(
    val name: String,
    val ambient: FloatArray,
    val diffuse: FloatArray,
    val specular: FloatArray,
    val shininess: Float = 0f,
    val alpha: Float = 1.0f,
    val textureFile: String,
    val textureData: ByteArray? = null,
    val textureId: Int = -1,
    val color: FloatArray? = null
) {


    companion object {
        private val COLOR_WHITE = floatArrayOf(1f, 1f, 1f, 1f)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Material) return false

        if (name != other.name) return false
        if (shininess != other.shininess) return false
        if (alpha != other.alpha) return false
        if (textureFile != other.textureFile) return false
        if (textureId != other.textureId) return false
        if (!color.contentEquals(other.color)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + shininess.hashCode()
        result = 31 * result + alpha.hashCode()
        result = 31 * result + textureFile.hashCode()
        result = 31 * result + textureId
        result = 31 * result + color.contentHashCode()
        return result
    }
}