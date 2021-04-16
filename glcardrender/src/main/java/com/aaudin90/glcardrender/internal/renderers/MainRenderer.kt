package com.aaudin90.glcardrender.internal.renderers

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.aaudin90.glcardrender.Cube
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

internal class MainRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private var width = 0
    private var height = 0
    private lateinit var mCube: Cube
    private var mAngle = 0f

    //used the touch listener to move the cube up/down (y) and left/right (x)
    var y = 0f
    var x = 0f

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private val mMVPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mRotationMatrix = FloatArray(16)

    var cardRenderer: CardRenderer? = null

    ///
    // Initialize the shader and program object
    //
    override fun onSurfaceCreated(glUnused: GL10, config: EGLConfig) {

        // Set the background frame color

        // Set the background frame color
        GLES30.glClearColor(
            backgroundColor[0],
            backgroundColor[1],
            backgroundColor[2],
            backgroundColor[3]
        )

        GLES30.glEnable(GLES20.GL_DEPTH_TEST)

        // Enable not drawing out of view port

        // Enable not drawing out of view port
        GLES30.glEnable(GLES20.GL_SCISSOR_TEST)
        //initialize the cube code for drawing.
        mCube = Cube(0)
        //if we had other objects setup them up here as well.
    }

    // /
    // Draw a triangle using the shader pair created in onSurfaceCreated()
    //
    override fun onDrawFrame(glUnused: GL10) {
        GLES20.glViewport(0, 0, width, height)
        GLES20.glScissor(0, 0, width, height)

        // Clear the color buffer  set above by glClearColor.
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        //need this otherwise, it will over right stuff and the cube will look wrong!
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)

        // Set the camera position (View matrix)  note Matrix is an include, not a declared method.
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, -4f, 0f, 0f, 0f, 0f, 1f, 0f)

        // Create a rotation and translation for the cube
        Matrix.setIdentityM(mRotationMatrix, 0)

        //move the cube up/down and left/right
        Matrix.translateM(mRotationMatrix, 0, 0f, 0f, 10f)

        //mangle is how fast, x,y,z which directions it rotates.
        //Matrix.rotateM(mRotationMatrix, 0, -mAngle, 1.0f, 0f, 0f)
        Matrix.rotateM(mRotationMatrix, 0, mAngle, 0f, 1.0f, 0f)
        // combine the model with the view matrix
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mRotationMatrix, 0)

        // combine the model-view with the projection matrix
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0)
        mCube.draw(mMVPMatrix)
        cardRenderer?.apply {
            if (!isInitialized) {
                init()
            }
            draw(mMVPMatrix)
        }

        //change the angle, so the cube will spin.
//        if (mAngle > 180) {
//            mAngle = 180f
//        } else {
//            mAngle +=.4f
//        }
        //mAngle +=.4f
    }

    // /
    // Handle surface changes
    //
    override fun onSurfaceChanged(glUnused: GL10, width: Int, height: Int) {
        this.width = width
        this.height = height
        // Set the viewport
        GLES30.glViewport(0, 0, width, height)

        val aspect = width.toFloat() / height

        // this projection matrix is applied to object coordinates
        //no idea why 53.13f, it was used in another example and it worked.
        Matrix.perspectiveM(projectionMatrix, 0, 53.13f, aspect, Z_NEAR, Z_FAR)
    }

    companion object {
        private const val TAG = "myRenderer"
        private const val Z_NEAR = 1f
        private const val Z_FAR = 100f
        private val backgroundColor = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)

        fun loadShader(type: Int, shaderSrc: String?): Int {
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
                Log.e(TAG, "Erorr!!!!")
                Log.e(TAG, GLES30.glGetShaderInfoLog(shader))
                GLES30.glDeleteShader(shader)
                return 0
            }
            return shader
        }

        fun checkGlError(glOperation: String) {
            var error: Int
            while (GLES30.glGetError().also { error = it } != GLES30.GL_NO_ERROR) {
                Log.e(TAG, "$glOperation: glError $error")
                throw RuntimeException("$glOperation: glError $error")
            }
        }
    }
}