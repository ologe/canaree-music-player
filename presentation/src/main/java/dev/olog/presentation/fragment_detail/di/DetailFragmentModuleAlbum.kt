package dev.olog.presentation.fragment_detail.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import dev.olog.domain.interactor.detail.siblings.*
import dev.olog.presentation.fragment_detail.model.toDetailDisplayableItem
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import io.reactivex.rxkotlin.toFlowable

@Module
class DetailFragmentModuleAlbum {

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_FOLDER)
    internal fun provideFolderData(
            resources: Resources,
            mediaId: String,
            useCase: GetFolderSiblingsUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).flatMapSingle { it.toFlowable()
                .map { it.toDetailDisplayableItem(resources) }
                .toList()
        }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_PLAYLIST)
    internal fun providePlaylistData(
            resources: Resources,
            mediaId: String,
            useCase: GetPlaylistSiblingsUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).flatMapSingle {
            it.toFlowable().map { it.toDetailDisplayableItem(resources) }.toList()
        }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_ALBUM)
    internal fun provideAlbumData(
            resources: Resources,
            mediaId: String,
            useCase: GetAlbumSiblingsByAlbumUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).flatMapSingle {
            it.toFlowable().map { it.toDetailDisplayableItem(resources) }.toList()
        }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_ARTIST)
    internal fun provideArtistData(
            resources: Resources,
            mediaId: String,
            useCase: GetAlbumSiblingsByArtistUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).flatMapSingle {
            it.toFlowable().map { it.toDetailDisplayableItem(resources) }.toList()
        }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_GENRE)
    internal fun provideGenreData(
            resources: Resources,
            mediaId: String,
            useCase: GetGenreSiblingsUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).flatMapSingle {
            it.toFlowable().map { it.toDetailDisplayableItem(resources) }.toList()
        }
    }

}