package dev.olog.platform.extension

import android.app.Application
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope

val Application.lifecycleScope: LifecycleCoroutineScope
    get() = lifecycleOwner.lifecycleScope

@Suppress("UnusedReceiverParameter")
val Application.lifecycleOwner: LifecycleOwner
    get() = ProcessLifecycleOwner.get()