package com.example.glcardrender

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.app.ActivityManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.aaudin90.glcardrender.CardGlSurfaceView
import com.aaudin90.glcardrender.api.CardModelLoader
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var surfaceView1: CardGlSurfaceView
    private lateinit var surfaceView2: CardGlSurfaceView
    private lateinit var surfaceView3: CardGlSurfaceView
    private val loader by lazy {
        CardModelLoader(application)
    }

    private lateinit var sensorManager: SensorManager
    private lateinit var sensorLinAccel: Sensor

    @Volatile
    private var maxSensorValues = FloatArray(3)

    private lateinit var timer: Timer

    private var animatedX = 0f
    private var animatedY = 0f

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

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorLinAccel = sensorManager
            .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        if (detectOpenGLES30()) {
            setContentView(R.layout.activity_main)
            surfaceView1 = findViewById(R.id.sv1)
            surfaceView2 = findViewById(R.id.sv2)
            surfaceView3 = findViewById(R.id.sv3)

            //surfaceView1.setDrawMicroSun(true)
            surfaceView1.setLightPosition(-1f, 0f, 10f)
            surfaceView1.setData3DProvider(
                loader.Data3DProvider(getBitmap("ural.jpg"))
            )
            surfaceView2.setLightPosition(-1f, 0f, 10f)
            surfaceView2.setData3DProvider(
                loader
                    .Data3DProvider(
                        getBitmap("podruzhka.png"),
                        getBitmap("podruzhka_gloss.png"),
                    )
            )
            surfaceView3.setData3DProvider(
                loader
                    .Data3DProvider(
                        getBitmap("ural.jpg"),
                        getBitmap("rivegauch_gloss.png")
                    )
            )
            loop()
        } else {
            Log.e("openglcube", "OpenGL ES 3.0 not supported on device.  Exiting...")
            finish()
        }
    }

    private fun loop() {
        val podruzhkaBM = getBitmap("podruzhka.png")
        val podruzhkaGloss = getBitmap("podruzhka_gloss.png")
        val rivegauch = getBitmap("rivegauch.jpg")
        val rivegauchGloss = getBitmap("rivegauch_gloss.png")

        val listR = listOf(Runnable {
            surfaceView2.setData3DProvider(
                loader
                    .Data3DProvider(
                        podruzhkaBM.copy(podruzhkaBM.config, false),
                        podruzhkaGloss.copy(podruzhkaGloss.config, false)
                    )
            )
            //surfaceView2.setDrawMicroSun(true)
        },
            Runnable {
                surfaceView2.setData3DProvider(
                    loader
                        .Data3DProvider(
                            rivegauch.copy(rivegauch.config, false),
                            rivegauchGloss.copy(rivegauchGloss.config, false)
                        )
                )
                //surfaceView2.setDrawMicroSun(false)
            }
        )
        var i = 1
        while (i < 2) {
            surfaceView2.postDelayed(
                listR[i % 2], 5000 * i.toLong()
            )
            i++
        }
    }

    private fun getBitmap(assetName: String): Bitmap =
        application.assets.open(assetName).use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }

    override fun onStart() {
        super.onStart()
        surfaceView1.onResume()
        surfaceView2.onResume()
        surfaceView3.onResume()
        sensorManager.registerListener(
            listener,
            sensorLinAccel,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        val task = timerTask {
            runOnUiThread {
                val sensorValues = maxSensorValues
                val xAnimator = ValueAnimator.ofFloat(animatedX, sensorValues[0]).apply {
                    addUpdateListener { animator ->
                        animatedX = animator.animatedValue as Float
                        surfaceView1.moveLight(animatedX * 2, animatedY * 2, 0f)
                        surfaceView2.moveLight(animatedX * 2, animatedY * 2, 0f)
                    }
                }
                val yAnimator = ValueAnimator.ofFloat(animatedY, sensorValues[1]).apply {
                    addUpdateListener { animator ->
                        animatedY = animator.animatedValue as Float
                        surfaceView1.moveLight(animatedX * 2, animatedY * 2, 0f)
                        surfaceView2.moveLight(animatedX * 2, animatedY * 2, 0f)
                    }
                }
//                val zAnimator = ValueAnimator.ofFloat(animatedZ, sensorValues[2]).apply {
//                    addUpdateListener { animator ->
//                        animatedZ = animator.animatedValue as Float
//                        surfaceView1.moveLight(animatedX, animatedY, animatedZ)
//                    }
//                }
                AnimatorSet().apply {
                    playTogether(xAnimator, yAnimator)
                    interpolator = AccelerateDecelerateInterpolator()
                    duration = 497
                    start()
                }
                maxSensorValues = FloatArray(3)
            }
        }
        timer = Timer()
        timer.schedule(task, 0, 500)
    }

    override fun onStop() {
        super.onStop()
        surfaceView1.onPause()
        surfaceView2.onPause()
        surfaceView3.onPause()
        sensorManager.unregisterListener(listener)
        timer.cancel()
    }

    private fun detectOpenGLES30(): Boolean {
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val info = am.deviceConfigurationInfo
        return info.reqGlEsVersion >= 0x30000
    }
}