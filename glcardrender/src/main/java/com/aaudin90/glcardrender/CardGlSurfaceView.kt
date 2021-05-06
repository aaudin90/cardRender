package com.aaudin90.glcardrender

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.aaudin90.glcardrender.api.CardModelLoader
import com.aaudin90.glcardrender.internal.renderers.CardRenderer
import com.aaudin90.glcardrender.internal.renderers.MainRenderer
import kotlin.math.min
import kotlin.math.roundToInt

class CardGlSurfaceView(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    private var render = MainRenderer(context)

    init {
        setEGLContextClientVersion(3)
        setRenderer(render)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var width = context.resources.displayMetrics.widthPixels
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = min(width, widthSize)
        }

        var height = (width / CARD_PROPORTION).roundToInt()
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = min(height, heightSize)
        }

        setMeasuredDimension(width, height)
    }

    fun setData3DProvider(provider: CardModelLoader.Data3DProvider) {
        releaseRenderer()
        queueEvent {
            render.cardRenderer = CardRenderer(provider.getData3D())
        }
    }

    fun removeRenderer() {
        releaseRenderer()
    }

    private fun releaseRenderer() {
        queueEvent {
            val oldRender = render.cardRenderer
            if (oldRender != null) {
                oldRender.release()
                render.cardRenderer = null
            }
        }
    }

    fun setLightPosition(x: Float, y: Float, z: Float) {
        queueEvent {
            render.lightPosition[0] = x
            render.lightPosition[1] = y
            render.lightPosition[2] = z
        }
    }

    fun moveLight(x: Float, y: Float, z: Float) {
        queueEvent {
            render.moveLightX = x
            render.moveLightY = y
            render.moveLightZ = z
        }
    }

    fun rotateObject(x: Float, y: Float) {
        queueEvent {
            render.objectRotateX = x
            render.objectRotateY = y
        }
    }

    fun setDrawMicroSun(flag: Boolean) {
        queueEvent {
            render.drawMicroSun = flag
        }
    }

    private companion object {
        const val CARD_PROPORTION = 1.57f
    }
}