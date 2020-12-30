package dev.olog.feature.edit.playlist.dagger

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.multibindings.IntoMap
import dev.olog.feature.edit.playlist.choose.PlaylistChooserActivity
import dev.olog.feature.edit.playlist.create.CreatePlaylistFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.dagger.NavigationIntentKey
import dev.olog.navigation.destination.FragmentScreen
import dev.olog.navigation.destination.NavigationIntent

@Module
@InstallIn(ApplicationComponent::class)
object FeatureEditPlaylistDagger {

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.CREATE_PLAYLIST)
    fun provideCreatePlaylistFragment(): Fragment = CreatePlaylistFragment()

    @Provides
    @IntoMap
    @NavigationIntentKey(NavigationIntent.PLAYLIST_CHOOSER)
    fun providePlaylistChooser(@ApplicationContext context: Context): Intent {
        val intent = Intent(context, PlaylistChooserActivity::class.java)
        intent.action = "" // crash, intent's action must be set
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return intent
    }

}