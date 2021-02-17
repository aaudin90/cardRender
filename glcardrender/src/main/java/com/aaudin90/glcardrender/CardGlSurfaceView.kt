package com.aaudin90.glcardrender

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent

/*
* simple extention of the GLsurfaceview.  basically setup to use opengl 3.0
* and set some configs.  This would be where the touch listener is setup to do something.
*
* It also declares and sets the render.
*/
class CardGlSurfaceView(context: Context?) : GLSurfaceView(context) {

    private val render = CardRenderer(context)

    private var mPreviousX = 0f
    private var mPreviousY = 0f

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
                render.x = (render.x - dx * TOUCH_SCALE_FACTOR)
                val dy = y - mPreviousY
                render.y = (render.y - dy * TOUCH_SCALE_FACTOR)
            }
        }
        mPreviousX = x
        mPreviousY = y
        return true
    }

    companion object {
        //private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
        private const val TOUCH_SCALE_FACTOR = 0.005f
    }

    init {
        // Create an OpenGL ES 3.0 context.
        setEGLContextClientVersion(3)

        setRenderer(render)

        // Render the view only when there is a change in the drawing data
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}