package com.aaudin90.glcardrender.internal.renderers

import android.content.Context
import android.opengl.GLES30
import android.util.Log
import com.aaudin90.glcardrender.internal.domain.IOUtils
import java.nio.FloatBuffer

internal class MicroSunRenderer {

    var isInitialized: Boolean = false
        private set

    private var programIndex: Int = -1
    private lateinit var verticesBuffer: FloatBuffer

    fun init(context: Context) {
        val vertexShader = GLUtil.loadShader(
            GLES30.GL_VERTEX_SHADER,
            IOUtils.loadAssetAsString(context, "micro_sun_shader_v.glsl")
        )

        val fragmentShader = GLUtil.loadShader(
            GLES30.GL_FRAGMENT_SHADER,
            IOUtils.loadAssetAsString(context, "micro_sun_shader_f.glsl")
        )

        val programObject = GLES30.glCreateProgram()
        if (programObject == 0) {
            throw Exception("programObject == 0")
        }
        GLES30.glAttachShader(programObject, vertexShader)
        GLES30.glAttachShader(programObject, fragmentShader)
        GLES30.glLinkProgram(programObject)

        val linked = IntArray(1)
        GLES30.glGetProgramiv(programObject, GLES30.GL_LINK_STATUS, linked, 0)
        if (linked[0] == 0) {
            Log.e("sssss", "Error linking program:")
            Log.e("sssss", GLES30.glGetProgramInfoLog(programObject))
            GLES30.glDeleteProgram(programObject)
            throw Exception("Error linking program:")
        }
        verticesBuffer = createVerticesBuffer()

        // Store the program object
        programIndex = programObject
        isInitialized = true
    }

    fun draw(
        modelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray
    ) {
        if (!isInitialized) return

        GLES30.glUseProgram(programIndex)

        setMVPData(modelMatrix, viewMatrix, projectionMatrix)
        setVertexData()
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, verticesBuffer.capacity() / 3)
    }

    private fun setVertexData() {
        val vertexHandler = GLES30.glGetAttribLocation(programIndex, "a_Position")

        verticesBuffer.position(0)
        GLES30.glVertexAttribPointer(
            vertexHandler, 3, GLES30.GL_FLOAT,
            false, 0, verticesBuffer
        )
        GLES30.glEnableVertexAttribArray(vertexHandler)
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
    }

    private fun createVerticesBuffer(): FloatBuffer {
        val buffer = IOUtils.createFloatBuffer(vertices.size * 3)
        for (i in vertices.indices) buffer.put(vertices[i])
        return buffer
    }

    private companion object {
        val vertices = floatArrayOf(
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, -0.5f
        )
    }
}