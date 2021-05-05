package com.example.glcardrender

import android.app.ActivityManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.aaudin90.glcardrender.api.CardModelLoader
import com.aaudin90.glcardview.GlCardView

class MainActivity : AppCompatActivity() {

    private lateinit var glCardView1: GlCardView
    private lateinit var glCardView2: GlCardView
    private lateinit var glCardView3: GlCardView
    private val loader by lazy {
        CardModelLoader(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (detectOpenGLES30()) {
            setContentView(R.layout.activity_main)
            glCardView1 = findViewById(R.id.gl_cv1)
            glCardView2 = findViewById(R.id.gl_cv2)
            glCardView3 = findViewById(R.id.gl_cv3)

            //glCardView1.cardSurfaceView.setDrawMicroSun(true)
            glCardView1.cardSurfaceView.setLightPosition(-1f, 0f, 10f)
            glCardView1.cardSurfaceView.setData3DProvider(
                loader.Data3DProvider(getBitmap("ural.jpg"))
            )
            glCardView2.cardSurfaceView.setLightPosition(-1f, 0f, 10f)
            glCardView2.cardSurfaceView.setData3DProvider(
                loader
                    .Data3DProvider(
                        getBitmap("podruzhka.png"),
                        getBitmap("podruzhka_gloss.png"),
                    )
            )
            glCardView3.cardSurfaceView.setData3DProvider(
                loader
                    .Data3DProvider(
                        getBitmap("ural.jpg"),
                        getBitmap("rivegauch_gloss.png")
                    )
            )
        } else {
            Log.e("openglcube", "OpenGL ES 3.0 not supported on device.  Exiting...")
            finish()
        }
    }

    private fun getBitmap(assetName: String): Bitmap =
        application.assets.open(assetName).use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }

    override fun onStart() {
        super.onStart()
        glCardView1.onStart()
        glCardView2.onStart()
        glCardView3.onStart()
    }

    override fun onStop() {
        super.onStop()
        glCardView1.onStop()
        glCardView2.onStop()
        glCardView3.onStop()
    }

    private fun detectOpenGLES30(): Boolean {
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val info = am.deviceConfigurationInfo
        return info.reqGlEsVersion >= 0x30000
    }
}