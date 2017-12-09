package dev.olog.presentation.fragment_search.di

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.content.Context
import android.text.TextUtils
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import dev.olog.domain.entity.SearchResult
import dev.olog.domain.entity.Song
import dev.olog.domain.interactor.search.GetAllRecentSearchesUseCase
import dev.olog.domain.interactor.tab.GetAllAlbumsUseCase
import dev.olog.domain.interactor.tab.GetAllArtistsUseCase
import dev.olog.domain.interactor.tab.GetAllSongsUseCase
import dev.olog.presentation.R
import dev.olog.presentation.fragment_search.SearchHeaders
import dev.olog.presentation.fragment_search.SearchType
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.presentation.utils.rx.groupMap
import dev.olog.shared.ApplicationContext
import dev.olog.shared.MediaIdHelper
import dev.olog.shared.RecentSearchesTypes
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers

private const val SONGS = "SONGS"
private const val ALBUMS = "ALBUMS"
private const val ARTISTS = "ARTISTS"
private const val RECENT = "RECENT"

private const val INSERT_SONG = "INSERT_SONG"
private const val INSERT_ALBUM = "INSERT_ALBUM"
private const val INSERT_ARTIST = "INSERT_ARTIST"

@Module
class SearchFragmentViewModelModule {

    @Provides
    internal fun provideQueryLiveData(): MutableLiveData<String> {
        return MutableLiveData()
    }

    @Provides
    fun provideSearchData(
            queryLiveData: MutableLiveData<String>,
            originalData: Map<String, @JvmSuppressWildcards Flowable<MutableList<DisplayableItem>>>)
            : LiveData<Pair<MutableMap<SearchType, MutableList<DisplayableItem>>, String>>{

        return Transformations.switchMap(queryLiveData, { input ->

            if (TextUtils.isEmpty(input)){
                originalData[RECENT]!!.map { mutableMapOf(
                        SearchType.RECENT to it,
                        SearchType.ARTISTS to mutableListOf(),
                        SearchType.ALBUMS to mutableListOf(),
                        SearchType.SONGS to mutableListOf())
                }.map { Pair(it, input) }
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .asLiveData()
            } else {
                Flowables.zip(
                        originalData[ARTISTS]!!,
                        originalData[ALBUMS]!!,
                        originalData[SONGS]!!,
                        { artists, albums, songs -> mutableMapOf(
                                SearchType.RECENT to mutableListOf(),
                                SearchType.ARTISTS to artists,
                                SearchType.ALBUMS to albums,
                                SearchType.SONGS to songs)
                        })
                        .map { it.to(input) }
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .asLiveData()
            }
        })
    }

    @Provides
    @IntoMap
    @StringKey(SONGS)
    fun provideSearchBySong(
            getAllSongsUseCase: GetAllSongsUseCase,
            query: String): Flowable<MutableList<DisplayableItem>> {

        return getAllSongsUseCase.execute()
                .map { if (TextUtils.isEmpty(query)) listOf() else it }
                .flatMapSingle { it.toFlowable()
                        .filter { it.title.contains(query, true)  ||
                                it.artist.contains(query, true) ||
                                it.album.contains(query, true)
                        }.map { it.toSearchDisplayableItem() }
                        .toList()
                }
    }

    @Provides
    @IntoMap
    @StringKey(ALBUMS)
    fun provideSearchByAlbum(
            getAllAlbumsUseCase: GetAllAlbumsUseCase,
            query: String): Flowable<MutableList<DisplayableItem>> {

        return getAllAlbumsUseCase.execute()
                .map { if (TextUtils.isEmpty(query)) listOf() else it }
                .flatMapSingle { it.toFlowable()
                        .filter { it.title.contains(query, true)  ||
                                it.artist.contains(query, true)
                        }.map { it.toSearchDisplayableItem() }
                        .toList()
                }
    }

    @Provides
    @IntoMap
    @StringKey(ARTISTS)
    fun provideSearchByArtist(
            getAllArtistsUseCase: GetAllArtistsUseCase,
            query: String): Flowable<MutableList<DisplayableItem>> {

        return getAllArtistsUseCase.execute()
                .map { if (TextUtils.isEmpty(query)) listOf() else it }
                .flatMapSingle { it.toFlowable()
                        .filter { it.name.contains(query, true) }
                        .map { it.toSearchDisplayableItem() }
                        .toList()
                }
    }

    @Provides
    @IntoMap
    @StringKey(RECENT)
    fun provideRecentSearchAsLiveDataFlowable(
            @ApplicationContext context: Context,
            getAllRecentSearchesUseCase: GetAllRecentSearchesUseCase,
            searchHeaders: SearchHeaders): Flowable<MutableList<DisplayableItem>> {

        return getAllRecentSearchesUseCase.execute()
                .groupMap { it.toSearchDisplayableItem(context) }
                .map { it.toMutableList() }
                .map {
                    if (it.isNotEmpty()){
                        it.add(DisplayableItem(R.layout.item_search_clear_recent, "clear recent id", ""))
                        it.addAll(0, searchHeaders.recents)
                    }
                    it
                }
    }

}

private fun Song.toSearchDisplayableItem(): DisplayableItem{
    return DisplayableItem(
            R.layout.item_search_song,
            MediaIdHelper.songId(id),
            title,
            "$artist${dev.olog.shared.TextUtils.MIDDLE_DOT_SPACED}$album",
            image,
            true,
            isRemix,
            isExplicit
    )
}

private fun Album.toSearchDisplayableItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_search_album,
            MediaIdHelper.albumId(id),
            title,
            artist,
            image
    )
}

private fun Artist.toSearchDisplayableItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_search_album,
            MediaIdHelper.artistId(id),
            name,
            null,
            image
    )
}

private fun SearchResult.toSearchDisplayableItem(context: Context) : DisplayableItem{
    val subtitle = when (this.itemType) {
        RecentSearchesTypes.SONG -> context.getString(R.string.search_type_song)
        RecentSearchesTypes.ALBUM -> context.getString(R.string.search_type_album)
        RecentSearchesTypes.ARTIST -> context.getString(R.string.search_type_artist)
        else -> throw IllegalArgumentException("invalid item type $itemType")
    }

    val isPlayable = this.itemType == RecentSearchesTypes.SONG

    return DisplayableItem(
            R.layout.item_search_recent,
            this.mediaId,
            this.title,
            subtitle,
            this.image,
            isPlayable
    )
}