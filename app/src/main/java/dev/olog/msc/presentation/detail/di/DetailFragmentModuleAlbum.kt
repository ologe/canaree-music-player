package dev.olog.msc.presentation.detail.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.MediaIdCategoryKey
import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.entity.Folder
import dev.olog.msc.domain.entity.Genre
import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.interactor.all.sibling.*
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.k.extension.mapToList
import io.reactivex.Observable

@Module
class DetailFragmentModuleAlbum {

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.FOLDERS)
    internal fun provideFolderData(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetFolderSiblingsUseCase): Observable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .mapToList { it.toDetailDisplayableItem(resources) }
                .onErrorReturnItem(listOf())
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PLAYLISTS)
    internal fun providePlaylistData(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetPlaylistSiblingsUseCase): Observable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .mapToList { it.toDetailDisplayableItem(resources) }
                .onErrorReturnItem(listOf())
    }


    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ALBUMS)
    internal fun provideAlbumData(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetAlbumSiblingsByAlbumUseCase): Observable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .mapToList { it.toDetailDisplayableItem(resources) }
                .onErrorReturnItem(listOf())
    }


    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ARTISTS)
    internal fun provideArtistData(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetAlbumSiblingsByArtistUseCase): Observable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .mapToList { it.toDetailDisplayableItem(resources) }
                .onErrorReturnItem(listOf())
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.GENRES)
    internal fun provideGenreData(
            resources: Resources,
            mediaId: MediaId,
            useCase: GetGenreSiblingsUseCase): Observable<List<DisplayableItem>> {

        return useCase.execute(mediaId)
                .mapToList { it.toDetailDisplayableItem(resources) }
                .onErrorReturnItem(listOf())
    }



}

private fun Folder.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album,
            MediaId.folderId(path),
            title,
            resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase(),
            this.image
    )
}

private fun Playlist.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album,
            MediaId.playlistId(id),
            title,
            resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase(),
            this.image
    )
}

private fun Album.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album,
            MediaId.albumId(id),
            title,
            resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs).toLowerCase(),
            image
    )
}

private fun Genre.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album,
            MediaId.genreId(id),
            name,
            resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase(),
            this.image
    )
}