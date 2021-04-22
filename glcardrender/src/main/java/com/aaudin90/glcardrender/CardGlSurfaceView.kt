package com.aaudin90.glcardrender

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.aaudin90.glcardrender.api.CardModelLoader
import com.aaudin90.glcardrender.internal.renderers.CardRenderer
import com.aaudin90.glcardrender.internal.renderers.MainRenderer

class CardGlSurfaceView(context: Context) : GLSurfaceView(context) {

    private var mPreviousX = 0f
    private var mPreviousY = 0f
    private var render = MainRenderer(context)


    init {
        // Create an OpenGL ES 3.0 context.
        setEGLContextClientVersion(3)
        setRenderer(render)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    fun setModelLoader(cardModelLoader: CardModelLoader) {
        render.cardRenderer = CardRenderer(cardModelLoader.getData3D())
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
        val x = e.x
        val y = e.y
        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx = x - mPreviousX
                //subtract, so the cube moves the same direction as your finger.
                //with plus it moves the opposite direction.
                render.let {
                    it.x = (it.x - dx * TOUCH_SCALE_FACTOR)
                    val dy = y - mPreviousY
                    it.y = (it.y - dy * TOUCH_SCALE_FACTOR)
                }
            }
        }
        mPreviousX = x
        mPreviousY = y
        return true
    }

    companion object {
        //private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
        private const val TOUCH_SCALE_FACTOR = 0.1f
    }
}