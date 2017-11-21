package dev.olog.msc

import dagger.Binds
import dagger.Module
import dev.olog.domain.executor.ComputationScheduler
import dev.olog.domain.executor.IoScheduler
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Module
abstract class SchedulersModule {

    @Binds
    @Singleton
    abstract fun provideComputationSchedulers(scheduler: ComputationSchedulers) : ComputationScheduler

    @Binds
    @Singleton
    abstract fun provideIoSchedulers(scheduler: IoSchedulers) : IoScheduler

}

class ComputationSchedulers @Inject constructor(): ComputationScheduler {

    override val worker: Scheduler
        get() = Schedulers.computation()

    override val ui: Scheduler
        get() = AndroidSchedulers.mainThread()
}

class IoSchedulers @Inject constructor(): IoScheduler {

    override val worker: Scheduler
        get() = Schedulers.io()

    override val ui: Scheduler
        get() = AndroidSchedulers.mainThread()
}