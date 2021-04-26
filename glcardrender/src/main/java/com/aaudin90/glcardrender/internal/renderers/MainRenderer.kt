package com.aaudin90.glcardrender.internal.renderers

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.aaudin90.glcardrender.internal.entity.MeshData
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

internal class MainRenderer(private val context: Context) : GLSurfaceView.Renderer {
    var y = 0f
    var x = 0f

    private val mVPMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)

    private var width: Int = 0
    private var height: Int = 0

    private var eyeZ = 0f
    private var isZCalculated = false

    var cardRenderer: CardRenderer? = null

    override fun onSurfaceCreated(glUnused: GL10, config: EGLConfig) {

        GLES30.glClearColor(
            backgroundColor[0],
            backgroundColor[1],
            backgroundColor[2],
            backgroundColor[3]
        )

        GLES30.glEnable(GLES20.GL_DEPTH_TEST)
        GLES30.glEnable(GLES20.GL_SCISSOR_TEST)
        cardRenderer?.init(context)
    }

    override fun onSurfaceChanged(glUnused: GL10, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        GLES20.glScissor(0, 0, width, height)
        this.width = width
        this.height = height

        cardRenderer?.let {
            if (!isZCalculated) {
                calculateZ(it.renderData.meshData)
            }
        }
    }

    override fun onDrawFrame(glUnused: GL10) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)

        calculateMVPMatrix()

        cardRenderer?.apply {
            if (!isZCalculated) {
                calculateZ(renderData.meshData)
            }

            if (!isInitialized) {
                init(context)
            }

            draw(mVPMatrix)
        }
    }

    private fun calculateMVPMatrix() {
        Matrix.setIdentityM(modelMatrix, 0)

        Matrix.perspectiveM(projectionMatrix, 0, 18f, width.toFloat() / height, Z_NEAR, Z_FAR)
        createViewMatrix()

        Matrix.rotateM(modelMatrix, 0, -x, 0f, 1f, 0f)
        Matrix.rotateM(modelMatrix, 0, -y, 1f, 0f, 0f)

        Matrix.multiplyMM(mVPMatrix, 0, mViewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, mVPMatrix, 0)
    }

    private fun calculateZ(meshData: MeshData) {
        calculateMVPMatrix()
        while (!GLUtil.inFrustum(mVPMatrix, meshData) && eyeZ < 50) {
            eyeZ += .5f
            calculateMVPMatrix()
        }
        eyeZ += 1f
        Log.d(TAG, eyeZ.toString())
        isZCalculated = true
    }

    private fun createViewMatrix() {
        // точка положения камеры
        val eyeX = 0f
        val eyeY = 0f

        // точка направления камеры
        val centerX = 0f
        val centerY = 0f
        val centerZ = 1f

        // up-вектор
        val upX = 0f
        val upY = 1f
        val upZ = 0f

        Matrix.setLookAtM(
            mViewMatrix,
            0,
            eyeX, eyeY, eyeZ,
            centerX, centerY, centerZ,
            upX, upY, upZ
        )

    }

    companion object {
        private const val TAG = "MainRenderer"
        private const val Z_NEAR = .1f
        private const val Z_FAR = 100f
        private val backgroundColor = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
    }
}