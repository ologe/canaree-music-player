package dev.olog.service.music.dagger

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.multibindings.IntoMap
import dev.olog.lib.media.MusicServiceAction
import dev.olog.navigation.dagger.NavigationIntentKey
import dev.olog.navigation.destination.NavigationIntent
import dev.olog.service.music.MusicService

@Module
@InstallIn(ApplicationComponent::class)
internal object FeatureMusicServiceDagger {

    @Provides
    @IntoMap
    @NavigationIntentKey(NavigationIntent.MUSIC_SERVICE)
    fun provideServiceIntentIntent(@ApplicationContext context: Context): Intent {
        return Intent(context, MusicService::class.java)
    }

    @Provides
    @IntoMap
    @NavigationIntentKey(NavigationIntent.MUSIC_SERVICE_PLAY_FROM_SEARCH)
    fun providePlayFromSearchIntent(@ApplicationContext context: Context): Intent {
        return Intent(context, MusicService::class.java).apply {
            action = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH
        }
    }

    @Provides
    @IntoMap
    @NavigationIntentKey(NavigationIntent.MUSIC_SERVICE_PLAY_URI)
    fun providePlayFromUriIntent(@ApplicationContext context: Context): Intent {
        return Intent(context, MusicService::class.java).apply {
            action = MusicServiceAction.PLAY_URI.name
        }
    }

}