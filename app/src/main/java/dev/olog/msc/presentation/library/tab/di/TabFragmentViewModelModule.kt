package dev.olog.msc.presentation.library.tab.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dev.olog.msc.R
import dev.olog.msc.dagger.MediaIdCategoryKey
import dev.olog.msc.domain.entity.*
import dev.olog.msc.domain.interactor.tab.*
import dev.olog.msc.presentation.library.tab.TabFragmentHeaders
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.k.extension.groupMap
import dev.olog.shared_android.TextUtils
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.rxkotlin.withLatestFrom

@Module
class TabFragmentViewModelModule {

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.FOLDER)
    internal fun provideFolderData(
            resources: Resources,
            useCase: GetAllFoldersUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute().groupMap { it.toTabDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.PLAYLIST)
    internal fun providePlaylistData(
            resources: Resources,
            useCase: GetAllPlaylistsUseCase,
            autoPlaylistUseCase: GetAllAutoPlaylistUseCase,
            headers: TabFragmentHeaders): Flowable<List<DisplayableItem>> {

        val playlistObs = useCase.execute().flatMapSingle { it.toFlowable()
                .map { it.toTabDisplayableItem(resources) }
                .startWith(headers.allPlaylistHeader)
                .toList()
        }

        val autoPlaylistObs = autoPlaylistUseCase.execute().flatMapSingle { it.toFlowable()
                .map { it.toTabDisplayableItem(resources) }
                .startWith(headers.autoPlaylistHeader)
                .toList()
        }

        return playlistObs.withLatestFrom(autoPlaylistObs, { playlist, autoPlaylist ->
            val result = autoPlaylist.toMutableList()
            result.addAll(if (playlist.size == 1) listOf() else playlist)
            result
        })
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.SONGS)
    internal fun provideSongData(
            useCase: GetAllSongsUseCase,
            headers: TabFragmentHeaders): Flowable<List<DisplayableItem>> {

        return useCase.execute().flatMapSingle{ it.toFlowable()
                .map { it.toTabDisplayableItem() }
                .startWith(headers.shuffleHeader)
                .toList()
        }.map { if (it.size == 1) listOf() else it }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ALBUM)
    internal fun provideAlbumData(
            useCase: GetAllAlbumsUseCase,
            lastPlayedAlbumsUseCase: GetLastPlayedAlbumsUseCase,
            headers: TabFragmentHeaders): Flowable<List<DisplayableItem>> {

        val allObs = useCase.execute().groupMap { it.toTabDisplayableItem() }
                .map { it.toMutableList() }

        val lastPlayedObs = lastPlayedAlbumsUseCase.execute()
                .groupMap { it.toTabDisplayableItem() }
                .map { if (it.isNotEmpty()) headers.albumHeaders else listOf() }
                .distinctUntilChanged()

        return Flowables.combineLatest(allObs, lastPlayedObs, { all, recent ->
            all.addAll(0, recent)
            all
        })
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.ARTIST)
    internal fun provideArtistData(
            resources: Resources,
            useCase: GetAllArtistsUseCase,
            lastPlayedArtistsUseCase: GetLastPlayedArtistsUseCase,
            headers: TabFragmentHeaders) : Flowable<List<DisplayableItem>> {

        val allObs = useCase.execute()
                .groupMap { it.toTabDisplayableItem(resources) }
                .map { it.toMutableList() }

        val lastPlayedObs = lastPlayedArtistsUseCase.execute()
                .groupMap { it.toTabDisplayableItem(resources) }
                .map { if (it.isNotEmpty()) headers.artistHeaders else listOf() }
                .distinctUntilChanged()

        return Flowables.combineLatest(allObs, lastPlayedObs, { all, recent ->
            all.addAll(0, recent)
            all
        })
    }


    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.GENRE)
    internal fun provideGenreData(
            resources: Resources,
            useCase: GetAllGenresUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute().groupMap { it.toTabDisplayableItem(resources) }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.RECENT_ALBUMS)
    internal fun provideLastPlayedAlbumData(
            useCase: GetLastPlayedAlbumsUseCase): Flowable<List<DisplayableItem>> {

        return useCase.execute().groupMap { it.toTabLastPlayedDisplayableItem() }
    }

    @Provides
    @IntoMap
    @MediaIdCategoryKey(MediaIdCategory.RECENT_ARTISTS)
    internal fun provideLastPlayedArtistData(
            useCase: GetLastPlayedArtistsUseCase) : Flowable<List<DisplayableItem>> {

        return useCase.execute().groupMap { it.toTabLastPlayedDisplayableItem() }
    }

}

private fun Folder.toTabDisplayableItem(resources: Resources): DisplayableItem{
    return DisplayableItem(
            R.layout.item_tab_album,
            MediaId.folderId(path),
            title.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
            this.image
    )
}

private fun Playlist.toTabDisplayableItem(resources: Resources): DisplayableItem{
    val listSize = if (this.size == -1){ "" } else {
        resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase()
    }

    return DisplayableItem(
            R.layout.item_tab_album,
            MediaId.playlistId(id),
            title.capitalize(),
            listSize,
            this.image
    )
}

private fun Song.toTabDisplayableItem(): DisplayableItem{
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
            artist,
            image
    )
}

private fun Artist.toTabDisplayableItem(resources: Resources): DisplayableItem{
    val songs = resources.getQuantityString(R.plurals.song_count, this.songs, this.songs)
    val albums = if (this.albums == 0) "" else {
        "${resources.getQuantityString(R.plurals.album_count, this.albums, this.albums)}${TextUtils.MIDDLE_DOT_SPACED}"
    }

    return DisplayableItem(
            R.layout.item_tab_album,
            MediaId.artistId(id),
            name,
            "$albums$songs".toLowerCase(),
            this.image
    )
}

private fun Genre.toTabDisplayableItem(resources: Resources): DisplayableItem{
    return DisplayableItem(
            R.layout.item_tab_album,
            MediaId.genreId(id),
            name,
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
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
            R.layout.item_tab_album_last_played,
            MediaId.artistId(id),
            name,
            null,
            this.image
    )
}