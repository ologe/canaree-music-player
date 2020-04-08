package dev.olog.presentation.model

import dagger.Binds
import dagger.Module
import dev.olog.core.dagger.FeatureScope

@Module
abstract class PresentationModelModule {

    @Binds
    @FeatureScope
    internal abstract fun providePresentationPrefs(impl: PresentationPreferencesImpl): PresentationPreferencesGateway

}