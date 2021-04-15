package com.aaudin90.glcardrender.internal.data.source

import android.app.Application
import android.util.Log
import com.aaudin90.glcardrender.internal.domain.Math3DUtils
import com.aaudin90.glcardrender.internal.entity.Material
import java.io.BufferedReader
import java.io.InputStreamReader

internal class WavefrontMaterialDataSource(
    private val application: Application
) {

    fun get(): Material {
        BufferedReader(
            InputStreamReader(
                application.assets.open("render_card.mtl")
            )
        ).use { reader ->
            var line = ""

            var name = ""
            var textureFile = ""
            var ambient = FloatArray(0)
            var diffuse = FloatArray(0)
            var specular = FloatArray(0)
            var shininess = 0f
            var alpha = 1.0f

            try {
                while (reader.readLine()?.also { line = it } != null) {

                    // read next line
                    line = line.trim()

                    // ignore empty lines
                    if (line.isEmpty()) continue

                    // parse line
                    when {
                        line.startsWith("newmtl ") -> {
                            name = line.substring(6).trim()
                        }

                        line.startsWith("map_Kd ") -> { // texture filename

                            // bind texture
                            textureFile = line.substring(6).trim()
                        }
                        line.startsWith("Ka ") -> {

                            // ambient colour
                            ambient = Math3DUtils.parseFloat(
                                line.substring(2).trim().split(" ".toRegex()).toTypedArray()
                            )
                        }
                        line.startsWith("Kd ") -> {

                            // diffuse colour
                            diffuse = Math3DUtils.parseFloat(
                                line.substring(2).trim().split(" ".toRegex()).toTypedArray()
                            )
                        }
                        line.startsWith("Ks ") -> {
                            // specular colour
                            specular = Math3DUtils.parseFloat(
                                line.substring(2).trim().split(" ".toRegex()).toTypedArray()
                            )
                        }
                        line.startsWith("Ns ") -> {

                            // shininess
                            shininess = line.substring(3).toFloat()
                        }
                        line[0] == 'd' -> {

                            // alpha
                            alpha = line.substring(2).toFloat()
                        }
                        line.startsWith("Tr ") -> {

                            // Transparency (inverted)
                            alpha = 1 - line.substring(3).toFloat()

                        }
                        line.startsWith("illum ") -> {

                            // illumination model
                            Log.v("WavefrontMaterialsParse", "Ignored line: $line")
                        }
                        line[0] == '#' -> { // comment line

                            // log comment
                            Log.v("WavefrontMaterialsParse", line)
                        }
                        else -> {

                            // log event
                            Log.v("WavefrontMaterialsParse", "Ignoring line: $line")
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("WavefrontMaterialsParse", e.message, e)
            }

            return Material(
                name,
                ambient,
                diffuse,
                specular,
                shininess,
                alpha,
                textureFile
            )
        }
    }
}
