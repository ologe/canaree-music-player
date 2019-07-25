package dev.olog.data

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import dev.olog.shared.android.CustomScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class DataObserver(
    private val onUpdate: () -> Unit
) : ContentObserver(Handler(Looper.getMainLooper())), CoroutineScope by CustomScope() {

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        launch { onUpdate() }
    }
}