package dev.olog.presentation.model

import dagger.Binds
import dagger.Module
import dev.olog.presentation.dagger.PerActivity

@Module
abstract class PresentationModelModule {

    @Binds
    @PerActivity
    internal abstract fun providePresentationPrefs(impl: PresentationPreferencesImpl): PresentationPreferencesGateway

}