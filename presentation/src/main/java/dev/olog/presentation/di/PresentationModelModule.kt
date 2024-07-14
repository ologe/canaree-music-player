package dev.olog.presentation.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.presentation.model.PresentationPreferencesImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class PresentationModelModule {

    @Binds
    internal abstract fun providePresentationPrefs(impl: PresentationPreferencesImpl): PresentationPreferencesGateway

}