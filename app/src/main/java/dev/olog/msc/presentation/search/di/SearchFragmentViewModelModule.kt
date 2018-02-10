package dev.olog.presentation.fragment_search.di

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.content.Context
import dagger.Module
import dagger.Provides
import dev.olog.msc.R
import dev.olog.msc.dagger.ApplicationContext
import dev.olog.msc.dagger.PerFragment
import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.entity.SearchResult
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.search.GetAllRecentSearchesUseCase
import dev.olog.msc.domain.interactor.tab.GetAllAlbumsUseCase
import dev.olog.msc.domain.interactor.tab.GetAllArtistsUseCase
import dev.olog.msc.domain.interactor.tab.GetAllSongsUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.search.SearchFragmentHeaders
import dev.olog.msc.presentation.search.SearchFragmentType
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.RecentSearchesTypes
import dev.olog.msc.utils.k.extension.groupMap
import dev.olog.shared_android.extension.asLiveData
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers

@Module
class SearchFragmentViewModelModule {

    @Provides
    @PerFragment
    internal fun provideQueryLiveData(): MutableLiveData<String> = MutableLiveData()

    @Provides
    fun provideSearchData(
            @ApplicationContext context: Context,
            getAllArtistsUseCase: GetAllArtistsUseCase,
            getAllAlbumsUseCase: GetAllAlbumsUseCase,
            getAllSongsUseCase: GetAllSongsUseCase,
            getAllRecentSearchesUseCase: GetAllRecentSearchesUseCase,
            searchHeaders: SearchFragmentHeaders,
            queryLiveData: MutableLiveData<String>)
            : LiveData<Pair<MutableMap<SearchFragmentType, MutableList<DisplayableItem>>, String>>{

        return Transformations.switchMap(queryLiveData, { input ->

            if (input.isBlank()){
                provideRecents(context, getAllRecentSearchesUseCase, searchHeaders)
                        .map { mutableMapOf(
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
                        .map { it to input }
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
                .flatMapSingle { it.toFlowable()
                        .filter { it.name.contains(query, true) }
                        .map { it.toSearchDisplayableItem() }
                        .toList()
                }
    }

    private fun provideRecents(
            context: Context,
            getAllRecentSearchesUseCase: GetAllRecentSearchesUseCase,
            searchHeaders: SearchFragmentHeaders): Flowable<MutableList<DisplayableItem>> {

        return getAllRecentSearchesUseCase.execute()
                .groupMap { it.toSearchDisplayableItem(context) }
                .map { it.toMutableList() }
                .map {
                    if (it.isNotEmpty()){
                        it.add(DisplayableItem(R.layout.item_search_clear_recent, MediaId.headerId("clear recent"), ""))
                        it.addAll(0, searchHeaders.recents)
                    }
                    it
                }
    }

}

private fun Song.toSearchDisplayableItem(): DisplayableItem{
    return DisplayableItem(
            R.layout.item_search_song,
            MediaId.songId(id),
            title,
            "$artist${dev.olog.shared_android.TextUtils.MIDDLE_DOT_SPACED}$album",
            image,
            true,
            isRemix,
            isExplicit
    )
}

private fun Album.toSearchDisplayableItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_search_album,
            MediaId.albumId(id),
            title,
            artist,
            image
    )
}

private fun Artist.toSearchDisplayableItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_search_album,
            MediaId.artistId(id),
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