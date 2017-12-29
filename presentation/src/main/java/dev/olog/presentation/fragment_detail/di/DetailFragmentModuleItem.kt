package dev.olog.presentation.fragment_detail.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import dev.olog.domain.entity.*
import dev.olog.domain.interactor.detail.item.*
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper
import dev.olog.shared_android.TextUtils
import io.reactivex.Flowable

@Module
class DetailFragmentModuleItem {

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_FOLDER)
    internal fun provideFolderItem(
            resources: Resources,
            mediaId: String,
            useCase: GetFolderUseCase) : Flowable<DisplayableItem> {

        return useCase.execute(mediaId).map { it.toHeaderItem(resources) }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_PLAYLIST)
    internal fun providePlaylistItem(
            resources: Resources,
            mediaId: String,
            useCase: GetPlaylistUseCase) : Flowable<DisplayableItem> {

        return useCase.execute(mediaId).map { it.toHeaderItem(resources) }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_ALBUM)
    internal fun provideAlbumItem(
            mediaId: String,
            useCase: GetAlbumUseCase) : Flowable<DisplayableItem> {

        return useCase.execute(mediaId).map { it.toHeaderItem() }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_ARTIST)
    internal fun provideArtistItem(
            resources: Resources,
            mediaId: String,
            useCase: GetArtistUseCase) : Flowable<DisplayableItem> {

        return useCase.execute(mediaId).map { it.toHeaderItem(resources) }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_GENRE)
    internal fun provideGenreItem(
            resources: Resources,
            mediaId: String,
            useCase: GetGenreUseCase) : Flowable<DisplayableItem> {

        return useCase.execute(mediaId).map { it.toHeaderItem(resources) }
    }
}


private fun Folder.toHeaderItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_item_info,
            MediaIdHelper.folderId(path),
            title.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
            image
    )
}

private fun Playlist.toHeaderItem(resources: Resources): DisplayableItem {
    val listSize = if (this.size == -1){ "" } else {
        resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase()
    }

    return DisplayableItem(
            R.layout.item_detail_item_info,
            MediaIdHelper.playlistId(this.id),
            title.capitalize(),
            listSize,
            image
    )
}

private fun Album.toHeaderItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_item_info,
            MediaIdHelper.albumId(this.id),
            title,
            artist,
            image
    )
}

private fun Artist.toHeaderItem(resources: Resources): DisplayableItem {
    val songs = resources.getQuantityString(R.plurals.song_count, this.songs, this.songs)
    val albums = if (this.albums == 0) "" else {
        "${resources.getQuantityString(R.plurals.album_count, this.albums, this.albums)}${TextUtils.MIDDLE_DOT_SPACED}"
    }

    return DisplayableItem(
            R.layout.item_detail_item_info,
            MediaIdHelper.artistId(this.id),
            name,
            "$albums$songs".toLowerCase(),
            image
    )
}

private fun Genre.toHeaderItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_item_info,
            MediaIdHelper.genreId(this.id),
            name,
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
            image
    )
}