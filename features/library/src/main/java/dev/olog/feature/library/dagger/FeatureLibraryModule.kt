package dev.olog.feature.library.dagger

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoSet
import dev.olog.feature.library.prefs.LibraryPreferencesGateway
import dev.olog.feature.library.prefs.LibraryPreferencesGatewayImpl

@Module
@InstallIn(ApplicationComponent::class)
internal abstract class FeatureLibraryModule {

    @Binds
    abstract fun providePresentationPrefs(impl: LibraryPreferencesGatewayImpl): LibraryPreferencesGateway

    @Binds
    @IntoSet
    abstract fun provideResettableLibrary(impl: LibraryPreferencesGatewayImpl): LibraryPreferencesGateway

}