package com.example.glcardrender

import android.app.ActivityManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aaudin90.glcardrender.api.CardModelLoader

class MainActivity : AppCompatActivity() {

    private val loader by lazy {
        CardModelLoader(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (detectOpenGLES30()) {
            setContentView(R.layout.activity_main)
            val rv: RecyclerView = findViewById(R.id.rv)
            rv.layoutManager = LinearLayoutManager(this).apply {
                orientation = RecyclerView.VERTICAL
            }
            rv.postDelayed({
                rv.adapter = createAdapter()
            }, 200)
        } else {
            Log.e("openglcube", "OpenGL ES 3.0 not supported on device.  Exiting...")
            finish()
        }
    }

    private fun createAdapter(): RvAdapter =
        RvAdapter(
            listOf(
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.TextItem,
                RvAdapter.Item.RenderItem(
                    loader
                        .Data3DProvider(
                            getBitmap("podruzhka.png"),
                            getBitmap("podruzhka_gloss.png"),
                        )
                ),
                RvAdapter.Item.TextItem,
                RvAdapter.Item.RenderItem(
                    loader
                        .Data3DProvider(
                            getBitmap("ural.jpg"),
                            getBitmap("rivegauch_gloss.png")
                        )
                ),
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.TextItem,
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.TextItem,
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.TextItem,
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.TextItem,
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.TextItem,
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.TextItem,
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.TextItem,
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.TextItem,
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.TextItem,
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.TextItem,
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.TextItem,
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.TextItem,
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.TextItem,
                RvAdapter.Item.RenderItem(loader.Data3DProvider(getBitmap("ural.jpg"))),
                RvAdapter.Item.RenderItem(
                    loader.Data3DProvider(
                        getBitmap("ural.jpg"),
                        getBitmap("rivegauch_gloss.png")
                    )
                )
            )
        )

    private fun getBitmap(assetName: String): Bitmap =
        application.assets.open(assetName).use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }

    private fun detectOpenGLES30(): Boolean {
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val info = am.deviceConfigurationInfo
        return info.reqGlEsVersion >= 0x30000
    }
}