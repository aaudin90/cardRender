package com.aaudin90.glcardrender.internal.renderers

import android.content.Context
import android.opengl.GLES30
import android.opengl.Matrix
import android.util.Log
import com.aaudin90.glcardrender.internal.domain.IOUtils.loadAssetAsString
import com.aaudin90.glcardrender.internal.entity.Data3D
import com.aaudin90.glcardrender.internal.renderers.GLUtil.loadShader

internal class CardRenderer(
    val renderData: Data3D
) {
    var isInitialized: Boolean = false
        private set

    private var programIndex: Int = -1
    private var textureIndex: Int = -1
    private var specularMapIndex: Int = -1

    fun init(context: Context) {
        val vertexShader = loadShader(
            GLES30.GL_VERTEX_SHADER,
            loadAssetAsString(context, "card_shader_v.glsl")
        )

        val fragmentShader = loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            loadAssetAsString(context, "card_shader_f.glsl")
        )

        val programObject = GLES30.glCreateProgram()
        if (programObject == 0) {
            throw Exception("programObject == 0")
        }
        GLES30.glAttachShader(programObject, vertexShader)
        GLES30.glAttachShader(programObject, fragmentShader)

        GLES30.glBindAttribLocation(programObject, 6, "a_Position")
        GLES30.glBindAttribLocation(programObject, 0, "a_TexPosition")

        // Link the program
        GLES30.glLinkProgram(programObject)

        textureIndex = if (renderData.texture != null) {
            GLUtil.loadTexture(renderData.texture, GLES30.GL_TEXTURE0)
        } else {
            -1
        }

        specularMapIndex = GLUtil.loadTexture(renderData.specularMap, GLES30.GL_TEXTURE1)

        val linked = IntArray(1)
        // Check the link status
        GLES30.glGetProgramiv(programObject, GLES30.GL_LINK_STATUS, linked, 0)
        if (linked[0] == 0) {
            Log.e(TAG, "Error linking program:")
            Log.e(TAG, GLES30.glGetProgramInfoLog(programObject))
            GLES30.glDeleteProgram(programObject)
            throw Exception("Error linking program:")
        }
        // Store the program object
        programIndex = programObject
        isInitialized = true
    }

    fun release() {
        GLES30.glUseProgram(programIndex)
        GLES30.glDeleteTextures(1, intArrayOf(textureIndex), 0)
        GLES30.glDeleteTextures(1, intArrayOf(specularMapIndex), 0)
        GLES30.glDeleteProgram(programIndex)
    }

    fun draw(
        modelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
        lightPosition: FloatArray
    ) {
        if (!isInitialized) return
        GLES30.glUseProgram(programIndex)
        setLightProperties()
        setTextureData()
        setVertexData()
        setNormalsData(modelMatrix)
        setLightPosition(lightPosition)
        setMVPData(modelMatrix, viewMatrix, projectionMatrix)

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, renderData.vertexBuffer.capacity() / 3)
    }

    private fun setLightProperties() {
        val ambientStrengthHandle = GLES30.glGetUniformLocation(programIndex, "ambientStrength")
        GLES30.glUniform1f(ambientStrengthHandle, renderData.ambientStrength)
        val ambientLightColorHandle = GLES30.glGetUniformLocation(programIndex, "ambientLightColor")
        GLES30.glUniform3fv(ambientLightColorHandle, 1, renderData.ambientLightColor, 0)

        val diffuseStrengthHandle = GLES30.glGetUniformLocation(programIndex, "diffuseStrength")
        GLES30.glUniform1f(diffuseStrengthHandle, renderData.diffuseStrength)
        val diffuseLightColorHandle = GLES30.glGetUniformLocation(programIndex, "diffuseLightColor")
        GLES30.glUniform3fv(diffuseLightColorHandle, 1, renderData.diffuseLightColor, 0)

        val specularMapStrengthHandle =
            GLES30.glGetUniformLocation(programIndex, "specularMapStrength")
        GLES30.glUniform1f(specularMapStrengthHandle, renderData.specularMapStrength)
        val specularMapLightColorHandle =
            GLES30.glGetUniformLocation(programIndex, "specularMapLightColor")
        GLES30.glUniform3fv(specularMapLightColorHandle, 1, renderData.specularMapLightColor, 0)

        val specularTextureStrengthHandle =
            GLES30.glGetUniformLocation(programIndex, "specularTextureStrength")
        GLES30.glUniform1f(specularTextureStrengthHandle, renderData.specularTextureStrength)
        val specularTextureLightColorHandle =
            GLES30.glGetUniformLocation(programIndex, "specularTextureLightColor")
        GLES30.glUniform3fv(
            specularTextureLightColorHandle,
            1,
            renderData.specularTextureLightColor,
            0
        )
    }

    private fun setLightPosition(lightPosition: FloatArray) {
        val lightPosHandle = GLES30.glGetUniformLocation(programIndex, "vLightPos")
        GLES30.glUniform3fv(lightPosHandle, 1, lightPosition, 0)
    }

    private fun setNormalsData(modelMatrix: FloatArray) {
        val normalsHandler = GLES30.glGetAttribLocation(programIndex, "a_Normals")
        renderData.normalsBuffer.position(0)
        GLES30.glVertexAttribPointer(
            normalsHandler, 3, GLES30.GL_FLOAT,
            false, 0, renderData.normalsBuffer
        )
        GLES30.glEnableVertexAttribArray(normalsHandler)

        val normalizedModelMatrix = FloatArray(16)
        val invertMatrix = FloatArray(16)
        Matrix.invertM(invertMatrix, 0, modelMatrix, 0)
        Matrix.transposeM(normalizedModelMatrix, 0, invertMatrix, 0)
        val normalizedModelMatrixHandle =
            GLES30.glGetUniformLocation(programIndex, "normalizedModelMatrix")
        GLES30.glUniformMatrix4fv(normalizedModelMatrixHandle, 1, false, normalizedModelMatrix, 0)
    }

    private fun setTextureData() {
        val texHandler = GLES30.glGetAttribLocation(programIndex, "a_TexPosition")
        renderData.textureBuffer.position(0)
        GLES30.glVertexAttribPointer(
            texHandler, 2, GLES30.GL_FLOAT,
            false, 0, renderData.textureBuffer
        )
        GLES30.glEnableVertexAttribArray(texHandler)

        val uTextureUnitHandler = GLES30.glGetUniformLocation(programIndex, "u_Texture")
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIndex)
        GLES30.glUniform1i(uTextureUnitHandler, 0)
        GLUtil.checkGlError("glUniform1i")

        val uSpecularMapHandler = GLES30.glGetUniformLocation(programIndex, "u_SpecularMap")
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, specularMapIndex)
        GLES30.glUniform1i(uSpecularMapHandler, 1)
        GLUtil.checkGlError("glUniform1i")
    }

    private fun setMVPData(
        modelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray
    ) {
        val modelMatrixHandle = GLES30.glGetUniformLocation(programIndex, "modelMatrix")
        val viewMatrixHandle = GLES30.glGetUniformLocation(programIndex, "viewMatrix")
        val projectionMatrixHandle = GLES30.glGetUniformLocation(programIndex, "projectionMatrix")
        GLES30.glUniformMatrix4fv(modelMatrixHandle, 1, false, modelMatrix, 0)
        GLES30.glUniformMatrix4fv(viewMatrixHandle, 1, false, viewMatrix, 0)
        GLES30.glUniformMatrix4fv(projectionMatrixHandle, 1, false, projectionMatrix, 0)

        val cameraVector = FloatArray(3)
        val invertedViewMatrix = FloatArray(16)
        Matrix.invertM(invertedViewMatrix, 0, viewMatrix, 0)
        cameraVector.also {
            it[0] = invertedViewMatrix[7]
            it[0] = invertedViewMatrix[11]
            it[0] = invertedViewMatrix[15]
        }
        val viewPosHandle = GLES30.glGetUniformLocation(programIndex, "viewPos")
        GLES30.glUniform3fv(viewPosHandle, 1, cameraVector, 0)
    }

    private fun setVertexData() {
        val vertexHandler = GLES30.glGetAttribLocation(programIndex, "a_Position")
        renderData.vertexBuffer.position(0)
        GLES30.glVertexAttribPointer(
            vertexHandler, 3, GLES30.GL_FLOAT,
            false, 0, renderData.vertexBuffer
        )
        GLES30.glEnableVertexAttribArray(vertexHandler)
    }

    private companion object {
        const val TAG = "CardRenderer"
    }
}