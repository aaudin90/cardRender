package com.example.glcardrender

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LaunchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.launch_activity)
        findViewById<Button>(R.id.btn_gogo).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}