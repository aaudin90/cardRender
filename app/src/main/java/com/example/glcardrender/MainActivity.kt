package com.example.glcardrender

import android.app.ActivityManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.aaudin90.glcardrender.CardGlSurfaceView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (detectOpenGLES30()) {
            //so we know it a opengl 3.0 and use our extended GLsurfaceview.
            setContentView(CardGlSurfaceView(this))
        } else {
            // This is where you could create an OpenGL ES 2.0 and/or 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            Log.e("openglcube", "OpenGL ES 3.0 not supported on device.  Exiting...")
            finish()
        }
    }

    private fun detectOpenGLES30(): Boolean {
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val info = am.deviceConfigurationInfo
        return info.reqGlEsVersion >= 0x30000
    }
}