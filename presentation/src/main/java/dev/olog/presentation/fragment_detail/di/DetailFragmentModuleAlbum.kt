package dev.olog.presentation.fragment_detail.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Folder
import dev.olog.domain.entity.Genre
import dev.olog.domain.entity.Playlist
import dev.olog.domain.interactor.detail.siblings.*
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.groupMap
import io.reactivex.Flowable

@Module
class DetailFragmentModuleAlbum {

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_FOLDER)
    internal fun provideFolderData(
            resources: Resources,
            mediaId: String,
            useCase: GetFolderSiblingsUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toDetailDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_PLAYLIST)
    internal fun providePlaylistData(
            resources: Resources,
            mediaId: String,
            useCase: GetPlaylistSiblingsUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toDetailDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_ALBUM)
    internal fun provideAlbumData(
            resources: Resources,
            mediaId: String,
            useCase: GetAlbumSiblingsByAlbumUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toDetailDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_ARTIST)
    internal fun provideArtistData(
            resources: Resources,
            mediaId: String,
            useCase: GetAlbumSiblingsByArtistUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toDetailDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @StringKey(MediaIdHelper.MEDIA_ID_BY_GENRE)
    internal fun provideGenreData(
            resources: Resources,
            mediaId: String,
            useCase: GetGenreSiblingsUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toDetailDisplayableItem(resources) }
    }
}

private fun Folder.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album_mini,
            MediaIdHelper.folderId(path),
            title.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
            this.image
    )
}

private fun Playlist.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album_mini,
            MediaIdHelper.playlistId(id),
            title.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
            this.image
    )
}

private fun Album.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album,
            MediaIdHelper.albumId(id),
            title,
            resources.getQuantityString(R.plurals.song_count, this.songs, this.songs).toLowerCase(),
            image
    )
}

private fun Genre.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album_mini,
            MediaIdHelper.genreId(id),
            name.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
            this.image
    )
}