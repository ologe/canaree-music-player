package dev.olog.presentation

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.olog.navigation.PopupMenuFactory
import dev.olog.presentation.popup.PopupMenuFactoryImpl
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
internal abstract class PresentationModule {

    @Binds
    @Singleton
    abstract fun providePopupFactory(impl: PopupMenuFactoryImpl): PopupMenuFactory

}