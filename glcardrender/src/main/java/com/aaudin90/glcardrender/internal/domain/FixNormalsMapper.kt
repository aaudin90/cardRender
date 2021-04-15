package com.aaudin90.glcardrender.internal.domain

import android.util.Log
import com.aaudin90.glcardrender.internal.entity.MeshData
import com.aaudin90.glcardrender.internal.entity.MeshData.Companion.WRONG_NORMAL
import com.aaudin90.glcardrender.internal.entity.Vertex

internal class FixNormalsMapper {

    fun map(meshData: MeshData): MeshData {
        // otherwise replaced with this normals
        val newNormals = mutableListOf<FloatArray>()
        val newVerticesAttributes = mutableListOf<Vertex>()

        var counter = 0
        var i = 0
        while (i < meshData.element.indices.size) {
            val idx1: Int = meshData.element.indices[i]
            val idx2: Int = meshData.element.indices[i + 1]
            val idx3: Int = meshData.element.indices[i + 2]
            var vertexAttribute1: Vertex = meshData.verticesAttributes[idx1]
            var vertexAttribute2: Vertex = meshData.verticesAttributes[idx2]
            var vertexAttribute3: Vertex = meshData.verticesAttributes[idx3]
            val idxV1: Int = vertexAttribute1.vertexIndex
            val idxV2: Int = vertexAttribute2.vertexIndex
            val idxV3: Int = vertexAttribute3.vertexIndex
            val v1: FloatArray = meshData.vertices[idxV1]
            val v2: FloatArray = meshData.vertices[idxV2]
            val v3: FloatArray = meshData.vertices[idxV3]

            // check valid triangle
            if (v1.contentEquals(v2) || v2.contentEquals(v3) || v1.contentEquals(v3)) {

                // update normal attribute
                vertexAttribute1 = vertexAttribute1.copy(normalIndex = newNormals.size)
                vertexAttribute2 = vertexAttribute2.copy(normalIndex = newNormals.size)
                vertexAttribute3 = vertexAttribute3.copy(normalIndex = newNormals.size)

                // repeated vertex - no normal
                newNormals.add(newNormals.size, WRONG_NORMAL)
                counter++
                i += 3
                continue
            }
            val normalIdxV1: Int = vertexAttribute1.normalIndex
            val normalIdxV2: Int = vertexAttribute2.normalIndex
            val normalIdxV3: Int = vertexAttribute3.normalIndex
            val normalV1: FloatArray = meshData.normals[normalIdxV1]
            val normalV2: FloatArray = meshData.normals[normalIdxV2]
            val normalV3: FloatArray = meshData.normals[normalIdxV3]

            // calculate normal
            val calculatedNormal: FloatArray = calculateNormalFailsafe(v1, v2, v3)

            // check normal attribute 1
            if (normalIdxV1 == -1 || Math3DUtils.length(normalV1) < 0.1f) {

                // update normal attribute
                vertexAttribute1 = vertexAttribute1.copy(normalIndex = newNormals.size)

                // add normal
                newNormals.add(calculatedNormal)
                counter++
            } else {

                // update normal attribute
                vertexAttribute1 = vertexAttribute1.copy(normalIndex = newNormals.size)

                // preserve current normal
                newNormals.add(normalV1)
            }

            // check normal attribute 2
            if (normalIdxV2 == -1 || Math3DUtils.length(normalV2) < 0.1f) {

                // update normal attribute
                vertexAttribute2 = vertexAttribute2.copy(normalIndex = newNormals.size)

                // add normal
                newNormals.add(calculatedNormal)
                counter++
            } else {

                // update normal attribute
                vertexAttribute2 = vertexAttribute2.copy(normalIndex = newNormals.size)

                // preserve current normal
                newNormals.add(normalV2)
            }

            // check normal attribute 3
            if (normalIdxV3 == -1 || Math3DUtils.length(normalV3) < 0.1f) {

                // update normal attribute
                vertexAttribute3 = vertexAttribute3.copy(normalIndex = newNormals.size)

                // add normal
                newNormals.add(calculatedNormal)
                counter++
            } else {

                // update normal attribute
                vertexAttribute3 = vertexAttribute3.copy(normalIndex = newNormals.size)

                // preserve current normal
                newNormals.add(normalV3)
            }

            newVerticesAttributes.apply {
                add(vertexAttribute1)
                add(vertexAttribute2)
                add(vertexAttribute3)
            }

            i += 3
        }

        Log.i("MeshData", "Fixed normals. Total: $counter")

        return meshData.copy(
            verticesAttributes = newVerticesAttributes,
            normals = newNormals
        )
    }

    private fun calculateNormalFailsafe(
        v1: FloatArray,
        v2: FloatArray,
        v3: FloatArray
    ): FloatArray {
        var normal: FloatArray = Math3DUtils.calculateNormal(v1, v2, v3)
        try {
            Math3DUtils.normalize(normal)
        } catch (e: Exception) {
            Log.e(
                "MeshData", "Error calculating normal. " + e.message
                        + "," + Math3DUtils.toString(v1) + "," + Math3DUtils.toString(v2) + "," + Math3DUtils.toString(
                    v3
                ), e
            )
            normal = WRONG_NORMAL
        }
        return normal
    }

}