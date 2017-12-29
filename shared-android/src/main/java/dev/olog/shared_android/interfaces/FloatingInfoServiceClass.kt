package dev.olog.shared_android.interfaces

import android.app.Service

interface FloatingInfoServiceClass {

    fun get(): Class<out Service>

}