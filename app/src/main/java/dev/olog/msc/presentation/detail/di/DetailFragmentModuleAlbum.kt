package dev.olog.msc.presentation.detail.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Playlist
import dev.olog.core.gateway.AlbumGateway2
import dev.olog.core.gateway.FolderGateway2
import dev.olog.core.gateway.GenreGateway2
import dev.olog.core.gateway.PlaylistGateway2
import dev.olog.msc.R
import dev.olog.presentation.dagger.MediaIdCategoryKey
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.mapToList
import io.reactivex.Observable
import kotlinx.coroutines.rx2.asObservable

@Module
class DetailFragmentModuleAlbum {

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.FOLDERS)
    internal fun provideFolderData(
            resources: Resources,
            mediaId: MediaId,
            useCase: FolderGateway2): Observable<List<DisplayableItem>> {

        return useCase.observeSiblings(mediaId.categoryValue).asObservable()
                .mapToList { it.toDetailDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PLAYLISTS)
    internal fun providePlaylistData(
            resources: Resources,
            mediaId: MediaId,
            useCase: PlaylistGateway2): Observable<List<DisplayableItem>> {

        return useCase.observeSiblings(mediaId.categoryId).asObservable()
                .mapToList { it.toDetailDisplayableItem(resources) }
    }


    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ALBUMS)
    internal fun provideAlbumData(
            resources: Resources,
            mediaId: MediaId,
            useCase: AlbumGateway2): Observable<List<DisplayableItem>> {

        return useCase.observeSiblings(mediaId.categoryId).asObservable()
                .mapToList { it.toDetailDisplayableItem(resources) }
    }


    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ARTISTS)
    internal fun provideArtistData(
            resources: Resources,
            mediaId: MediaId,
            useCase: AlbumGateway2): Observable<List<DisplayableItem>> {

        return useCase.observeArtistsAlbums(mediaId.categoryId).asObservable()
                .mapToList { it.toDetailDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.GENRES)
    internal fun provideGenreData(
            resources: Resources,
            mediaId: MediaId,
            useCase: GenreGateway2): Observable<List<DisplayableItem>> {

        return useCase.observeSiblings(mediaId.categoryId).asObservable()
                .mapToList { it.toDetailDisplayableItem(resources) }
    }


}

internal fun Folder.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album,
            MediaId.folderId(path),
            title,
            resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    )
}

internal fun Playlist.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album,
            MediaId.playlistId(id),
            title,
            resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    )
}

internal fun Album.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album,
            MediaId.albumId(id),
            title,
            resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs).toLowerCase()
    )
}

internal fun Genre.toDetailDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_detail_album,
            MediaId.genreId(id),
            name,
            resources.getQuantityString(R.plurals.common_plurals_song, this.size, this.size).toLowerCase()
    )
}