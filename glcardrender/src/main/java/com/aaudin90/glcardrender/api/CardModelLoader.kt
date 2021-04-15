package com.aaudin90.glcardrender.api

import android.app.Application
import com.aaudin90.glcardrender.internal.InternalServiceLocator
import com.aaudin90.glcardrender.internal.entity.Data3D

class CardModelLoader(
    application: Application
) {
    private val serviceLocator = InternalServiceLocator(application)

    init {
        serviceLocator.wavefrontLoader.init()
    }

    internal fun getData3D(): Data3D =
        serviceLocator.wavefrontLoader.getData3D()
}