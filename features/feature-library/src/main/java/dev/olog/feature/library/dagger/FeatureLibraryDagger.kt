package dev.olog.feature.library.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.core.dagger.FeatureScope
import dev.olog.feature.library.album.AlbumsFragment
import dev.olog.feature.library.artist.ArtistsFragment
import dev.olog.feature.library.dagger.module.*
import dev.olog.feature.library.folder.FoldersFragment
import dev.olog.feature.library.folder.normal.FoldersNormalFragment
import dev.olog.feature.library.folder.tree.FoldersTreeFragment
import dev.olog.feature.library.genre.GenresFragment
import dev.olog.feature.library.home.HomeFragment
import dev.olog.feature.library.playlists.PlaylistsFragment
import dev.olog.feature.library.tracks.TracksFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.screens.FragmentScreen

class FeatureLibraryDagger {

    @Module
    abstract class AppModule {

        @ContributesAndroidInjector(modules = [HomeFragmentModule::class])
        @FeatureScope
        internal abstract fun provideHomeFragment(): HomeFragment

        @ContributesAndroidInjector(modules = [TracksFragmentModule::class])
        @FeatureScope
        internal abstract fun provideTabAltFragment(): TracksFragment

        @ContributesAndroidInjector(modules = [PlaylistsFragmentModule::class])
        @FeatureScope
        internal abstract fun providePlaylistFragment(): PlaylistsFragment

        @ContributesAndroidInjector(modules = [AlbumsFragmentModule::class])
        @FeatureScope
        internal abstract fun provideAlbumsFragment(): AlbumsFragment

        @ContributesAndroidInjector(modules = [ArtistsFragmentModule::class])
        @FeatureScope
        internal abstract fun provideArtistsFragment(): ArtistsFragment

        // genres
        @ContributesAndroidInjector(modules = [GenresFragmentModule::class])
        @FeatureScope
        internal abstract fun provideGenreFragment(): GenresFragment

        // folders
        @ContributesAndroidInjector
        @FeatureScope
        internal abstract fun provideFolderFragment(): FoldersFragment

        @ContributesAndroidInjector(modules = [FoldersNormalFragmentModule::class])
        @FeatureScope
        internal abstract fun provideFolderNormalFragment(): FoldersNormalFragment

        @ContributesAndroidInjector(modules = [FoldersTreeFragmentModule::class])
        @FeatureScope
        internal abstract fun provideFolderTreeFragment(): FoldersTreeFragment

        companion object {

            @Provides
            @IntoMap
            @FragmentScreenKey(FragmentScreen.HOME)
            internal fun provideHomeFragment(): Fragment {
                return HomeFragment()
            }

            @Provides
            @IntoMap
            @FragmentScreenKey(FragmentScreen.TRACKS)
            internal fun provideTracksFragment(): Fragment {
                return TracksFragment()
            }

            @Provides
            @IntoMap
            @FragmentScreenKey(FragmentScreen.PLAYLISTS)
            internal fun providePlaylistFragment(): Fragment {
                return PlaylistsFragment()
            }

            @Provides
            @IntoMap
            @FragmentScreenKey(FragmentScreen.ALBUMS)
            internal fun provideAlbumsFragment(): Fragment {
                return AlbumsFragment()
            }

            @Provides
            @IntoMap
            @FragmentScreenKey(FragmentScreen.ARTISTS)
            internal fun provideArtistFragment(): Fragment {
                return ArtistsFragment()
            }

            @Provides
            @IntoMap
            @FragmentScreenKey(FragmentScreen.GENRES)
            internal fun provideGenreFragment(): Fragment {
                return GenresFragment()
            }

            @Provides
            @IntoMap
            @FragmentScreenKey(FragmentScreen.FOLDERS)
            internal fun provideFolderFragment(): Fragment {
                return FoldersFragment()
            }

        }

    }

}