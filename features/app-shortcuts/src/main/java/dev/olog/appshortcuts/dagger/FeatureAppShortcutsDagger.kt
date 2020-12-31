package dev.olog.appshortcuts.dagger

import android.content.Context
import android.content.Intent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.multibindings.IntoMap
import dev.olog.appshortcuts.ShortcutsActivity
import dev.olog.lib.media.MusicServiceAction
import dev.olog.lib.media.MusicServiceCustomAction
import dev.olog.navigation.dagger.NavigationIntentKey
import dev.olog.navigation.destination.NavigationIntent

@Module
@InstallIn(ApplicationComponent::class)
object FeatureAppShortcutsDagger {

    @Provides
    @IntoMap
    @NavigationIntentKey(NavigationIntent.SHORTCUTS_PLAY)
    fun provideShortcutsPlay(@ApplicationContext context: Context): Intent {
        val intent = Intent(context, ShortcutsActivity::class.java)
        intent.action = dev.olog.lib.media.MusicServiceAction.PLAY.name
        return intent
    }

    @Provides
    @IntoMap
    @NavigationIntentKey(NavigationIntent.SHORTCUTS_SHUFFLE)
    fun provideShortcutsShuffle(@ApplicationContext context: Context): Intent {
        val intent = Intent(context, ShortcutsActivity::class.java)
        intent.action = dev.olog.lib.media.MusicServiceCustomAction.SHUFFLE.name
        return intent
    }

}