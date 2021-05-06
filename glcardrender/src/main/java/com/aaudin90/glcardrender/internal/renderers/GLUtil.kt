package com.aaudin90.glcardrender.internal.renderers

import android.graphics.Bitmap
import android.opengl.GLES30
import android.opengl.GLUtils.texImage2D
import android.opengl.Matrix
import android.util.Log
import com.aaudin90.glcardrender.internal.entity.MeshData
import kotlin.math.abs

internal object GLUtil {
    private const val TAG = "GLUtil"

    /**
     * Utility method for compiling a OpenGL shader.
     *
     *
     *
     * **Note:** When developing shaders, use the checkGlError() method to debug shader coding errors.
     *
     *
     * @param type       - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    fun loadShader(type: Int, shaderSrc: String): Int {
        val compiled = IntArray(1)

        // Create the shader object
        val shader: Int = GLES30.glCreateShader(type)
        if (shader == 0) {
            return 0
        }

        // Load the shader source
        GLES30.glShaderSource(shader, shaderSrc)

        // Compile the shader
        GLES30.glCompileShader(shader)

        // Check the compile status
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            Log.e("ssssss", GLES30.glGetShaderInfoLog(shader))
            GLES30.glDeleteShader(shader)
            return 0
        }
        return shader
    }

    fun loadTexture(bitmap: Bitmap, activeTextureIndex: Int): Int {
        val textureHandle = IntArray(1)
        GLES30.glGenTextures(1, textureHandle, 0)
        checkGlError("glGenTextures")
        if (textureHandle[0] == 0) {
            throw RuntimeException("Error loading texture.")
        }
        GLES30.glPixelStorei(GLES30.GL_UNPACK_ALIGNMENT, 1);

        GLES30.glActiveTexture(activeTextureIndex)
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0])
        checkGlError("glBindTexture")
        texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)
        checkGlError("texImage2D")
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D)

        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_WRAP_S,
            GLES30.GL_REPEAT
        )
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_WRAP_T,
            GLES30.GL_REPEAT
        )
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MIN_FILTER,
            GLES30.GL_NEAREST
        )
        GLES30.glTexParameteri(
            GLES30.GL_TEXTURE_2D,
            GLES30.GL_TEXTURE_MAG_FILTER,
            GLES30.GL_NEAREST
        )
        Log.v("GLUtil", "Loaded texture ok")

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0)
        return textureHandle[0]
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call just after making it:
     *
     * <pre>
     * mColorHandle = GLES30.glGetUniformLocation(mProgram, &quot;vColor&quot;);
     * MyGLRenderer.checkGlError(&quot;glGetUniformLocation&quot;);
    </pre> *
     *
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    fun checkGlError(glOperation: String): Boolean {
        var glError: Int
        var error = false
        while (GLES30.glGetError().also { glError = it } != GLES30.GL_NO_ERROR) {
            Log.e(TAG, "$glOperation: glError $glError")
            error = true
            Log.e(TAG, Thread.currentThread().stackTrace[3].toString())
            Log.e(TAG, Thread.currentThread().stackTrace[4].toString())
            Log.e(TAG, Thread.currentThread().stackTrace[5].toString())
            Log.e(TAG, Thread.currentThread().stackTrace[6].toString())
        }
        return error
    }

    /**
     * Проверяет находится ли объект полностью в конусе видимости камеры.
     */
    fun inFrustum(
        modelMatrix: FloatArray,
        viewMatrix: FloatArray,
        projectionMatrix: FloatArray,
        meshData: MeshData
    ): Boolean {
        val mVPMatrix = FloatArray(16)
        Matrix.multiplyMM(mVPMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, mVPMatrix, 0)

        val resultVector = FloatArray(4)
        var inFrustum = true
        meshData.vertices.forEach { vertexArray ->
            val vertexArray4 = vertexArray.copyOf(4).also {
                it[3] = 1f
            }
            Matrix.multiplyMV(resultVector, 0, mVPMatrix, 0, vertexArray4, 0)

            inFrustum = abs(resultVector[0]) < resultVector[3]
                    && abs(resultVector[1]) < resultVector[3]
                    && abs(resultVector[2]) < resultVector[3]

            if (!inFrustum) return inFrustum
        }
        return inFrustum
    }
}