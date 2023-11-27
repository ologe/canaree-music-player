package dev.olog.msc.schedulers

import dev.olog.core.schedulers.Schedulers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

internal class SchedulersProd @Inject constructor(

): Schedulers {

    override val io: CoroutineDispatcher
        get() = Dispatchers.IO

    override val cpu: CoroutineDispatcher
        get() = Dispatchers.Default

    override val main: CoroutineDispatcher
        get() = Dispatchers.Main
}