package com.aaudin90.glcardrender.internal.entity

import android.util.Log
import com.aaudin90.glcardrender.internal.domain.IOUtils
import java.nio.FloatBuffer

internal data class MeshData(
    val vertices: List<FloatArray>,
    val normals: List<FloatArray>,
    val textures: List<FloatArray>,
    val verticesAttributes: List<Vertex>,
    val materialFile: String,
    val element: Element,
    val smoothingGroups: Map<String, List<Vertex>>
) {
    val vertexBuffer: FloatBuffer = createVertexBuffer()
    val normalsBuffer: FloatBuffer = createNormalsBuffer()
    val textureBuffer: FloatBuffer = createTextureBuffer()

    private fun createVertexBuffer(): FloatBuffer {
        return if (verticesAttributes.isNotEmpty()) {
            val buffer = IOUtils.createFloatBuffer(verticesAttributes.size * 3)
            for (i in verticesAttributes.indices) buffer.put(vertices[verticesAttributes[i].vertexIndex])
            buffer
        } else {
            val buffer = IOUtils.createFloatBuffer(vertices.size * 3)
            for (i in vertices.indices) buffer.put(vertices[i])
            buffer
        }
    }

    private fun createNormalsBuffer(): FloatBuffer =
        if (normals.isNotEmpty()) {
            val buffer = IOUtils.createFloatBuffer(verticesAttributes.size * 3)
            for (i in verticesAttributes.indices) {
                var normal: FloatArray = WRONG_NORMAL // no normal in case of error
                val index = verticesAttributes[i].normalIndex
                if (index >= 0 && index < normals.size) {
                    normal = normals[index]
                } else {
                    Log.e("MeshData", "Wrong normal index: $index")
                }
                buffer.put(normal)
            }
            buffer
        } else {
            val buffer = IOUtils.createFloatBuffer(normals.size * 3)
            for (i in normals.indices) {
                buffer.put(normals[i])
            }
            buffer
        }

    private fun createTextureBuffer(): FloatBuffer {
        var buffer = FloatBuffer.allocate(0)
        if (textures.isNotEmpty()) {
            buffer = IOUtils.createFloatBuffer(verticesAttributes.size * 2)
            for (i in verticesAttributes.indices) {
                var texture = FloatArray(2) // no texture in case of error
                val index: Int = verticesAttributes[i].textureIndex
                if (index >= 0 && index < textures.size) {
                    texture = textures[index]
                    texture = floatArrayOf(texture[0], 1 - texture[1])
                }
                buffer.put(texture)
            }
        }
        return buffer
    }

    companion object {
        val WRONG_NORMAL = floatArrayOf(0f, -1f, 0f)
    }
}


//class MeshData(
//    val id: String?,
//    val name: String?,
//    private val vertices: List<FloatArray>?, // we can build them
//    private var normals: MutableList<FloatArray>?,
//    private val colors: List<FloatArray>?,
//    private val textures: List<FloatArray>?,
//    verticesAttributes: List<Vertex>?,
//    elements: List<Element>?,
//    materialFile: String?,
//    smoothingGroups: Map<String, List<Vertex>>?
//) {
//
//    private val verticesAttributes: List<Vertex>?
//    private val elements: List<Element>?
//    var vertexBuffer: FloatBuffer? = null
//        get() {
//            if (field == null) {
//                if (verticesAttributes != null) {
//                    field = IOUtils.createFloatBuffer(verticesAttributes.size * 3)
//                    for (i in verticesAttributes.indices) field!!.put(vertices!![verticesAttributes[i].vertexIndex])
//                } else {
//                    field = IOUtils.createFloatBuffer(vertices!!.size * 3)
//                    for (i in vertices.indices) field!!.put(vertices[i])
//                }
//            }
//            return field
//        }
//        private set
//    private var normalsBuffer: FloatBuffer? = null
//
//    // red to warn about error
//    var colorsBuffer: FloatBuffer? = null
//        get() {
//            if (field == null && !colors!!.isEmpty()) {
//                field = IOUtils.createFloatBuffer(verticesAttributes!!.size * 4)
//                var i = 0
//                while (i < verticesAttributes.size && i < colors.size) {
//                    var color = floatArrayOf(1f, 0f, 0f, 1f) // red to warn about error
//                    val index: Int = verticesAttributes[i].colorIndex
//                    if (index >= 0 && index < colors.size) {
//                        color = colors[index]
//                    }
//                    field!!.put(color)
//                    i++
//                }
//            }
//            return field
//        }
//        private set
//
//    // no texture in case of error
//    var textureBuffer: FloatBuffer? = null
//        get() {
//            if (field == null && !textures!!.isEmpty()) {
//                field = IOUtils.createFloatBuffer(verticesAttributes!!.size * 2)
//                for (i in verticesAttributes.indices) {
//                    var texture = FloatArray(2) // no texture in case of error
//                    val index: Int = verticesAttributes[i].textureIndex
//                    if (index >= 0 && index < textures.size) {
//                        texture = textures[index]
//                        texture = floatArrayOf(texture[0], 1 - texture[1])
//                    }
//                    field!!.put(texture)
//                }
//            }
//            return field
//        }
//        private set
//
//    // skinning data
//    var bindShapeMatrix: FloatArray
//    var jointsArray: IntArray?
//    var weightsArray: FloatArray?
//    var jointsBuffer: FloatBuffer? = null
//        get() {
//            if (field == null && jointsArray != null) {
//                field = IOUtils.createFloatBuffer(jointsArray!!.size)
//                for (i in jointsArray!!.indices) {
//                    field.put(jointsArray!![i])
//                }
//            }
//            return field
//        }
//        private set
//    var weightsBuffer: FloatBuffer? = null
//        get() {
//            if (field == null && weightsArray != null) {
//                field = IOUtils.createFloatBuffer(weightsArray!!.size)
//                field!!.put(weightsArray)
//            }
//            return field
//        }
//        private set
//
//    // external material file
//    val materialFile: String?
//
//    // smoothing
//    private var normalsOriginal: MutableList<FloatArray>? = null
//    private val smoothingGroups: Map<String, List<Vertex>>?
//    fun getNormals(): List<FloatArray>? {
//        return normals
//    }
//
//    fun getElements(): List<Element>? {
//        return elements
//    }
//
//    fun smooth() {
//
//        // save normals to allow rollback operation
//        normalsOriginal = ArrayList(normals!!.size)
//        for (i in normals!!.indices) {
//            normalsOriginal.add(normals!![i].clone())
//        }
//
//        // check we have normals to smooth
//        if (smoothingGroups == null || smoothingGroups.isEmpty()) {
//            smoothAuto()
//        } else {
//            smoothGroups()
//        }
//    }
//
//    fun unSmooth() {
//        normals = normalsOriginal
//    }
//
//    private fun smoothAuto() {
//        if (elements != null) {
//            smoothAutoForElements()
//        } else {
//            smoothAutoForArrays()
//        }
//    }
//
//    private fun smoothGroups() {
//        // log event
//        Log.i("MeshData", "Smoothing groups... Total: " + smoothingGroups!!.size)
//
//        // process all smoothing groups
//        for ((_, value) in smoothingGroups) {
//            Log.v("MeshData", "Smoothing group... Total vertices: " + value.size)
//
//
//            // accumulated normal
//            var smoothNormal = FloatArray(3)
//            run {
//                var i = 0
//                while (i < value.size) {
//                    val va1: Vertex = value[i]
//                    val va2: Vertex = value[i + 1]
//                    val va3: Vertex = value[i + 2]
//                    val v1 = this.vertices!![va1.vertexIndex]
//                    val v2 = this.vertices[va2.vertexIndex]
//                    val v3 = this.vertices[va3.vertexIndex]
//                    val normal = calculateNormalFailsafe(v1, v2, v3)
//                    smoothNormal = Math3DUtils.add(smoothNormal, normal)
//                    i += 3
//                }
//            }
//
//            // normalize smooth normals
//            Math3DUtils.normalize(smoothNormal)
//
//
//            // add new normal
//            val newSmoothNormalIdx = normals!!.size
//            normals!!.add(newSmoothNormalIdx, smoothNormal)
//
//            // update normal index to smoothed normal
//            for (i in value.indices) {
//
//                // next vertex attribute linked to the smoothing group
//                val va: Vertex = value[i]
//
//                // When vertex normals are present, they supersede smoothing groups.
//                if (va.normalIndex !== -1) {
//                    continue
//                }
//
//                // update with smoothed normal
//                va.normalIndex = newSmoothNormalIdx
//            }
//        }
//    }
//
//    /**
//     * Fix missing or wrong normals.  Only for triangulated polygons
//     */
//    fun fixNormals() {
//        Log.i("MeshData", "Fixing missing or wrong normals...")
//
//        // check there is normals to fix
//        if (normals == null || normals!!.isEmpty()) {
//
//            // write new normals
//            generateNormals()
//        } else {
//
//            // fix missing or wrong
//            if (elements != null) {
//                fixNormalsForElements()
//            } else {
//                fixNormalsForArrays()
//            }
//        }
//    }
//
//    /**
//     * Fix missing or wrong normals.  Only for triangulated polygons
//     */
//    private fun generateNormals() {
//        Log.i("MeshData", "Generating normals...")
//
//        // replaced normals
//        val newNormals: MutableList<FloatArray> = ArrayList()
//        var counter = 0
//        for (element in getElements()!!) {
//            var i = 0
//            while (i < element.indices.size()) {
//                val idx1: Int = element.indices.get(i)
//                val idx2: Int = element.indices.get(i + 1)
//                val idx3: Int = element.indices.get(i + 2)
//                val vertexAttribute1: Vertex = verticesAttributes!![idx1]
//                val vertexAttribute2: Vertex = verticesAttributes[idx2]
//                val vertexAttribute3: Vertex = verticesAttributes[idx3]
//                val idxV1: Int = vertexAttribute1.vertexIndex
//                val idxV2: Int = vertexAttribute2.vertexIndex
//                val idxV3: Int = vertexAttribute3.vertexIndex
//                val v1 = vertices!![idxV1]
//                val v2 = vertices[idxV2]
//                val v3 = vertices[idxV3]
//
//                // check valid triangle
//                if (Arrays.equals(v1, v2) || Arrays.equals(v2, v3) || Arrays.equals(v1, v3)) {
//
//                    // update normal attribute
//                    vertexAttribute1.normalIndex = newNormals.size
//                    vertexAttribute2.normalIndex = newNormals.size
//                    vertexAttribute3.normalIndex = newNormals.size
//
//                    // repeated vertex - no normal
//                    newNormals.add(newNormals.size, WRONG_NORMAL)
//                    counter++
//                    i += 3
//                    continue
//                }
//
//                // calculate normal
//                val calculatedNormal = calculateNormalFailsafe(v1, v2, v3)
//
//                // update normal attribute
//                vertexAttribute1.normalIndex = newNormals.size
//                vertexAttribute2.normalIndex = newNormals.size
//                vertexAttribute3.normalIndex = newNormals.size
//
//                // add normal
//                newNormals.add(newNormals.size, calculatedNormal)
//                i += 3
//            }
//        }
//        normals = newNormals
//        Log.i(
//            "MeshData",
//            "Generated normals. Total: " + normals!!.size + ", Faces/Lines: " + counter
//        )
//    }
//
//    private fun fixNormalsForArrays() {
//        Log.i("MeshData", "Fixing normals...")
//
//        // otherwise replaced with this normals
//        val newNormals: MutableList<FloatArray> = ArrayList()
//        var counter = 0
//        var i = 0
//        while (i < vertices!!.size) {
//            val v1 = vertices[i]
//            val v2 = vertices[i + 1]
//            val v3 = vertices[i + 2]
//
//            // check valid triangle
//            if (Arrays.equals(v1, v2) || Arrays.equals(v2, v3) || Arrays.equals(v1, v3)) {
//
//                // repeated vertex - no normal
//                newNormals.add(newNormals.size, WRONG_NORMAL)
//                newNormals.add(newNormals.size, WRONG_NORMAL)
//                newNormals.add(newNormals.size, WRONG_NORMAL)
//                counter++
//                i += 3
//                continue
//            }
//            val normalV1 = normals!![i]
//            val normalV2 = normals!![i + 1]
//            val normalV3 = normals!![i + 2]
//
//            // calculate normal
//            val calculatedNormal = calculateNormalFailsafe(v1, v2, v3)
//
//            // check normal attribute 1
//            if (Math3DUtils.length(normalV1) < 0.1f) {
//
//                // add normal
//                newNormals.add(calculatedNormal)
//                counter++
//            } else {
//
//                // preserve current normal
//                newNormals.add(normalV1)
//            }
//
//            // check normal attribute 2
//            if (Math3DUtils.length(normalV2) < 0.1f) {
//
//                // add normal
//                newNormals.add(calculatedNormal)
//                counter++
//            } else {
//
//                // preserve current normal
//                newNormals.add(normalV2)
//            }
//
//            // check normal attribute 3
//            if (Math3DUtils.length(normalV3) < 0.1f) {
//
//                // add normal
//                newNormals.add(calculatedNormal)
//                counter++
//            } else {
//
//                // preserve current normal
//                newNormals.add(normalV3)
//            }
//            i += 3
//        }
//        normals = newNormals
//        Log.i("MeshData", "Fixed normals. Total: $counter")
//    }
//
//    private fun fixNormalsForElements() {
//        Log.i("MeshData", "Fixing normals for all elements...")
//
//        // otherwise replaced with this normals
//        val newNormals: MutableList<FloatArray> = ArrayList()
//        var counter = 0
//        for (element in getElements()!!) {
//            var i = 0
//            while (i < element.indices.size()) {
//                val idx1: Int = element.indices.get(i)
//                val idx2: Int = element.indices.get(i + 1)
//                val idx3: Int = element.indices.get(i + 2)
//                val vertexAttribute1: Vertex = verticesAttributes!![idx1]
//                val vertexAttribute2: Vertex = verticesAttributes[idx2]
//                val vertexAttribute3: Vertex = verticesAttributes[idx3]
//                val idxV1: Int = vertexAttribute1.vertexIndex
//                val idxV2: Int = vertexAttribute2.vertexIndex
//                val idxV3: Int = vertexAttribute3.vertexIndex
//                val v1 = vertices!![idxV1]
//                val v2 = vertices[idxV2]
//                val v3 = vertices[idxV3]
//
//                // check valid triangle
//                if (Arrays.equals(v1, v2) || Arrays.equals(v2, v3) || Arrays.equals(v1, v3)) {
//
//                    // update normal attribute
//                    vertexAttribute1.normalIndex = newNormals.size
//                    vertexAttribute2.normalIndex = newNormals.size
//                    vertexAttribute3.normalIndex = newNormals.size
//
//                    // repeated vertex - no normal
//                    newNormals.add(newNormals.size, WRONG_NORMAL)
//                    counter++
//                    i += 3
//                    continue
//                }
//                val normalIdxV1: Int = vertexAttribute1.normalIndex
//                val normalIdxV2: Int = vertexAttribute2.normalIndex
//                val normalIdxV3: Int = vertexAttribute3.normalIndex
//                val normalV1 = normals!![normalIdxV1]
//                val normalV2 = normals!![normalIdxV2]
//                val normalV3 = normals!![normalIdxV3]
//
//                // calculate normal
//                val calculatedNormal = calculateNormalFailsafe(v1, v2, v3)
//
//                // check normal attribute 1
//                if (normalIdxV1 == -1 || Math3DUtils.length(normalV1) < 0.1f) {
//
//                    // update normal attribute
//                    vertexAttribute1.normalIndex = newNormals.size
//
//                    // add normal
//                    newNormals.add(calculatedNormal)
//                    counter++
//                } else {
//
//                    // update normal attribute
//                    vertexAttribute1.normalIndex = newNormals.size
//
//                    // preserve current normal
//                    newNormals.add(normalV1)
//                }
//
//                // check normal attribute 2
//                if (normalIdxV2 == -1 || Math3DUtils.length(normalV2) < 0.1f) {
//
//                    // update normal attribute
//                    vertexAttribute2.normalIndex = newNormals.size
//
//                    // add normal
//                    newNormals.add(calculatedNormal)
//                    counter++
//                } else {
//
//                    // update normal attribute
//                    vertexAttribute2.normalIndex = newNormals.size
//
//                    // preserve current normal
//                    newNormals.add(normalV2)
//                }
//
//                // check normal attribute 3
//                if (normalIdxV3 == -1 || Math3DUtils.length(normalV3) < 0.1f) {
//
//                    // update normal attribute
//                    vertexAttribute3.normalIndex = newNormals.size
//
//                    // add normal
//                    newNormals.add(calculatedNormal)
//                    counter++
//                } else {
//
//                    // update normal attribute
//                    vertexAttribute3.normalIndex = newNormals.size
//
//                    // preserve current normal
//                    newNormals.add(normalV3)
//                }
//                i += 3
//            }
//        }
//        normals = newNormals
//        Log.i("MeshData", "Fixed normals. Total: $counter")
//    }
//
//    private fun smoothAutoForArrays() {
//
//        // log event
//        Log.i("MeshData", "Auto smoothing normals for arrays...")
//
//        // smoothed normal
//        val smoothNormals: MutableMap<String, FloatArray> = HashMap()
//        for (i in vertices!!.indices) {
//            val idxKey = Arrays.toString(vertices[i])
//            val smoothNormal = smoothNormals[idxKey]
//            if (smoothNormal == null) {
//                smoothNormals[idxKey] = normals!![i]
//                continue
//            }
//
//            // if same normal, do nothing
//            val normal = normals!![i]
//            if (normal == smoothNormal || Arrays.equals(normal, smoothNormal)) {
//                normals!![i] = smoothNormal
//                continue
//            }
//
//            // smooth normal
//            val newSmoothNormal: FloatArray = Math3DUtils.mean(smoothNormal, normal)
//            Math3DUtils.normalize(newSmoothNormal)
//
//            // update smoothed normal
//            smoothNormal[0] = newSmoothNormal[0]
//            smoothNormal[1] = newSmoothNormal[1]
//            smoothNormal[2] = newSmoothNormal[2]
//
//            // replace with smoothed normal
//            normals!![i] = smoothNormal
//        }
//    }
//
//    private fun smoothAutoForElements() {
//
//        // log event
//        Log.i("MeshData", "Auto smoothing normals for all elements...")
//
//        // smoothed normal
//        val smoothNormals: MutableMap<Int, FloatArray> = HashMap()
//        val vertexToNormalMap: MutableMap<Int, Int> = HashMap()
//        for (element in getElements()!!) {
//            for (i in 0 until element.indices.size()) {
//
//                // next index
//                val idx: Int = element.indices.get(i)
//
//                // next vertex attributes
//                val vertexIndex: Int = verticesAttributes!![idx].vertexIndex
//                val normalIndex: Int = verticesAttributes[idx].normalIndex
//
//                // initialize smoothed normal
//                var smoothNormal = smoothNormals[vertexIndex]
//                if (smoothNormal == null) {
//                    try {
//                        val normal = normals!![normalIndex]
//                        smoothNormal = normal.clone()
//                        smoothNormals[vertexIndex] = smoothNormal
//                    } catch (e: Exception) {
//                        Log.e("MeshData", e.message!!)
//                    }
//                    vertexToNormalMap[vertexIndex] = normalIndex
//                    continue
//                }
//
//                // if same normal, do nothing
//                val normal = normals!![normalIndex]
//                if (normal == smoothNormal) {
//                    verticesAttributes[i].normalIndex = normalIndex
//                    continue
//                }
//
//                // if same normal values, point to already existing normal
//                if (Arrays.equals(normal, smoothNormal)) {
//                    verticesAttributes[i].normalIndex = normalIndex
//                    continue
//                }
//
//                // smooth normal
//                val newSmoothNormal: FloatArray = Math3DUtils.mean(smoothNormal, normal)
//                Math3DUtils.normalize(newSmoothNormal)
//
//                // update smoothed normal
//                smoothNormal[0] = newSmoothNormal[0]
//                smoothNormal[1] = newSmoothNormal[1]
//                smoothNormal[2] = newSmoothNormal[2]
//
//                // replace with smoothed normal
//                normals!![normalIndex] = smoothNormal
//                verticesAttributes[i].normalIndex = normalIndex
//            }
//        }
//    }
//
//    fun validate() {
//        if (normals == null) return
//        for (i in normals!!.indices) {
//            val normal = normals!![i]
//            require(!Float.isNaN(normal[0])) { "NaN" }
//            require(!Float.isNaN(normal[1])) { "NaN" }
//            require(!Float.isNaN(normal[2])) { "NaN" }
//            require(Math3DUtils.length(normal) >= 0.9f) { "Wrong normal. Length < 1.0" }
//        }
//        for (element in elements!!) {
//            for (i in 0 until element.indices.size()) {
//
//                // next vertex attribute
//                val idx: Int = element.indices.get(i)
//                val vertexAttribute: Vertex = verticesAttributes!![idx]
//
//                // check normals
//                require(!(vertexAttribute.normalIndex < 0 || vertexAttribute.normalIndex >= normals!!.size)) { "Wrong normal index: " + vertexAttribute.normalIndex }
//            }
//        }
//    }
//
//    fun getNormalsBuffer(): FloatBuffer? {
//        if (normalsBuffer == null && !normals!!.isEmpty()) {
//            if (verticesAttributes != null) {
//                normalsBuffer = IOUtils.createFloatBuffer(verticesAttributes.size * 3)
//                for (i in verticesAttributes.indices) {
//                    var normal = WRONG_NORMAL // no normal in case of error
//                    val index: Int = verticesAttributes[i].normalIndex
//                    if (index >= 0 && index < normals!!.size) {
//                        normal = normals!![index]
//                    } else {
//                        Log.e("MeshData", "Wrong normal index: $index")
//                    }
//                    normalsBuffer!!.put(normal)
//                }
//            } else {
//                normalsBuffer = IOUtils.createFloatBuffer(normals!!.size * 3)
//                for (i in normals!!.indices) normalsBuffer!!.put(normals!![i])
//            }
//        }
//        return normalsBuffer
//    }
//
//    fun refreshNormalsBuffer() {
//        if (normalsBuffer == null || normals!!.isEmpty() || normalsBuffer!!.capacity() != normals!!.size * 3) {
//            Log.e(
//                "MeshData",
//                "Can't refresh normals buffer. Either normals or normalsBuffer is empty"
//            )
//            return
//        }
//        Log.i("MeshData", "Refreshing normals buffer...")
//        for (i in normals!!.indices) {
//            normalsBuffer!!.put(i * 3, normals!![i][0])
//            normalsBuffer!!.put(i * 3 + 1, normals!![i][1])
//            normalsBuffer!!.put(i * 3 + 2, normals!![i][2])
//        }
//    }
//
//    fun getVerticesAttributes(): List<Vertex>? {
//        return verticesAttributes
//    }
//
//    override fun clone(): MeshData {
//        val ret = MeshData(
//            id, name, vertices, normals, colors, textures,
//            getVerticesAttributes(), getElements(), materialFile, smoothingGroups
//        )
//        ret.bindShapeMatrix = bindShapeMatrix
//        ret.jointsArray = jointsArray
//        ret.weightsArray = weightsArray
//        return ret
//    }
//
//    companion object {
//        private val WRONG_NORMAL = floatArrayOf(0f, -1f, 0f)
//
//        /**
//         * Calculate normal using high precision if needed. Otherwise return dummy normal
//         *
//         * @param v1
//         * @param v2
//         * @param v3
//         * @return
//         */
//        private fun calculateNormalFailsafe(
//            v1: FloatArray,
//            v2: FloatArray,
//            v3: FloatArray
//        ): FloatArray {
//            var normal: FloatArray = Math3DUtils.calculateNormal(v1, v2, v3)
//            try {
//                Math3DUtils.normalize(normal)
//            } catch (e: Exception) {
//                Log.e(
//                    "MeshData", "Error calculating normal. " + e.message
//                            + "," + Math3DUtils.toString(v1)
//                        .toString() + "," + Math3DUtils.toString(v2)
//                        .toString() + "," + Math3DUtils.toString(v3), e
//                )
//                normal = WRONG_NORMAL
//            }
//            return normal
//        }
//    }
//
//    init {
//        this.verticesAttributes = verticesAttributes
//        this.elements = elements
//        this.materialFile = materialFile
//        this.smoothingGroups = smoothingGroups
//    }
//}