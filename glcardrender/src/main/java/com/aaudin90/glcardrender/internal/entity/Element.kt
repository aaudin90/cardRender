package com.aaudin90.glcardrender.internal.entity

internal data class Element(
    val id: String,
    val indices: List<Int>,
    val materialId: String
    //Если понадобится использовать несколько материалов для объекта
    //val material: Material? = null
)