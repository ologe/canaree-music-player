package dev.olog.shared.android.extensions

import android.app.Service
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope

val Service.lifecycleScope: LifecycleCoroutineScope
    get() {
        require(this is LifecycleOwner)
        return lifecycle.coroutineScope
    }

val Service.lifecycleOwner: LifecycleOwner
    get() {
        require(this is LifecycleOwner)
        return this
    }