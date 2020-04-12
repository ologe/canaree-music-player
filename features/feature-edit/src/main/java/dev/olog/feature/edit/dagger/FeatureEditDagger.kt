package dev.olog.feature.edit.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.feature.edit.album.EditAlbumFragment
import dev.olog.feature.edit.artist.EditArtistFragment
import dev.olog.feature.edit.track.EditTrackFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.screens.FragmentScreen

class FeatureEditDagger {

    @Module
    abstract class AppModule {

        @ContributesAndroidInjector(modules = [EditTrackModule::class])
        internal abstract fun provideEditTrackFragment(): EditTrackFragment

        @ContributesAndroidInjector(modules = [EditAlbumModule::class])
        internal abstract fun provideEditAlbumFragment(): EditAlbumFragment

        @ContributesAndroidInjector(modules = [EditArtistModule::class])
        internal abstract fun provideEditArtistFragment(): EditArtistFragment

        companion object {

            @Provides
            @IntoMap
            @FragmentScreenKey(FragmentScreen.EDIT_TRACK)
            internal fun provideTrackFragment(): Fragment {
                return EditTrackFragment()
            }

            @Provides
            @IntoMap
            @FragmentScreenKey(FragmentScreen.EDIT_ARTIST)
            internal fun provideArtistFragment(): Fragment {
                return EditArtistFragment()
            }

            @Provides
            @IntoMap
            @FragmentScreenKey(FragmentScreen.EDIT_ALBUM)
            internal fun provideAlbumFragment(): Fragment {
                return EditAlbumFragment()
            }

        }

    }

}