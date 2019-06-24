package dev.olog.presentation.model

import dagger.Binds
import dagger.Module

@Module
abstract class PresentationModelModule {

    @Binds
    abstract fun providePresentationPrefs(impl: PresentationPreferencesImpl): PresentationPreferencesGateway

}