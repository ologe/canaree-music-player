package dev.olog.msc.presentation.library.tab.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.MediaIdCategoryKey
import dev.olog.msc.domain.entity.*
import dev.olog.msc.domain.interactor.tab.*
import dev.olog.msc.presentation.library.tab.TabFragmentHeaders
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.k.extension.mapToList
import dev.olog.msc.utils.k.extension.startWith
import dev.olog.msc.utils.k.extension.startWithIfNotEmpty
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables

@Suppress("unused")
@Module
class TabFragmentViewModelModule {

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.FOLDERS)
    internal fun provideFolderData(
            resources: Resources,
            useCase: GetAllFoldersUseCase): Observable<List<DisplayableItem>> {

        return useCase.execute().mapToList { it.toTabDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PLAYLISTS)
    internal fun providePlaylistData(
            resources: Resources,
            useCase: GetAllPlaylistsUseCase,
            autoPlaylistUseCase: GetAllAutoPlaylistUseCase,
            headers: TabFragmentHeaders): Observable<List<DisplayableItem>> {

        val playlistObs = useCase.execute()
                .mapToList { it.toTabDisplayableItem(resources) }
                .map { it.startWithIfNotEmpty(headers.allPlaylistHeader) }

        val autoPlaylistObs = autoPlaylistUseCase.execute()
                .mapToList { it.toTabDisplayableItem(resources) }
                .map { it.startWith(headers.autoPlaylistHeader) }

        return Observables.combineLatest(playlistObs, autoPlaylistObs, { playlist, autoPlaylist ->
            autoPlaylist.plus(playlist)
        })
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.SONGS)
    internal fun provideSongData(
            useCase: GetAllSongsUseCase,
            headers: TabFragmentHeaders): Observable<List<DisplayableItem>> {

        return useCase.execute()
                .mapToList { it.toTabDisplayableItem() }
                .map { it.startWithIfNotEmpty(headers.shuffleHeader) }
    }


    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ALBUMS)
    internal fun provideAlbumData(
            useCase: GetAllAlbumsUseCase,
            lastPlayedAlbumsUseCase: GetLastPlayedAlbumsUseCase,
            headers: TabFragmentHeaders): Observable<List<DisplayableItem>> {

        val allObs = useCase.execute()
                .mapToList { it.toTabDisplayableItem() }
                .map { it.toMutableList() }

        val lastPlayedObs = lastPlayedAlbumsUseCase.execute()
                .mapToList { it.toTabDisplayableItem() }
                .map {
                    if (it.isNotEmpty()) headers.albumHeaders else it
                }
                .distinctUntilChanged()

        return Observables.combineLatest(allObs, lastPlayedObs, { all, recent -> recent.plus(all) })
    }


    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ARTISTS)
    internal fun provideArtistData(
            resources: Resources,
            useCase: GetAllArtistsUseCase,
            lastPlayedArtistsUseCase: GetLastPlayedArtistsUseCase,
            headers: TabFragmentHeaders) : Observable<List<DisplayableItem>> {

        val allObs = useCase.execute()
                .mapToList { it.toTabDisplayableItem(resources) }
                .map { it.toMutableList() }

        val lastPlayedObs = lastPlayedArtistsUseCase.execute()
                .mapToList { it.toTabDisplayableItem(resources) }
                .map {
                    if (it.isNotEmpty()) headers.artistHeaders else it
                }
                .distinctUntilChanged()

        return Observables.combineLatest(allObs, lastPlayedObs, { all, recent -> recent.plus(all) })
    }



    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.GENRES)
    internal fun provideGenreData(
            resources: Resources,
            useCase: GetAllGenresUseCase): Observable<List<DisplayableItem>> {

        return useCase.execute().mapToList { it.toTabDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.RECENT_ALBUMS)
    internal fun provideLastPlayedAlbumData(
            useCase: GetLastPlayedAlbumsUseCase): Observable<List<DisplayableItem>> {

        return useCase.execute().mapToList { it.toTabLastPlayedDisplayableItem() }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.RECENT_ARTISTS)
    internal fun provideLastPlayedArtistData(
            useCase: GetLastPlayedArtistsUseCase) : Observable<List<DisplayableItem>> {

        return useCase.execute().mapToList { it.toTabLastPlayedDisplayableItem() }
    }

}

private fun Folder.toTabDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_tab_album,
            MediaId.folderId(path),
            title,
            DisplayableItem.handleSongListSize(resources, size),
            this.image
    )
}

private fun Playlist.toTabDisplayableItem(resources: Resources): DisplayableItem {
    return DisplayableItem(
            R.layout.item_tab_album,
            MediaId.playlistId(id),
            title,
            DisplayableItem.handleSongListSize(resources, size),
            this.image
    )
}

private fun Song.toTabDisplayableItem(): DisplayableItem {
    val artist = DisplayableItem.adjustArtist(this.artist)
    val album = DisplayableItem.adjustAlbum(this.album)

    return DisplayableItem(
            R.layout.item_tab_song,
            MediaId.songId(id),
            title,
            "$artist${TextUtils.MIDDLE_DOT_SPACED}$album",
            image,
            true,
            isRemix,
            isExplicit
    )
}

private fun Album.toTabDisplayableItem(): DisplayableItem{
    return DisplayableItem(
            R.layout.item_tab_album,
            MediaId.albumId(id),
            title,
            DisplayableItem.adjustArtist(artist),
            image
    )
}

private fun Artist.toTabDisplayableItem(resources: Resources): DisplayableItem{
    val songs = DisplayableItem.handleSongListSize(resources, songs)
    var albums = DisplayableItem.handleAlbumListSize(resources, albums)
    if (albums.isNotBlank()) albums+= TextUtils.MIDDLE_DOT_SPACED

    return DisplayableItem(
            R.layout.item_tab_artist,
            MediaId.artistId(id),
            name,
            albums + songs,
            this.image
    )
}

private fun Genre.toTabDisplayableItem(resources: Resources): DisplayableItem{
    return DisplayableItem(
            R.layout.item_tab_album,
            MediaId.genreId(id),
            name,
            DisplayableItem.handleSongListSize(resources, size),
            this.image
    )
}

private fun Album.toTabLastPlayedDisplayableItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_tab_album_last_played,
            MediaId.albumId(id),
            title,
            artist,
            image
    )
}

private fun Artist.toTabLastPlayedDisplayableItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_tab_artist_last_played,
            MediaId.artistId(id),
            name,
            null,
            this.image
    )
}