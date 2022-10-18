package dev.olog.msc.app

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.core.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
class SchedulersModule {

    @Provides
    fun provideSchedulers() = Schedulers(
        io = Dispatchers.IO,
        cpu = Dispatchers.Default,
        main = Dispatchers.Main,
    )

}