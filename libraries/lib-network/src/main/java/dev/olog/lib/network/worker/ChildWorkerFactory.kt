package dev.olog.lib.network.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

interface ChildWorkerFactory {

    // assisted injection has problems with interfaces in other modules
    fun create(arg0: Context, arg1: WorkerParameters): CoroutineWorker

}