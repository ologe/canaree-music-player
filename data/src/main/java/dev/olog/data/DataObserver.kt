package dev.olog.data

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper

internal class DataObserver(
    private val onUpdate: () -> Unit
) : ContentObserver(Handler(Looper.getMainLooper())) {

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        onUpdate()
    }
}