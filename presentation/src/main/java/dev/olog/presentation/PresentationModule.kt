package dev.olog.presentation

import android.content.Context
import android.content.Intent
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.multibindings.IntoMap
import dev.olog.navigation.Navigator
import dev.olog.navigation.PopupMenuFactory
import dev.olog.navigation.dagger.NavigationIntentKey
import dev.olog.navigation.destination.NavigationIntent
import dev.olog.presentation.main.MainActivity
import dev.olog.presentation.popup.PopupMenuFactoryImpl
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
internal abstract class PresentationModule {

    @Binds
    @Singleton
    abstract fun providePopupFactory(impl: PopupMenuFactoryImpl): PopupMenuFactory

    companion object {

        @Provides
        @IntoMap
        @NavigationIntentKey(NavigationIntent.MAIN_ACTIVITY)
        fun provideMainActivity(@ApplicationContext context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }

        @Provides
        @IntoMap
        @NavigationIntentKey(NavigationIntent.SEARCH)
        fun providePlaylistChooser(@ApplicationContext context: Context): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.action = Navigator.INTENT_ACTION_SEARCH
            return intent
        }

        @Provides
        @IntoMap
        @NavigationIntentKey(NavigationIntent.DETAIL)
        fun provideDetail(@ApplicationContext context: Context): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.action = Navigator.INTENT_ACTION_DETAIL
            return intent
        }


    }

}