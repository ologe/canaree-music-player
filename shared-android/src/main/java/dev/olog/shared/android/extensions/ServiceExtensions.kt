package dev.olog.shared.android.extensions

import android.app.Service
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope

val Service.lifecycleScope: LifecycleCoroutineScope
    get() {
        require(this is LifecycleOwner)
        return lifecycle.coroutineScope
    }

val Service.lifecycle: Lifecycle
    get() {
        require(this is LifecycleOwner)
        return lifecycle
    }