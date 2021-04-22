package com.aaudin90.glcardrender.internal.renderers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.ByteBuffer

object GLUtil {
    private const val TAG = "GLUtil"

    /**
     * Helper function to compile and link a program.
     *
     * @param vertexShaderHandle   An OpenGL handle to an already-compiled vertex shader.
     * @param fragmentShaderHandle An OpenGL handle to an already-compiled fragment shader.
     * @param attributes           Attributes that need to be bound to the program.
     * @return An OpenGL handle to the program.
     */
    fun createAndLinkProgram(
        vertexShaderHandle: Int, fragmentShaderHandle: Int,
        attributes: Array<String?>?
    ): Int {
        var programHandle: Int = GLES30.glCreateProgram()
        if (programHandle != 0) {
            // Bind the vertex shader to the program.
            GLES30.glAttachShader(programHandle, vertexShaderHandle)

            // Bind the fragment shader to the program.
            GLES30.glAttachShader(programHandle, fragmentShaderHandle)

            // Bind attributes
            if (attributes != null) {
                val size = attributes.size
                for (i in 0 until size) {
                    GLES30.glBindAttribLocation(programHandle, i, attributes[i])
                }
            }

            // Link the two shaders together into a program.
            GLES30.glLinkProgram(programHandle)

            // Get the link status.
            val linkStatus = IntArray(1)
            GLES30.glGetProgramiv(programHandle, GLES30.GL_LINK_STATUS, linkStatus, 0)

            // If the link failed, delete the program.
            if (linkStatus[0] == 0) {
                Log.e(TAG, "Error compiling program: " + GLES30.glGetProgramInfoLog(programHandle))
                GLES30.glDeleteProgram(programHandle)
                programHandle = 0
            }
        }
        if (programHandle == 0) {
            throw RuntimeException("Error creating program.")
        }
        return programHandle
    }

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
    fun loadShader(type: Int, shaderCode: String): Int {

        // create a vertex shader type (GLES30.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
        val shader: Int = GLES30.glCreateShader(type)

        // add the source code to the shader and compile it
        GLES30.glShaderSource(shader, shaderCode)
        GLES30.glCompileShader(shader)
        val compiled = IntArray(1)
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0)
        Log.d("GLUtil", "Shader compilation info: " + GLES30.glGetShaderInfoLog(shader))
        if (compiled[0] == 0) {
            Log.e(
                "GLUtil", """
     Shader error: ${GLES30.glGetShaderInfoLog(shader)}
     $shaderCode
     """.trimIndent()
            )
            GLES30.glDeleteShader(shader)
        }
        return shader
    }

    fun loadTexture(textureData: ByteArray): Int {
        ByteArrayInputStream(textureData).use { textureIs ->
            Log.v("GLUtil", "Loading texture from stream...")
            val textureHandle = IntArray(1)
            GLES30.glGenTextures(1, textureHandle, 0)
            checkGlError("glGenTextures")
            if (textureHandle[0] == 0) {
                throw RuntimeException("Error loading texture.")
            }
            Log.v("GLUtil", "Handler: " + textureHandle[0])
            //val bitmap: Bitmap = loadBitmap(textureIs)

            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inScaled = false
            options.inJustDecodeBounds = true
            val bitmap = BitmapFactory.decodeStream(textureIs, null, options)
            // Bind to the texture in OpenGL
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0])
            checkGlError("glBindTexture")
            //texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0)
            GLES30.glTexImage2D(
                GLES30.GL_TEXTURE_2D,
                0,
                GLES30.GL_RGB,
                bitmap?.width ?: 0,
                bitmap?.height ?: 0,
                0,
                GLES30.GL_RGB,
                GLES30.GL_UNSIGNED_BYTE,
                ByteBuffer.wrap(textureData)
            )
            checkGlError("texImage2D")
            GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D)
            bitmap?.recycle()
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
    }

    private fun loadBitmap(bytes: ByteArray): Bitmap {
        val options: BitmapFactory.Options = BitmapFactory.Options()
        // By default, Android applies pre-scaling to bitmaps depending on the resolution of your device and which
        // resource folder you placed the image in. We don’t want Android to scale our bitmap at all, so to be sure,
        // we set inScaled to false.
        options.inScaled = false

        // Read in the resource
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
            ?: throw RuntimeException("couldn't load bitmap")
    }

    private fun loadBitmap(inputStream: InputStream): Bitmap {
        val options: BitmapFactory.Options = BitmapFactory.Options()
        // By default, Android applies pre-scaling to bitmaps depending on the resolution of your device and which
        // resource folder you placed the image in. We don’t want Android to scale our bitmap at all, so to be sure,
        // we set inScaled to false.
        options.inScaled = false
        options.inJustDecodeBounds = true
        // Read in the resource
        return BitmapFactory.decodeStream(inputStream, null, options)
            ?: throw RuntimeException("couldn't load bitmap")
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
    private fun checkGlError(glOperation: String): Boolean {
        var glError: Int
        var error = false
        while (GLES30.glGetError().also { glError = it } != GLES30.GL_NO_ERROR) {
            Log.e(TAG, "$glOperation: glError $glError")
            error = true
            Log.e(TAG, Thread.currentThread().stackTrace[3].toString())
            Log.e(TAG, Thread.currentThread().stackTrace[4].toString())
            Log.e(TAG, Thread.currentThread().stackTrace[5].toString())
            Log.e(TAG, Thread.currentThread().stackTrace[6].toString())

            // throw new RuntimeException(glOperation + ": glError " + error);
        }
        return error
    }
}