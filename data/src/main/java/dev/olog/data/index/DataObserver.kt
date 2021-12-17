package dev.olog.data.index

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import dev.olog.shared.autoDisposeJob
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class DataObserver(
    private val appScope: CoroutineScope,
    private val scheduler: CoroutineDispatcher,
    private val onUpdate: suspend () -> Unit
) : ContentObserver(Handler(Looper.getMainLooper())) {

    private var disposable by autoDisposeJob()

    override fun onChange(selfChange: Boolean) {
        disposable = appScope.launch(scheduler) {
            onUpdate()
        }
    }
}