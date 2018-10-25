package dev.olog.msc.presentation.library.tab.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.MediaIdCategoryKey
import dev.olog.msc.domain.entity.*
import dev.olog.msc.domain.interactor.all.GetAllAutoPlaylistUseCase
import dev.olog.msc.domain.interactor.all.GetAllFoldersUseCase
import dev.olog.msc.domain.interactor.all.GetAllGenresUseCase
import dev.olog.msc.domain.interactor.all.GetAllPlaylistsUseCase
import dev.olog.msc.domain.interactor.all.last.played.GetLastPlayedAlbumsUseCase
import dev.olog.msc.domain.interactor.all.last.played.GetLastPlayedArtistsUseCase
import dev.olog.msc.domain.interactor.all.recently.added.GetRecentlyAddedAlbumsUseCase
import dev.olog.msc.domain.interactor.all.recently.added.GetRecentlyAddedArtistsUseCase
import dev.olog.msc.domain.interactor.all.sorted.GetAllAlbumsSortedUseCase
import dev.olog.msc.domain.interactor.all.sorted.GetAllArtistsSortedUseCase
import dev.olog.msc.domain.interactor.all.sorted.GetAllSongsSortedUseCase
import dev.olog.msc.presentation.library.tab.TabFragmentHeaders
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.k.extension.*
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
                .defer()
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
                .defer()

        val autoPlaylistObs = autoPlaylistUseCase.execute()
                .mapToList { it.toAutoPlaylist() }
                .map { it.startWith(headers.autoPlaylistHeader) }
                .defer()

        return Observables.combineLatest(playlistObs, autoPlaylistObs) { playlist, autoPlaylist ->
            autoPlaylist.plus(playlist)
        }.defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.SONGS)
    internal fun provideSongData(
            useCase: GetAllSongsSortedUseCase,
            headers: TabFragmentHeaders): Observable<List<DisplayableItem>> {

        return useCase.execute()
                .mapToList { it.toTabDisplayableItem() }
                .map { it.startWithIfNotEmpty(headers.shuffleHeader) }
                .defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ALBUMS)
    internal fun provideAlbumData(
            useCase: GetAllAlbumsSortedUseCase,
            lastPlayedAlbumsUseCase: GetLastPlayedAlbumsUseCase,
            newAlbumsUseCase: GetRecentlyAddedAlbumsUseCase,
            headers: TabFragmentHeaders): Observable<List<DisplayableItem>> {

        val allObs = useCase.execute()
                .mapToList { it.toTabDisplayableItem() }
                .map { it.toMutableList() }
                .defer()

        val lastPlayedObs = Observables.combineLatest(
                lastPlayedAlbumsUseCase.execute().distinctUntilChanged(),
                newAlbumsUseCase.execute().distinctUntilChanged()
        ) { last, new ->
            val result = mutableListOf<DisplayableItem>()
            result.doIf(new.count() > 0) { addAll(headers.newAlbumsHeaders) }
                    .doIf(last.count() > 0) { addAll(headers.recentAlbumHeaders) }
                    .doIf(result.isNotEmpty()) { addAll(headers.allAlbumsHeader) }
        }.distinctUntilChanged()
                .defer()

        return Observables.combineLatest(allObs, lastPlayedObs) { all, recent -> recent.plus(all) }
                .defer()
    }




    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ARTISTS)
    internal fun provideArtistData(
            resources: Resources,
            useCase: GetAllArtistsSortedUseCase,
            lastPlayedArtistsUseCase: GetLastPlayedArtistsUseCase,
            newArtistsUseCase: GetRecentlyAddedArtistsUseCase,
            headers: TabFragmentHeaders) : Observable<List<DisplayableItem>> {

        val allObs = useCase.execute()
                .mapToList { it.toTabDisplayableItem(resources) }
                .map { it.toMutableList() }
                .defer()

        val lastPlayedObs = Observables.combineLatest(
                lastPlayedArtistsUseCase.execute().distinctUntilChanged(),
                newArtistsUseCase.execute().distinctUntilChanged()
        ) { last, new ->
            val result = mutableListOf<DisplayableItem>()
            result.doIf(new.count() > 0) { addAll(headers.newArtistsHeaders) }
                    .doIf(last.count() > 0) { addAll(headers.recentArtistHeaders) }
                    .doIf(result.isNotEmpty()) { addAll(headers.allArtistsHeader) }
        }.distinctUntilChanged()
                .defer()

        return Observables.combineLatest(allObs, lastPlayedObs) { all, recent -> recent.plus(all) }
                .defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.GENRES)
    internal fun provideGenreData(
            resources: Resources,
            useCase: GetAllGenresUseCase): Observable<List<DisplayableItem>> {

        return useCase.execute()
                .mapToList { it.toTabDisplayableItem(resources) }
                .defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.RECENT_ALBUMS)
    internal fun provideLastPlayedAlbumData(
            useCase: GetLastPlayedAlbumsUseCase): Observable<List<DisplayableItem>> {

        return useCase.execute()
                .mapToList { it.toTabLastPlayedDisplayableItem() }
                .defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.RECENT_ARTISTS)
    internal fun provideLastPlayedArtistData(
            resources: Resources,
            useCase: GetLastPlayedArtistsUseCase) : Observable<List<DisplayableItem>> {

        return useCase.execute()
                .mapToList { it.toTabLastPlayedDisplayableItem(resources) }
                .defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.NEW_ALBUMS)
    internal fun provideNewAlbumsData(
            useCase: GetRecentlyAddedAlbumsUseCase): Observable<List<DisplayableItem>> {

        return useCase.execute()
                .mapToList { it.toTabLastPlayedDisplayableItem() }
                .defer()
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.NEW_ARTISTS)
    internal fun provideNewArtistsData(
            resources: Resources,
            useCase: GetRecentlyAddedArtistsUseCase): Observable<List<DisplayableItem>> {

        return useCase.execute()
                .mapToList { it.toTabLastPlayedDisplayableItem(resources) }
                .defer()
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

private fun Playlist.toAutoPlaylist(): DisplayableItem {

    return DisplayableItem(
            R.layout.item_tab_auto_playlist,
            MediaId.playlistId(id),
            title,
            "",
            this.image
    )
}

private fun Playlist.toTabDisplayableItem(resources: Resources): DisplayableItem {

    val size = DisplayableItem.handleSongListSize(resources, size)

    return DisplayableItem(
            R.layout.item_tab_album,
            MediaId.playlistId(id),
            title,
            size,
            this.image
    )
}

private fun Song.toTabDisplayableItem(): DisplayableItem {
    val artist = DisplayableItem.adjustArtist(this.artist)
    val album = DisplayableItem.adjustAlbum(this.album)

    return DisplayableItem(
            R.layout.item_tab_song,
            MediaId.songId(this.id),
            title,
            "$artist${TextUtils.MIDDLE_DOT_SPACED}$album",
            image,
            true
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

private fun Artist.toTabLastPlayedDisplayableItem(resources: Resources): DisplayableItem {
    val songs = DisplayableItem.handleSongListSize(resources, songs)
    var albums = DisplayableItem.handleAlbumListSize(resources, albums)
    if (albums.isNotBlank()) albums+= TextUtils.MIDDLE_DOT_SPACED

    return DisplayableItem(
            R.layout.item_tab_artist_last_played,
            MediaId.artistId(id),
            name,
            albums + songs,
            this.image
    )
}