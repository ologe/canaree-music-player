package dev.olog.msc.schedulers

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.olog.domain.schedulers.Schedulers

@Module
@InstallIn(ApplicationComponent::class)
abstract class SchedulersModule {

    @Binds
    internal abstract fun provideSchedulers(impl: SchedulersProd): Schedulers

}