package dev.olog.presentation.fragment_tab.di

import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntKey
import dagger.multibindings.IntoMap
import dev.olog.domain.entity.*
import dev.olog.domain.interactor.GetSmallPlayType
import dev.olog.domain.interactor.tab.*
import dev.olog.presentation.R
import dev.olog.presentation.activity_main.TabViewPagerAdapter
import dev.olog.presentation.fragment_tab.TabFragmentHeaders
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.MediaId
import dev.olog.shared_android.TextUtils
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.rxkotlin.withLatestFrom

@Module
class TabFragmentViewModelModule {

    companion object {
        const val LAST_PLAYED_ARTIST = 50
        const val LAST_PLAYED_ALBUM = 60
    }

    @Provides
    @IntoMap
    @IntKey(TabViewPagerAdapter.FOLDER)
    internal fun provideFolderData(
            resources: Resources,
            useCase: GetAllFoldersUseCase,
            getSmallPlayType: GetSmallPlayType): Flowable<List<DisplayableItem>> {

        return Flowables.combineLatest(useCase.execute(), getSmallPlayType.execute(), { data, smallPlayType ->
            data.map { it.toTabDisplayableItem(resources, smallPlayType) }
        })
    }

    @Provides
    @IntoMap
    @IntKey(TabViewPagerAdapter.PLAYLIST)
    internal fun providePlaylistData(
            resources: Resources,
            useCase: GetAllPlaylistsUseCase,
            autoPlaylistUseCase: GetAllAutoPlaylistUseCase,
            headers: TabFragmentHeaders,
            getSmallPlayType: GetSmallPlayType): Flowable<List<DisplayableItem>> {

        val playlistObs = Flowables.combineLatest(useCase.execute(), getSmallPlayType.execute(), { data, smallPlayType ->
            val result = data.map { it.toTabDisplayableItem(resources, smallPlayType) }.toMutableList()
            result.add(0, headers.allPlaylistHeader)
            result
        })

        val autoPlaylistObs = Flowables.combineLatest(autoPlaylistUseCase.execute(), getSmallPlayType.execute(), { data, smallPlayType ->
            val result = data.map { it.toTabDisplayableItem(resources, smallPlayType) }.toMutableList()
            result.add(0, headers.autoPlaylistHeader)
            result
        })

        return playlistObs.withLatestFrom(autoPlaylistObs, { playlist, autoPlaylist ->
            val result = autoPlaylist.toMutableList()
            result.addAll(if (playlist.size == 1) listOf() else playlist)
            result
        })
    }

    @Provides
    @IntoMap
    @IntKey(TabViewPagerAdapter.SONG)
    internal fun provideSongData(
            useCase: GetAllSongsUseCase,
            headers: TabFragmentHeaders): Flowable<List<DisplayableItem>> {

        return useCase.execute().flatMapSingle{ it.toFlowable()
                .map { it.toTabDisplayableItem() }
                .startWith(headers.shuffleHeader)
                .toList()
        }
    }

    @Provides
    @IntoMap
    @IntKey(TabViewPagerAdapter.ALBUM)
    internal fun provideAlbumData(
            useCase: GetAllAlbumsUseCase,
            lastPlayedAlbumsUseCase: GetLastPlayedAlbumsUseCase,
            headers: TabFragmentHeaders,
            getSmallPlayType: GetSmallPlayType): Flowable<List<DisplayableItem>> {

        val allObs = Flowables.combineLatest(useCase.execute(), getSmallPlayType.execute(), { data, smallPlayType ->
            data.map { it.toTabDisplayableItem(smallPlayType) }.toMutableList()
        })

        val lastPlayedObs = Flowables.combineLatest(lastPlayedAlbumsUseCase.execute(), getSmallPlayType.execute(), { data, smallPlayType ->
            if (data.isEmpty()){
                listOf()
            } else {
                headers.albumHeaders
            }
        }).distinctUntilChanged()

        return Flowables.combineLatest(allObs, lastPlayedObs, { all, recent ->
            all.addAll(0, recent)
            all
        })
    }

    @Provides
    @IntoMap
    @IntKey(TabViewPagerAdapter.ARTIST)
    internal fun provideArtistData(
            resources: Resources,
            useCase: GetAllArtistsUseCase,
            lastPlayedArtistsUseCase: GetLastPlayedArtistsUseCase,
            headers: TabFragmentHeaders,
            getSmallPlayType: GetSmallPlayType) : Flowable<List<DisplayableItem>> {

        val allObs = Flowables.combineLatest(useCase.execute(), getSmallPlayType.execute(), { data, smallPlayType ->
            data.map { it.toTabDisplayableItem(resources, smallPlayType) }.toMutableList()
        })

        val lastPlayedObs = Flowables.combineLatest(lastPlayedArtistsUseCase.execute(), getSmallPlayType.execute(), { data, smallPlayType ->
            if (data.isEmpty()){
                listOf()
            } else headers.artistHeaders
        }).distinctUntilChanged()

        return Flowables.combineLatest(allObs, lastPlayedObs, { all, recent ->
            all.addAll(0, recent)
            all
        })
    }

    @Provides
    @IntoMap
    @IntKey(TabViewPagerAdapter.GENRE)
    internal fun provideGenreData(
            resources: Resources,
            useCase: GetAllGenresUseCase,
            getSmallPlayType: GetSmallPlayType): Flowable<List<DisplayableItem>> {

        return Flowables.combineLatest(useCase.execute(), getSmallPlayType.execute(), { data, smallPlayType ->
            data.map { it.toTabDisplayableItem(resources, smallPlayType) }
        })
    }

    @Provides
    @IntoMap
    @IntKey(LAST_PLAYED_ALBUM)
    internal fun provideLastPlayedAlbumData(
            useCase: GetLastPlayedAlbumsUseCase,
            getSmallPlayType: GetSmallPlayType): Flowable<List<DisplayableItem>> {

        return Flowables.combineLatest(useCase.execute(), getSmallPlayType.execute(), { data, smallPlayType ->
            data.map { it.toTabLastPlayedDisplayableItem(smallPlayType) }
        })
    }

    @Provides
    @IntoMap
    @IntKey(LAST_PLAYED_ARTIST)
    internal fun provideLastPlayedArtistData(
            useCase: GetLastPlayedArtistsUseCase,
            getSmallPlayType: GetSmallPlayType) : Flowable<List<DisplayableItem>> {

        return Flowables.combineLatest(useCase.execute(), getSmallPlayType.execute(), { data, smallPlayType ->
            data.map { it.toTabLastPlayedDisplayableItem(smallPlayType) }
        })
    }

}

private fun Folder.toTabDisplayableItem(resources: Resources, smallPlayType: SmallPlayType): DisplayableItem{
    return DisplayableItem(
            R.layout.item_tab_album,
            MediaId.folderId(path),
            title.capitalize(),
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
            this.image,
            smallPlayType = smallPlayType
    )
}

private fun Playlist.toTabDisplayableItem(resources: Resources, smallPlayType: SmallPlayType): DisplayableItem{
    val listSize = if (this.size == -1){ "" } else {
        resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase()
    }

    return DisplayableItem(
            R.layout.item_tab_album,
            MediaId.playlistId(id),
            title.capitalize(),
            listSize,
            this.image,
            smallPlayType = smallPlayType
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

private fun Album.toTabDisplayableItem(smallPlayType: SmallPlayType): DisplayableItem{
    return DisplayableItem(
            R.layout.item_tab_album,
            MediaId.albumId(id),
            title,
            artist,
            image,
            smallPlayType = smallPlayType
    )
}

private fun Artist.toTabDisplayableItem(resources: Resources, smallPlayType: SmallPlayType): DisplayableItem{
    val songs = resources.getQuantityString(R.plurals.song_count, this.songs, this.songs)
    val albums = if (this.albums == 0) "" else {
        "${resources.getQuantityString(R.plurals.album_count, this.albums, this.albums)}${TextUtils.MIDDLE_DOT_SPACED}"
    }

    return DisplayableItem(
            R.layout.item_tab_album,
            MediaId.artistId(id),
            name,
            "$albums$songs".toLowerCase(),
            this.image,
            smallPlayType = smallPlayType
    )
}

private fun Genre.toTabDisplayableItem(resources: Resources, smallPlayType: SmallPlayType): DisplayableItem{
    return DisplayableItem(
            R.layout.item_tab_album,
            MediaId.genreId(id),
            name,
            resources.getQuantityString(R.plurals.song_count, this.size, this.size).toLowerCase(),
            this.image,
            smallPlayType = smallPlayType
    )
}

private fun Album.toTabLastPlayedDisplayableItem(smallPlayType: SmallPlayType): DisplayableItem {
    return DisplayableItem(
            R.layout.item_tab_album_last_played,
            MediaId.albumId(id),
            title,
            artist,
            image,
            smallPlayType = smallPlayType
    )
}

private fun Artist.toTabLastPlayedDisplayableItem(smallPlayType: SmallPlayType): DisplayableItem {
    return DisplayableItem(
            R.layout.item_tab_album_last_played,
            MediaId.artistId(id),
            name,
            null,
            this.image,
            smallPlayType = smallPlayType
    )
}