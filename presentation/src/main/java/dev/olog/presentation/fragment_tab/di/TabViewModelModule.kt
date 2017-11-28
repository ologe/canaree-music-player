package dev.olog.presentation.fragment_tab.di

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntKey
import dagger.multibindings.IntoMap
import dev.olog.domain.interactor.tab.*
import dev.olog.presentation.activity_main.TabViewPagerAdapter
import dev.olog.presentation.fragment_tab.TabFragmentViewModel
import dev.olog.presentation.fragment_tab.TabFragmentViewModelFactory
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.toDisplayableItem
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable

@Module
class TabViewModelModule {

    @Provides
    internal fun viewModel(activity: FragmentActivity, factory: TabFragmentViewModelFactory): TabFragmentViewModel {
        return ViewModelProviders.of(activity, factory).get(TabFragmentViewModel::class.java)
    }

    @Provides
    @IntoMap
    @IntKey(TabViewPagerAdapter.FOLDER)
    internal fun provideFolderData(useCase: GetAllFoldersUseCase): Flowable<List<DisplayableItem>> {
        return useCase.execute().flatMapSingle{ it.toFlowable()
                .map { it.toDisplayableItem() }
                .toList()
        }
    }

    @Provides
    @IntoMap
    @IntKey(TabViewPagerAdapter.PLAYLIST)
    internal fun providePlaylistData(useCase: GetAllPlaylistsUseCase): Flowable<List<DisplayableItem>> {
        return useCase.execute().flatMapSingle{ it.toFlowable()
                .map { it.toDisplayableItem() }
                .toList()
        }
    }

    @Provides
    @IntoMap
    @IntKey(TabViewPagerAdapter.SONG)
    internal fun provideSongData(useCase: GetAllSongsUseCase): Flowable<List<DisplayableItem>> {
        return useCase.execute().flatMapSingle{ it.toFlowable()
                .map { it.toDisplayableItem() }
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
    internal fun provideArtistData(useCase: GetAllArtistsUseCase): Flowable<List<DisplayableItem>> {
        return useCase.execute().flatMapSingle{ it.toFlowable()
                .map { it.toDisplayableItem() }
                .toList()
        }
    }

    @Provides
    @IntoMap
    @IntKey(TabViewPagerAdapter.GENRE)
    internal fun provideGenreData(useCase: GetAllGenresUseCase): Flowable<List<DisplayableItem>> {
        return useCase.execute().flatMapSingle{ it.toFlowable()
                .map { it.toDisplayableItem() }
                .toList()
        }
    }

}