package dev.olog.presentation.fragment_tab.di

import android.arch.lifecycle.ViewModelProviders
import android.content.res.Resources
import android.support.v4.app.FragmentActivity
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntKey
import dagger.multibindings.IntoMap
import dev.olog.domain.interactor.tab.*
import dev.olog.presentation.R
import dev.olog.presentation.activity_main.TabViewPagerAdapter
import dev.olog.presentation.fragment_tab.TabFragmentViewModel
import dev.olog.presentation.fragment_tab.TabFragmentViewModelFactory
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.toDisplayableItem
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.rxkotlin.withLatestFrom

@Module
class TabViewModelModule {

    @Provides
    internal fun viewModel(activity: FragmentActivity, factory: TabFragmentViewModelFactory): TabFragmentViewModel {
        return ViewModelProviders.of(activity, factory).get(TabFragmentViewModel::class.java)
    }

    @Provides
    @IntoMap
    @IntKey(TabViewPagerAdapter.FOLDER)
    internal fun provideFolderData(
            resources: Resources,
            useCase: GetAllFoldersUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute().flatMapSingle{ it.toFlowable()
                .map { it.toDisplayableItem(resources) }
                .toList()
        }
    }

    @Provides
    @IntoMap
    @IntKey(TabViewPagerAdapter.PLAYLIST)
    internal fun providePlaylistData(
            resources: Resources,
            useCase: GetAllPlaylistsUseCase,
            autoPlaylistsUseCase: GetAllAutoPlaylistsUseCase): Flowable<List<DisplayableItem>> {

        val playlistsObs = useCase.execute().flatMapSingle { it.toFlowable()
                .map { it.toDisplayableItem(resources) }
                .startWith(DisplayableItem(R.layout.item_tab_header, "all playlist header", resources.getString(R.string.tab_all_playlists)))
                .toList()
        }
        val autoPlaylistsObs = autoPlaylistsUseCase.execute().flatMapSingle { it.toFlowable()
                .map { it.toDisplayableItem(resources) }
                .startWith(DisplayableItem(R.layout.item_tab_header, "auto playlist header", resources.getString(R.string.tab_auto_playlists)))
                .toList()
        }

        return playlistsObs.withLatestFrom(autoPlaylistsObs, { playlists, autoPlaylist ->
            autoPlaylist.addAll(playlists)
            autoPlaylist
        })
    }

    @Provides
    @IntoMap
    @IntKey(TabViewPagerAdapter.SONG)
    internal fun provideSongData(useCase: GetAllSongsUseCase): Flowable<List<DisplayableItem>> {
        return useCase.execute().flatMapSingle{ it.toFlowable()
                .map { it.toDisplayableItem() }
                .startWith(DisplayableItem(R.layout.item_shuffle, "shuffle id",""))
                .toList()
        }
    }

    @Provides
    @IntoMap
    @IntKey(TabViewPagerAdapter.ALBUM)
    internal fun provideAlbumData(useCase: GetAllAlbumsUseCase): Flowable<List<DisplayableItem>> {
        return useCase.execute().flatMapSingle{ it.toFlowable()
                .map { it.toDisplayableItem() }
                .toList()
        }
    }

    @Provides
    @IntoMap
    @IntKey(TabViewPagerAdapter.ARTIST)
    internal fun provideArtistData(
            resources: Resources,
            useCase: GetAllArtistsUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute().flatMapSingle{ it.toFlowable()
                .map { it.toDisplayableItem(resources) }
                .toList()
        }
    }

    @Provides
    @IntoMap
    @IntKey(TabViewPagerAdapter.GENRE)
    internal fun provideGenreData(
            resources: Resources,
            useCase: GetAllGenresUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute().flatMapSingle{ it.toFlowable()
                .map { it.toDisplayableItem(resources) }
                .toList()
        }
    }

}