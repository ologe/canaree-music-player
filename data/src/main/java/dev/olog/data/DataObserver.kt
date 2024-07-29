package dev.olog.data

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class DataObserver(
    private val scope: CoroutineScope,
    private val onUpdate: suspend () -> Unit
) : ContentObserver(Handler(Looper.getMainLooper())) {

    override fun onChange(selfChange: Boolean) {
        scope.launch { onUpdate() }
    }
}