package dev.olog.presentation.utils

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData

fun <T> LiveData<T>.subscribe(lifecycleOwner: LifecycleOwner, func: (T) -> Unit) {
    this.observe(lifecycleOwner, android.arch.lifecycle.Observer {
        if (it != null){
            func(it)
        }
    })
}