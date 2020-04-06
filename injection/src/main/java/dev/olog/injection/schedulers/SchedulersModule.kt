package dev.olog.injection.schedulers

import dagger.Binds
import dagger.Module
import dev.olog.domain.schedulers.Schedulers

@Module
abstract class SchedulersModule {

    @Binds
    internal abstract fun provideSchedulers(impl: SchedulersProd): Schedulers

}