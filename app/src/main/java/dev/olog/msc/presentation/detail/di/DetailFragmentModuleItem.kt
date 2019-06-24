package dev.olog.msc.presentation.detail.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.*
import dev.olog.msc.R
import dev.olog.msc.domain.interactor.item.*
import dev.olog.presentation.dagger.MediaIdCategoryKey
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.TextUtils
import dev.olog.shared.asFlowable
import io.reactivex.Flowable

@Module
class DetailFragmentModuleItem {

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.FOLDERS)
    internal fun provideFolderItem(
        resources: Resources,
        mediaId: MediaId,
        useCase: GetFolderUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .map { it.toHeaderItem(resources) }
                .asFlowable()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PLAYLISTS)
    internal fun providePlaylistItem(
        resources: Resources,
        mediaId: MediaId,
        useCase: GetPlaylistUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .map { it.toHeaderItem(resources) }
                .asFlowable()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ALBUMS)
    internal fun provideAlbumItem(
        mediaId: MediaId,
        useCase: GetAlbumUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .map { it.toHeaderItem() }
                .asFlowable()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ARTISTS)
    internal fun provideArtistItem(
        resources: Resources,
        mediaId: MediaId,
        useCase: GetArtistUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .map { it.toHeaderItem(resources) }
                .asFlowable()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.GENRES)
    internal fun provideGenreItem(
        resources: Resources,
        mediaId: MediaId,
        useCase: GetGenreUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .map { it.toHeaderItem(resources) }
                .asFlowable()
    }

}


internal fun Folder.toHeaderItem(resources: Resources): List<DisplayableItem> {

    return listOf(
        DisplayableItem(
            R.layout.item_detail_item_image,
            getMediaId(),
            title,
            subtitle = resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
        )
    )
}

internal fun Playlist.toHeaderItem(resources: Resources): List<DisplayableItem> {
    val listSize = if (this.size == -1){ "" } else {
        resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    }

    return listOf(
        DisplayableItem(
            R.layout.item_detail_item_image,
            getMediaId(),
            title,
            listSize
        )
    )

}

internal fun Album.toHeaderItem(): List<DisplayableItem> {

    return listOf(
        DisplayableItem(
            R.layout.item_detail_item_image,
            getMediaId(),
            title,
            DisplayableItem.adjustArtist(this.artist)
        )
    )
}

internal fun Artist.toHeaderItem(resources: Resources): List<DisplayableItem> {
    val songs = resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs)
    val albums = if (this.albums == 0) "" else {
        "${resources.getQuantityString(R.plurals.common_plurals_album, this.albums, this.albums)}${TextUtils.MIDDLE_DOT_SPACED}"
    }

    return listOf(
        DisplayableItem(
            R.layout.item_detail_item_image,
            getMediaId(),
            name,
            "$albums$songs".toLowerCase()
        )
    )
}

internal fun Genre.toHeaderItem(resources: Resources): List<DisplayableItem> {

    return listOf(
        DisplayableItem(
            R.layout.item_detail_item_image,
            getMediaId(),
            name,
            resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
        )
    )
}