package dev.olog.presentation.fragment_search.di

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.content.Context
import android.text.TextUtils
import dagger.Module
import dagger.Provides
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import dev.olog.domain.entity.SearchResult
import dev.olog.domain.entity.Song
import dev.olog.domain.interactor.search.GetAllRecentSearchesUseCase
import dev.olog.domain.interactor.tab.GetAllAlbumsUseCase
import dev.olog.domain.interactor.tab.GetAllArtistsUseCase
import dev.olog.domain.interactor.tab.GetAllSongsUseCase
import dev.olog.presentation.R
import dev.olog.presentation.fragment_search.SearchFragmentHeaders
import dev.olog.presentation.fragment_search.SearchFragmentType
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

@Module
class SearchFragmentViewModelModule {

    @Provides
    internal fun provideQueryLiveData(): MutableLiveData<String> {
        return MutableLiveData()
    }

    @Provides
    fun provideSearchData(
            getAllArtistsUseCase: GetAllArtistsUseCase,
            getAllAlbumsUseCase: GetAllAlbumsUseCase,
            getAllSongsUseCase: GetAllSongsUseCase,
            queryLiveData: MutableLiveData<String>,
            recent: Flowable<MutableList<DisplayableItem>>)
            : LiveData<Pair<MutableMap<SearchFragmentType, MutableList<DisplayableItem>>, String>>{

        return Transformations.switchMap(queryLiveData, { input ->

            if (TextUtils.isEmpty(input)){
                recent.map { mutableMapOf(
                        SearchFragmentType.RECENT to it,
                        SearchFragmentType.ARTISTS to mutableListOf(),
                        SearchFragmentType.ALBUMS to mutableListOf(),
                        SearchFragmentType.SONGS to mutableListOf())
                }.map { Pair(it, input) }
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .asLiveData()
            } else {
                Flowables.zip(
                        provideSearchByArtist(getAllArtistsUseCase, input),
                        provideSearchByAlbum(getAllAlbumsUseCase, input),
                        provideSearchBySong(getAllSongsUseCase, input),
                        { artists, albums, songs -> mutableMapOf(
                                SearchFragmentType.RECENT to mutableListOf(),
                                SearchFragmentType.ARTISTS to artists,
                                SearchFragmentType.ALBUMS to albums,
                                SearchFragmentType.SONGS to songs)
                        })
                        .map { it.to(input) }
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .asLiveData()
            }
        })
    }

    private fun provideSearchBySong(
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

    private fun provideSearchByAlbum(
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

    private fun provideSearchByArtist(
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
    fun provideRecentSearchAsLiveDataFlowable(
            @ApplicationContext context: Context,
            getAllRecentSearchesUseCase: GetAllRecentSearchesUseCase,
            searchHeaders: SearchFragmentHeaders): Flowable<MutableList<DisplayableItem>> {

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