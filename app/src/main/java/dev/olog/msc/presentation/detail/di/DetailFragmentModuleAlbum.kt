package dev.olog.msc.presentation.detail.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.R
import dev.olog.msc.dagger.MediaIdCategoryKey
import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.entity.Folder
import dev.olog.msc.domain.entity.Genre
import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.interactor.detail.siblings.*
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.k.extension.groupMap
import io.reactivex.Flowable

@Module
class DetailFragmentModuleAlbum {

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.FOLDER)
    internal fun provideFolderData(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetFolderSiblingsUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toDetailDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PLAYLIST)
    internal fun providePlaylistData(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetPlaylistSiblingsUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toDetailDisplayableItem(resources) }
    }


    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ALBUM)
    internal fun provideAlbumData(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetAlbumSiblingsByAlbumUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toDetailDisplayableItem(resources) }
    }


    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ARTIST)
    internal fun provideArtistData(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetAlbumSiblingsByArtistUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toDetailDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.GENRE)
    internal fun provideGenreData(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetGenreSiblingsUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute(mediaId).groupMap { it.toDetailDisplayableItem(resources) }
    }
}

private fun Folder.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album_mini,
            MediaId.folderId(path),
            title.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
            this.image
    )
}

private fun Playlist.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album_mini,
            MediaId.playlistId(id),
            title.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
            this.image
    )
}

private fun Album.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album,
            MediaId.albumId(id),
            title,
            resources.getQuantityString(R.plurals.song_count, this.songs, this.songs).toLowerCase(),
            image
    )
}

private fun Genre.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album_mini,
            MediaId.genreId(id),
            name.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
            this.image
    )
}