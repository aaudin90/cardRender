package com.aaudin90.glcardrender.internal.renderers

import android.content.Context
import android.opengl.GLES30
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

        textureIndex = if (renderData.element.material?.textureData != null) {
            GLUtil.loadTexture(renderData.element.material.textureData)
        } else {
            -1
        }

        val linked = IntArray(1)
        // Check the link status
        GLES30.glGetProgramiv(programObject, GLES30.GL_LINK_STATUS, linked, 0)
        if (linked[0] == 0) {
            Log.e("sssss", "Error linking program:")
            Log.e("sssss", GLES30.glGetProgramInfoLog(programObject))
            GLES30.glDeleteProgram(programObject)
            throw Exception("Error linking program:")
        }
        // Store the program object
        programIndex = programObject
        isInitialized = true
    }

    fun draw(mvpMatrix: FloatArray) {
        if (!isInitialized) return
        GLES30.glUseProgram(programIndex)
        setTextureData()
        setVertexData()
        setMVPData(mvpMatrix)

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, renderData.vertexBuffer.capacity() / 3)
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
    }

    private fun setMVPData(mvpMatrix: FloatArray) {
        val mVPMatrixHandle = GLES30.glGetUniformLocation(programIndex, "uMVPMatrix")
        GLES30.glUniformMatrix4fv(mVPMatrixHandle, 1, false, mvpMatrix, 0)
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
}