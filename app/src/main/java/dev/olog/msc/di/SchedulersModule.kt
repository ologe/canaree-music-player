package dev.olog.msc.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.core.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SchedulersModule {

    @Provides
    @Singleton
    fun provideSchedulers(): Schedulers = Schedulers(
        io = Dispatchers.IO,
        cpu = Dispatchers.Default,
        main = Dispatchers.Main,
    )

}