package com.example.glcardrender

import android.app.ActivityManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.aaudin90.glcardrender.CardGlSurfaceView
import com.aaudin90.glcardrender.api.CardModelLoader

class MainActivity : AppCompatActivity() {

    private lateinit var surfaceView: CardGlSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (detectOpenGLES30()) {
            val loader = CardModelLoader(application)
            surfaceView = CardGlSurfaceView(this)
            setContentView(surfaceView)
            surfaceView.setModelLoader(loader)
        } else {
            Log.e("openglcube", "OpenGL ES 3.0 not supported on device.  Exiting...")
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        surfaceView.onResume()
    }

    override fun onStop() {
        super.onStop()
        surfaceView.onPause()
    }

    private fun detectOpenGLES30(): Boolean {
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val info = am.deviceConfigurationInfo
        return info.reqGlEsVersion >= 0x30000
    }
}