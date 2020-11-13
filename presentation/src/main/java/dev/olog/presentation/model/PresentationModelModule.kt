package dev.olog.presentation.model

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
internal abstract class PresentationModelModule {

    @Binds
    internal abstract fun providePresentationPrefs(impl: PresentationPreferencesImpl): PresentationPreferencesGateway

}