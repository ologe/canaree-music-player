package dev.olog.presentation.fragment_albums.di

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import dev.olog.domain.interactor.detail.GetAlbumsSizeUseCase
import dev.olog.domain.interactor.detail.siblings.*
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.fragment_albums.AlbumsFragment
import dev.olog.presentation.fragment_albums.AlbumsFragmentViewModel
import dev.olog.presentation.fragment_albums.AlbumsFragmentViewModelFactory
import dev.olog.presentation.fragment_detail.model.toDetailDisplayableItem
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.ApplicationContext
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers

@Module
class AlbumsFragmentModule(
       private val fragment: AlbumsFragment
) {

    @Provides
    @FragmentLifecycle
    internal fun lifecycle(): Lifecycle = fragment.lifecycle

    @Provides
    internal fun provideMediaId(): String {
        return fragment.arguments!!.getString(AlbumsFragment.ARGUMENTS_MEDIA_ID)
    }

    @Provides
    internal fun provideViewModel(factory: AlbumsFragmentViewModelFactory): AlbumsFragmentViewModel {

        return ViewModelProviders.of(fragment, factory).get(AlbumsFragmentViewModel::class.java)
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_FOLDER)
    internal fun provideFolderData(
            @ApplicationContext context: Context,
            mediaId: String,
            useCase: GetFolderSiblingsUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).flatMapSingle { it.toFlowable()
                .map { it.toDetailDisplayableItem(context) }
                .toList()
        }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_PLAYLIST)
    internal fun providePlaylistData(
            resources: Resources,
            mediaId: String,
            useCase: GetPlaylistSiblingsUseCase,
            albumsSizeUseCase: GetAlbumsSizeUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).flatMapSingle {
            it.toFlowable().flatMapSingle { playlist ->
                albumsSizeUseCase.execute(MediaIdHelper.playlistId(playlist.id))
                        .map { playlist.toDetailDisplayableItem(resources, it) }
                        .firstOrError().subscribeOn(Schedulers
                        .computation())
            }.toList()
        }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_ALBUM)
    internal fun provideAlbumData(mediaId: String,
                                  useCase: GetAlbumSiblingsByAlbumUseCase)
            : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).flatMapSingle {
            it.toFlowable().map { it.toDetailDisplayableItem() }.toList()
        }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_ARTIST)
    internal fun provideArtistData(mediaId: String,
                                   useCase: GetAlbumSiblingsByArtistUseCase)
            : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).flatMapSingle {
            it.toFlowable().map { it.toDetailDisplayableItem() }.toList()
        }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_GENRE)
    internal fun provideGenreData(
            resources: Resources,
            mediaId: String,
            useCase: GetGenreSiblingsUseCase,
            albumsSizeUseCase: GetAlbumsSizeUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).flatMapSingle {
            it.toFlowable().flatMapSingle { genre ->
                albumsSizeUseCase.execute(MediaIdHelper.genreId(genre.id))
                        .map { genre.toDetailDisplayableItem(resources, it) }
                        .firstOrError().subscribeOn(Schedulers
                        .computation())
            }.toList()
        }
    }

}