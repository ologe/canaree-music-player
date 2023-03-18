package dev.olog.platform.extension

import android.app.Service
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope

val Service.lifecycleScope: LifecycleCoroutineScope
    get() {
        return lifecycleOwner.lifecycleScope
    }

val Service.lifecycleOwner: LifecycleOwner
    get() {
        require(this is LifecycleOwner)
        return this
    }