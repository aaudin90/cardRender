package com.aaudin90.glcardrender.internal.data.source

import android.app.Application
import android.util.Log
import com.aaudin90.glcardrender.internal.entity.Element
import com.aaudin90.glcardrender.internal.entity.MeshData
import com.aaudin90.glcardrender.internal.entity.Vertex
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

internal class WavefrontMeshDataSource(
    private val application: Application
) {

    fun get(): MeshData {
        BufferedReader(
            InputStreamReader(
                application.assets.open("render_card.obj")
            )
        ).use { reader ->

            // debug model purposes
            var lineNum = 0
            var line = ""

            // primitive data
            val vertexList = mutableListOf<FloatArray>()
            val normalsList = mutableListOf<FloatArray>()
            val textureList = mutableListOf<FloatArray>()

            // mesh data
            val verticesAttributes = mutableListOf<Vertex>()

            // material file
            var mtllib = ""
            var materialId = ""

            val smoothingGroups = mutableMapOf<String, List<Vertex>>()
            var currentSmoothingList = mutableListOf<Vertex>()

            val indicesCurrent = mutableListOf<Int>()

            try {
                while (reader.readLine()?.also { line = it } != null) {
                    lineNum++
                    line = line.trim()
                    if (line.isEmpty()) {
                        continue
                    }

                    when {
                        line.startsWith("v ") -> {
                            vertexList.add(parseVector(line.substring(2).trim()))
                        }

                        line.startsWith("vn") -> {
                            normalsList.add(parseVector(line.substring(3).trim()))
                        }

                        line.startsWith("vt") -> {
                            textureList.add(parseVariableVector(line.substring(3).trim()))
                        }

                        line.startsWith("f ") -> {
                            parseFace(
                                verticesAttributes,
                                indicesCurrent,
                                vertexList,
                                normalsList,
                                textureList,
                                line.substring(2),
                                currentSmoothingList
                            )
                        }

                        line.startsWith("s ") -> {
                            val smoothingGroupId = line.substring(1).trim()
                            if ("0" == smoothingGroupId || "off" == smoothingGroupId) {
                                currentSmoothingList.clear()
                            } else {
                                currentSmoothingList = ArrayList()
                                smoothingGroups[smoothingGroupId] = currentSmoothingList
                            }
                        }

                        line.startsWith("mtllib ") -> {
                            mtllib = line.substring(7)
                        }

                        line.startsWith("usemtl ") -> {
                            materialId = line.substring(7)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("WavefrontLoader", "Error reading line: $lineNum:$line", e);
                Log.e("WavefrontLoader", e.message, e);
                throw RuntimeException(e)
            }

            val element = Element("default", indicesCurrent, materialId)

            return MeshData(
                vertexList,
                normalsList,
                textureList,
                verticesAttributes,
                mtllib,
                element,
                smoothingGroups
            )
        }
    }

    private fun parseVector(line: String): FloatArray =
        try {
            val tokens = line.split(" +".toRegex()).toTypedArray()
            val vector = FloatArray(3)
            vector[0] = tokens[0].toFloat()
            vector[1] = tokens[1].toFloat()
            vector[2] = tokens[2].toFloat()
            vector
        } catch (ex: java.lang.Exception) {
            Log.e("WavefrontLoader", "Error parsing vector '" + line + "': " + ex.message)
            FloatArray(3)
        }

    /**
     * List of texture coordinates, in (u, [,v ,w]) coordinates, these will vary between 0 and 1. v, w are optional and default to 0.
     * There may only be 1 tex coords  on the line, which is determined by looking at the first tex coord line.
     */
    private fun parseVariableVector(line: String): FloatArray =
        try {
            val tokens = line.split(" +".toRegex()).toTypedArray()
            val vector = FloatArray(2)
            vector[0] = tokens[0].toFloat()
            if (tokens.size > 1) {
                vector[1] = tokens[1].toFloat()
                // ignore 3d coordinate
                /*if (tokens.length > 2) {
					vector[2] = Float.parseFloat(tokens[2]);
				}*/
            }
            vector
        } catch (ex: java.lang.Exception) {
            Log.e("WavefrontLoader", ex.message.toString())
            FloatArray(2)
        }

    /**
     * get this face's indicies from line "f v/vt/vn ..." with vt or vn index values perhaps being absent.
     */
    private fun parseFace(
        vertexAttributes: MutableList<Vertex>,
        indices: MutableList<Int>,
        vertexList: List<FloatArray>,
        normalsList: List<FloatArray>,
        texturesList: List<FloatArray>,
        line: String,
        currentSmoothingList: MutableList<Vertex>
    ) {
        try {

            // cpu optimization
            val tokens = if (line.contains("  ")) {
                line.split(" +".toRegex()).toTypedArray()
            } else {
                line.split(" ".toRegex()).toTypedArray()
            }

            // number of v/vt/vn tokens
            val numTokens = tokens.size
            var i = 0
            var faceIndex = 0
            while (i < numTokens) {
                // convert to triangles all polygons
                if (faceIndex > 2) {
                    // Converting polygon to triangle
                    faceIndex = 0
                    i -= 2
                }

                // triangulate polygon
                val faceToken: String =
                    // In FAN mode all meshObject shares the initial vertex
                    if (faceIndex == 0) {
                        tokens[0] // get a v/vt/vn
                    } else {
                        tokens[i] // get a v/vt/vn
                    }

                // parse index tokens
                // how many '/'s are there in the token
                val faceTokens = faceToken.split("/".toRegex()).toTypedArray()
                val numSeps = faceTokens.size
                var vertIdx = faceTokens[0].toInt()
                // A valid vertex index matches the corresponding vertex elements of a previously defined vertex list.
                // If an index is positive then it refers to the offset in that vertex list, starting at 1.
                // If an index is negative then it relatively refers to the end of the vertex list,
                // -1 referring to the last element.
                if (vertIdx < 0) {
                    vertIdx += vertexList.size
                } else {
                    vertIdx--
                }
                var textureIdx = -1
                if (numSeps > 1 && faceTokens[1].isNotEmpty()) {
                    textureIdx = faceTokens[1].toInt()
                    if (textureIdx < 0) {
                        textureIdx += texturesList.size
                    } else {
                        textureIdx--
                    }
                }
                var normalIdx = -1
                if (numSeps > 2 && faceTokens[2].isNotEmpty()) {
                    normalIdx = faceTokens[2].toInt()
                    if (normalIdx < 0) {
                        normalIdx += normalsList.size
                    } else {
                        normalIdx--
                    }
                }

                // create VertexAttribute
                val vertexAttribute = Vertex(
                    vertIdx,
                    normalIndex = normalIdx,
                    textureIndex = textureIdx
                )

                // add VertexAtribute
                val idx = vertexAttributes.size
                vertexAttributes.add(idx, vertexAttribute)

                // store the indices for this face
                indices.add(idx)

                // smoothing
                currentSmoothingList.add(vertexAttribute)
                i++
                faceIndex++
            }
        } catch (e: NumberFormatException) {
            Log.e("WavefrontLoader", e.message, e)
        }
    }
}