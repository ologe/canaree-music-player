package dev.olog.injection.schedulers

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.olog.core.schedulers.Schedulers

@Module
@InstallIn(ApplicationComponent::class)
internal abstract class SchedulersModule {

    @Binds
    internal abstract fun provideSchedulers(impl: SchedulersProd): Schedulers

}