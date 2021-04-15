package com.aaudin90.glcardrender.internal.entity

import java.util.*

internal class VertexSkinData {
    val jointIds: MutableList<Int> = ArrayList(3)
    val weights: MutableList<Float> = ArrayList(3)

    fun addJointEffect(jointId: Int, weight: Float) {
        for (i in weights.indices) {
            if (weight > weights[i]) {
                jointIds.add(i, jointId)
                weights.add(i, weight)
                return
            }
        }
        jointIds.add(jointId)
        weights.add(weight)
    }

    fun limitJointNumber(max: Int) {
        if (jointIds.size > max) {
            val topWeights = FloatArray(max)
            val total = saveTopWeights(topWeights)
            refillWeightList(topWeights, total)
            removeExcessJointIds(max)
        } else if (jointIds.size < max) {
            fillEmptyWeights(max)
        }
    }

    private fun fillEmptyWeights(max: Int) {
        while (jointIds.size < max) {
            jointIds.add(0)
            weights.add(0f)
        }
    }

    private fun saveTopWeights(topWeightsArray: FloatArray): Float {
        var total = 0f
        for (i in topWeightsArray.indices) {
            topWeightsArray[i] = weights[i]
            total += topWeightsArray[i]
        }
        return total
    }

    private fun refillWeightList(topWeights: FloatArray, total: Float) {
        weights.clear()
        for (i in topWeights.indices) {
            weights.add(Math.min(topWeights[i] / total, 1f))
        }
    }

    private fun removeExcessJointIds(max: Int) {
        while (jointIds.size > max) {
            jointIds.removeAt(jointIds.size - 1)
        }
    }
}