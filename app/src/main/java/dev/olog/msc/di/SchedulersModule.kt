package dev.olog.msc.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.core.schedulers.Schedulers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SchedulersModule {

    @Binds
    @Singleton
    internal abstract fun provideSchedulers(impl: SchedulersProd): Schedulers

}