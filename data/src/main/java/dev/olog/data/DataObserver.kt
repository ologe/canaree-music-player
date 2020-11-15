package dev.olog.data

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import dev.olog.shared.launchUnit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope

internal class DataObserver(
    private val scheduler: CoroutineDispatcher,
    private val onUpdate: suspend () -> Unit
) : ContentObserver(Handler(Looper.getMainLooper())) {

    override fun onChange(selfChange: Boolean) = GlobalScope.launchUnit(scheduler) {
        onUpdate()
    }
}