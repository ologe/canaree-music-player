package dev.olog.core.dagger

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds
import dev.olog.core.AppInitializer

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModule {

    @Multibinds
    abstract fun provideAppInitializers(): Set<AppInitializer>

}