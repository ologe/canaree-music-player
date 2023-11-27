package dev.olog.msc.schedulers

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.core.schedulers.Schedulers

@Module
@InstallIn(SingletonComponent::class)
abstract class SchedulersModule {

    @Binds
    internal abstract fun provideSchedulers(impl: SchedulersProd): Schedulers

}