package dev.olog.lib.network.worker

import androidx.work.CoroutineWorker
import dagger.MapKey
import kotlin.reflect.KClass

@MapKey
annotation class WorkerKey(val value: KClass<out CoroutineWorker>)