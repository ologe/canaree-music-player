package dev.olog.presentation.model

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PresentationModelModule {

    @Binds
    internal abstract fun providePresentationPrefs(impl: PresentationPreferencesImpl): PresentationPreferencesGateway

}