package com.example.glcardrender

import android.app.ActivityManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.aaudin90.glcardrender.CardGlSurfaceView
import com.aaudin90.glcardrender.api.CardModelLoader

class MainActivity : AppCompatActivity() {

    private lateinit var surfaceView1: CardGlSurfaceView
    private lateinit var surfaceView2: CardGlSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (detectOpenGLES30()) {
            val loader1 = CardModelLoader(application)
            val loader2 = CardModelLoader(application)
            setContentView(R.layout.activity_main)
            surfaceView1 = findViewById(R.id.sv1)
            //surfaceView2 = findViewById(R.id.sv2)
            surfaceView1.setModelLoader(loader1)
            //.setModelLoader(loader2)
        } else {
            Log.e("openglcube", "OpenGL ES 3.0 not supported on device.  Exiting...")
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        surfaceView1.onResume()
        //surfaceView2.onResume()
    }

    override fun onStop() {
        super.onStop()
        surfaceView1.onPause()
        //surfaceView2.onPause()
    }

    private fun detectOpenGLES30(): Boolean {
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val info = am.deviceConfigurationInfo
        return info.reqGlEsVersion >= 0x30000
    }
}