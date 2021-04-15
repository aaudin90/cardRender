package com.aaudin90.glcardrender.internal.entity

internal data class Element(
    val id: String,
    val indices: List<Int>,
    val materialId: String,
    val material: Material? = null
)