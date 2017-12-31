package dev.olog.presentation.fragment_search.di

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.content.Context
import dagger.Module
import dagger.Provides
import dev.olog.domain.entity.*
import dev.olog.domain.interactor.GetSmallPlayType
import dev.olog.domain.interactor.search.GetAllRecentSearchesUseCase
import dev.olog.domain.interactor.tab.GetAllAlbumsUseCase
import dev.olog.domain.interactor.tab.GetAllArtistsUseCase
import dev.olog.domain.interactor.tab.GetAllSongsUseCase
import dev.olog.presentation.R
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.fragment_search.SearchFragmentHeaders
import dev.olog.presentation.fragment_search.SearchFragmentType
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.shared.ApplicationContext
import dev.olog.shared.MediaId
import dev.olog.shared.RecentSearchesTypes
import dev.olog.shared.groupMap
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
            queryLiveData: MutableLiveData<String>,
            getSmallPlayType: GetSmallPlayType)
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
                        provideSearchByArtist(getAllArtistsUseCase, input, getSmallPlayType),
                        provideSearchByAlbum(getAllAlbumsUseCase, input, getSmallPlayType),
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
            query: String,
            getSmallPlayType: GetSmallPlayType): Flowable<MutableList<DisplayableItem>> {

        return Flowables.combineLatest(
                getAllAlbumsUseCase.execute(), getSmallPlayType.execute(), { data, smallPlayType ->
            data.filter { it.title.contains(query, true)  || it.artist.contains(query, true) }
                    .map { it.toSearchDisplayableItem(smallPlayType) }
                    .toMutableList()

        })
    }

    private fun provideSearchByArtist(
            getAllArtistsUseCase: GetAllArtistsUseCase,
            query: String,
            getSmallPlayType: GetSmallPlayType): Flowable<MutableList<DisplayableItem>> {

        return Flowables.combineLatest(
                getAllArtistsUseCase.execute(), getSmallPlayType.execute(), { data, smallPlayType ->
            data.filter { it.name.contains(query, true) }
                    .map { it.toSearchDisplayableItem(smallPlayType) }
                    .toMutableList()

        })
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

private fun Album.toSearchDisplayableItem(smallPlayType: SmallPlayType): DisplayableItem {
    return DisplayableItem(
            R.layout.item_search_album,
            MediaId.albumId(id),
            title,
            artist,
            image,
            smallPlayType = smallPlayType
    )
}

private fun Artist.toSearchDisplayableItem(smallPlayType: SmallPlayType): DisplayableItem {
    return DisplayableItem(
            R.layout.item_search_album,
            MediaId.artistId(id),
            name,
            null,
            image,
            smallPlayType = smallPlayType
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