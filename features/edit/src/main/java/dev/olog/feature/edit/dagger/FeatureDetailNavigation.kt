package dev.olog.feature.edit.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap
import dev.olog.feature.edit.album.EditAlbumFragment
import dev.olog.feature.edit.artist.EditArtistFragment
import dev.olog.feature.edit.track.EditTrackFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.destination.FragmentScreen

@Module
@InstallIn(ApplicationComponent::class)
object FeatureEditNavigation {

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.EDIT_TRACK)
    fun provideEditTrackFragment(): Fragment = EditTrackFragment()

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.EDIT_ALBUM)
    fun provideEditAlbumFragment(): Fragment = EditAlbumFragment()

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.EDIT_ARTIST)
    fun provideEditArtistFragment(): Fragment = EditArtistFragment()

}