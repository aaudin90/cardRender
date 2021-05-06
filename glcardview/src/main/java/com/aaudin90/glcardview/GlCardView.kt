package com.aaudin90.glcardview

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import com.aaudin90.glcardrender.CardGlSurfaceView
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

class GlCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val cardSurfaceView: CardGlSurfaceView

    private var movePreviousX = 0f
    private var movePreviousY = 0f

    private var objectRotateX = 0f
    private var objectRotateY = 0f

    private var lightOffsetX = 0f
    private var lightOffsetY = 0f
    private var lightAnimatorSet: AnimatorSet? = null

    private var lastCloserXState = 0f
    private var lastCloserYState = 0f

    private var closerAnimatorSet: AnimatorSet? = null
    private var closerAnimationInProgress = false

    private val sensorManager: SensorManager =
        context.getSystemService(SENSOR_SERVICE) as SensorManager
    private val sensorLinAccel: Sensor = sensorManager
        .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

    private var timer: Timer? = null

    private var maxSensorValues = FloatArray(3)
    private val listener = object : SensorEventListener {

        override fun onSensorChanged(event: SensorEvent) {
            when (event.sensor.type) {
                Sensor.TYPE_LINEAR_ACCELERATION -> {
                    if (abs(event.values[0]) > abs(maxSensorValues[0])) {
                        maxSensorValues[0] = event.values[0]
                    }
                    if (abs(event.values[1]) > abs(maxSensorValues[1])) {
                        maxSensorValues[1] = event.values[1]
                    }
                    if (abs(event.values[2]) > abs(maxSensorValues[2])) {
                        maxSensorValues[2] = event.values[2]
                    }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }

    init {
        inflate(context, R.layout.layout_gl_card_view, this)
        cardSurfaceView = findViewById(R.id.surface_card)
    }

    //Вызывать при ЖЦ старт + bind вью холдера
    fun onStart() {
        cardSurfaceView.onResume()

        sensorManager.registerListener(
            listener,
            sensorLinAccel,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        val task = timerTask {
            post {
                startLightAnimator()
            }
        }
        timer = Timer()
        timer?.schedule(task, 0, 500)
    }

    //Вызывать при ЖЦ стоп + unbind вью холдера
    fun onStop() {
        closerAnimatorSet?.end()
        lightAnimatorSet?.end()
        sensorManager.unregisterListener(listener)
        timer?.cancel()

        cardSurfaceView.onPause()
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        if (!closerAnimationInProgress) {
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    val dx = (x - movePreviousX) * -1
                    val dy = (y - movePreviousY) * -1
                    objectRotateX -= dx * TOUCH_SCALE_FACTOR
                    objectRotateY -= dy * TOUCH_SCALE_FACTOR
                    rotateObject()
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    val closerX = calculateCloser(objectRotateX, lastCloserXState)
                    val closerY = calculateCloser(objectRotateY, lastCloserYState)
                    animateRotation(objectRotateX, closerX, objectRotateY, closerY)
                }
            }
        }
        movePreviousX = x
        movePreviousY = y
        return true
    }

    private fun trimRotation() {
        if (abs(objectRotateX) >= 360) {
            objectRotateX = abs(objectRotateX) - 360 * objectRotateX.sign
        }
        if (abs(objectRotateY) >= 360) {
            objectRotateY = abs(objectRotateY) - 360 * objectRotateY.sign
        }
    }

    private fun calculateCloser(from: Float, lastClosedState: Float): Float {
        val unsignedFrom = abs(from)
        val unsignedLastClosedState = abs(lastClosedState)
        return when {
            abs(unsignedFrom - unsignedLastClosedState) < 40 -> {
                lastClosedState
            }
            else -> {
                getNextAngle(lastClosedState, (from - lastClosedState).sign)
            }
        }
    }

    private fun getNextAngle(lastClosedPosition: Float, sign: Float): Float =
        when {
            lastClosedPosition.roundToInt() == 0 -> {
                if (sign > 0) {
                    180f
                } else {
                    -180f
                }
            }
            lastClosedPosition.roundToInt() == 180 -> {
                if (sign > 0) {
                    360f
                } else {
                    0f
                }
            }
            lastClosedPosition.roundToInt() == -180 -> {
                if (sign > 0) {
                    0f
                } else {
                    -360f
                }
            }
            else -> {
                0f
            }
        }

    private fun rotateObject() {
        trimRotation()
        cardSurfaceView.rotateObject(objectRotateX, objectRotateY)
    }

    private fun animateRotation(fromX: Float, toX: Float, fromY: Float, toY: Float) {
        val xAnimator = ValueAnimator.ofFloat(fromX, toX).apply {
            addUpdateListener {
                objectRotateX = it.animatedValue as Float
                rotateObject()
            }
        }
        val yAnimator = ValueAnimator.ofFloat(fromY, toY).apply {
            addUpdateListener {
                objectRotateY = it.animatedValue as Float
                rotateObject()
            }
        }
        closerAnimatorSet = AnimatorSet().apply {
            playTogether(xAnimator, yAnimator)
            duration = 300
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                    closerAnimationInProgress = true
                }

                override fun onAnimationEnd(animation: Animator?) {
                    closerAnimatorSet = null
                    closerAnimationInProgress = false
                    lastCloserXState = objectRotateX
                    lastCloserYState = objectRotateY
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }
            })
            start()
        }
    }

    private fun startLightAnimator() {
        val sensorValues = maxSensorValues
        val xAnimator = ValueAnimator.ofFloat(lightOffsetX, sensorValues[0]).apply {
            addUpdateListener { animator ->
                lightOffsetX = animator.animatedValue as Float
                cardSurfaceView.moveLight(
                    lightOffsetX * LIGHT_OFFSET_MULTIPLAYER,
                    lightOffsetY * LIGHT_OFFSET_MULTIPLAYER,
                    0f
                )
            }
        }
        val yAnimator = ValueAnimator.ofFloat(lightOffsetY, sensorValues[1]).apply {
            addUpdateListener { animator ->
                lightOffsetY = animator.animatedValue as Float
                cardSurfaceView.moveLight(
                    lightOffsetX * LIGHT_OFFSET_MULTIPLAYER,
                    lightOffsetY * LIGHT_OFFSET_MULTIPLAYER,
                    0f
                )
            }
        }
        lightAnimatorSet = AnimatorSet().apply {
            playTogether(xAnimator, yAnimator)
            interpolator = AccelerateDecelerateInterpolator()
            duration = 497
            start()
        }
        maxSensorValues = FloatArray(3)
    }

    private companion object {
        const val TOUCH_SCALE_FACTOR = 0.17f
        const val LIGHT_OFFSET_MULTIPLAYER = 2
    }
}