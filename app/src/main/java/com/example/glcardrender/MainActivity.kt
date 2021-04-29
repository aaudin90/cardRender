package com.example.glcardrender

import android.app.ActivityManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.aaudin90.glcardrender.CardGlSurfaceView
import com.aaudin90.glcardrender.api.CardModelLoader

class MainActivity : AppCompatActivity() {

    private lateinit var surfaceView1: CardGlSurfaceView
    private lateinit var surfaceView2: CardGlSurfaceView
    private lateinit var surfaceView3: CardGlSurfaceView
    private val loader by lazy {
        CardModelLoader(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (detectOpenGLES30()) {
            setContentView(R.layout.activity_main)
            surfaceView1 = findViewById(R.id.sv1)
            surfaceView2 = findViewById(R.id.sv2)
            surfaceView3 = findViewById(R.id.sv3)
            surfaceView1.setData3DProvider(
                loader.Data3DProvider(getBitmap("ural.jpg"))
            )
            surfaceView2.setData3DProvider(
                loader
                    .Data3DProvider(
                        getBitmap("podruzhka.png"),
                        getBitmap("podruzhka_gloss.png")
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
            val time = System.currentTimeMillis()
            surfaceView2.setData3DProvider(
                loader
                    .Data3DProvider(
                        podruzhkaBM.copy(podruzhkaBM.config, false),
                        podruzhkaGloss.copy(podruzhkaGloss.config, false)
                    )
            )
            Log.d("sssss", "${System.currentTimeMillis() - time}")
        },
            Runnable {
                val time = System.currentTimeMillis()
                surfaceView2.setData3DProvider(
                    loader
                        .Data3DProvider(
                            rivegauch.copy(rivegauch.config, false),
                            rivegauchGloss.copy(rivegauchGloss.config, false)
                        )
                )
                Log.d("sssss", "${System.currentTimeMillis() - time}")
            }
        )
        var i = 1
        while (i < 150) {
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
    }

    override fun onStop() {
        super.onStop()
        surfaceView1.onPause()
        surfaceView2.onPause()
        surfaceView3.onPause()
    }

    private fun detectOpenGLES30(): Boolean {
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val info = am.deviceConfigurationInfo
        return info.reqGlEsVersion >= 0x30000
    }
}