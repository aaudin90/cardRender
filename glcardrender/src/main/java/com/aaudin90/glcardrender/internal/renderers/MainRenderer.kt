package com.aaudin90.glcardrender.internal.renderers

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.aaudin90.glcardrender.internal.entity.MeshData
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

internal class MainRenderer(private val context: Context) : GLSurfaceView.Renderer {
    internal var objectRotateX = 0f
    internal var objectRotateY = 0f
    internal var moveLightX = 0f
    internal var moveLightY = 0f
    internal var moveLightZ = 0f
    internal var drawMicroSun: Boolean = false
    internal val lightPosition = floatArrayOf(-1f, 0f, 10f)

    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)

    private var width: Int = 0
    private var height: Int = 0

    private var eyeZ = 0f
    private var isZCalculated = false

    private val microSunRenderer = MicroSunRenderer()

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
        if (drawMicroSun) {
            microSunRenderer.init(context)
        }
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

        val lightPositionToDraw = changeLightPosition()
        drawCard(lightPositionToDraw)
        if (drawMicroSun) {
            drawMicroSun(lightPositionToDraw)
        }
    }

    private fun changeLightPosition(): FloatArray {
        val newLightPosition = FloatArray(3)
        newLightPosition[0] = lightPosition[0] + moveLightX
        newLightPosition[1] = lightPosition[1] + moveLightY
        newLightPosition[2] = lightPosition[2] + moveLightZ
        return newLightPosition
    }

    private fun drawCard(lightPositionToDraw: FloatArray) {
        cardRenderer?.apply {
            calculateMatrix()

            if (!isZCalculated) {
                calculateZ(renderData.meshData)
            }

            if (!isInitialized) {
                init(context)
            }

            draw(modelMatrix, viewMatrix, projectionMatrix, lightPositionToDraw)
        }
    }

    private fun drawMicroSun(lightPositionToDraw: FloatArray) {
        microSunRenderer.apply {
            val sunModelMatrix = FloatArray(16)
            Matrix.setIdentityM(sunModelMatrix, 0)

            Matrix.translateM(
                sunModelMatrix,
                0,
                sunModelMatrix,
                0,
                lightPositionToDraw[0],
                lightPositionToDraw[1],
                lightPositionToDraw[2]
            )

            Matrix.scaleM(sunModelMatrix, 0, .2f, .2f, .2f)

            if (!isInitialized) {
                init(context)
            }

            draw(sunModelMatrix, viewMatrix, projectionMatrix)
        }
    }

    private fun calculateMatrix() {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.perspectiveM(projectionMatrix, 0, 18f, width.toFloat() / height, Z_NEAR, Z_FAR)

        Matrix.setLookAtM(
            viewMatrix,
            0,
            eyeX, eyeY, eyeZ,
            centerX, centerY, centerZ,
            upX, upY, upZ
        )

        Matrix.rotateM(modelMatrix, 0, objectRotateX, 0f, 1f, 0f)
        Matrix.rotateM(modelMatrix, 0, objectRotateY, 1f, 0f, 0f)
    }

    private fun calculateZ(meshData: MeshData) {
        calculateMatrix()
        while (!GLUtil.inFrustum(
                modelMatrix,
                viewMatrix,
                projectionMatrix,
                meshData
            ) && eyeZ < 50
        ) {
            eyeZ += .3f
            calculateMatrix()
        }
        eyeZ += eyeZ * .05f

        isZCalculated = true
    }

    private companion object {
        const val TAG = "MainRenderer"
        const val Z_NEAR = .1f
        const val Z_FAR = 100f

        // точка положения камеры
        const val eyeX = 0f
        const val eyeY = 0f

        // точка направления камеры
        const val centerX = 0f
        const val centerY = 0f
        const val centerZ = 1f

        // up-вектор
        const val upX = 0f
        const val upY = 1f
        const val upZ = 0f

        val backgroundColor = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
    }
}