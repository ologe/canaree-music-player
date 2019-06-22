package dev.olog.data

import android.database.ContentObserver
import android.os.Handler

internal class DataObserver(
    private val onUpdate: () -> Unit
) : ContentObserver(Handler()) {

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        onUpdate()
    }
}