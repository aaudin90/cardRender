package com.aaudin90.glcardrender.internal.renderers

import android.opengl.GLES30
import android.opengl.Matrix
import android.util.Log
import com.aaudin90.glcardrender.TestColor
import com.aaudin90.glcardrender.internal.domain.Math3DUtils
import com.aaudin90.glcardrender.internal.entity.Data3D

internal class CardRenderer(
    private val renderData: Data3D
) {

    var isInitialized: Boolean = false
        private set

    private var programIndex: Int = -1

    fun init() {
        val vertexShader = MainRenderer.loadShader(GLES30.GL_VERTEX_SHADER, vShaderStr)
        val fragmentShader = MainRenderer.loadShader(GLES30.GL_FRAGMENT_SHADER, fShaderStr)

        val programObject = GLES30.glCreateProgram()
        if (programObject == 0) {
            throw Exception("programObject == 0")
        }
        GLES30.glAttachShader(programObject, vertexShader)
        GLES30.glAttachShader(programObject, fragmentShader)

        GLES30.glBindAttribLocation(programObject, 0, "vPosition")

        // Link the program
        GLES30.glLinkProgram(programObject)

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

        // get handle to shape's transformation matrix
        val mVPMatrixHandle = GLES30.glGetUniformLocation(programIndex, "uMVPMatrix")
        val colorHandle = GLES30.glGetUniformLocation(programIndex, "vColor")

        GLES30.glUniformMatrix4fv(mVPMatrixHandle, 1, false, mvpMatrix, 0)

        renderData.vertexBuffer.position(0)
        GLES30.glVertexAttribPointer(
            0, 3, GLES30.GL_FLOAT,
            false, 0, renderData.vertexBuffer
        )
        GLES30.glEnableVertexAttribArray(0)

        val color = Math3DUtils.mult(yellow, gray)
        GLES30.glUniform4fv(colorHandle, 1, color, 0)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, renderData.vertexBuffer.capacity() / 3)
    }

    private companion object {
        //vertex shader code
        const val vShaderStr = """#version 300 es 			  
        uniform mat4 uMVPMatrix;     
        in vec4 vPosition;           
        void main()                  
        {                            
            gl_Position = uMVPMatrix * vPosition;  
        }                            
        """

        //fragment shader code.
        const val fShaderStr = """#version 300 es		 			          	
        precision mediump float;					  	
        uniform vec4 vColor;	 			 		  	
        out vec4 fragColor;	 			 		  	
        void main()                                  
        {                                            
            fragColor = vColor;                    	
        }                                            
        """


        val blue = TestColor.blue()
        val red = TestColor.red()
        val gray = TestColor.gray()
        val green = TestColor.green()
        val yellow = TestColor.yellow()
    }
}