package dev.olog.injection.schedulers

import dagger.Binds
import dagger.Module
import dev.olog.core.schedulers.Schedulers

@Module
abstract class SchedulersModule {

    @Binds
    internal abstract fun provideSchedulers(impl: SchedulersProd): Schedulers

}