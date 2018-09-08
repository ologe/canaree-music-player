package dev.olog.msc.presentation.search.di

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.content.Context
import dagger.Module
import dagger.Provides
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.entity.SearchResult
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.all.GetAllAlbumsUseCase
import dev.olog.msc.domain.interactor.all.GetAllArtistsUseCase
import dev.olog.msc.domain.interactor.all.GetAllSongsUseCase
import dev.olog.msc.domain.interactor.search.GetAllRecentSearchesUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.presentation.search.SearchFragmentHeaders
import dev.olog.msc.presentation.search.SearchFragmentType
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.RecentSearchesTypes
import dev.olog.msc.utils.k.extension.asLiveData
import dev.olog.msc.utils.k.extension.mapToList
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers

@Module
class SearchFragmentViewModelModule {

    @Provides
    @PerFragment
    internal fun provideQueryLiveData(): MutableLiveData<String> = MutableLiveData()

    @Provides
    internal fun provideSearchData(
            @ApplicationContext context: Context,
            getAllArtistsUseCase: GetAllArtistsUseCase,
            getAllAlbumsUseCase: GetAllAlbumsUseCase,
            getAllSongsUseCase: GetAllSongsUseCase,
            getAllRecentSearchesUseCase: GetAllRecentSearchesUseCase,
            searchHeaders: SearchFragmentHeaders,
            queryLiveData: MutableLiveData<String>)
            : LiveData<Pair<MutableMap<SearchFragmentType, MutableList<DisplayableItem>>, String>> {

        return Transformations.switchMap(queryLiveData) { input ->

            if (input.isBlank()) {
                provideRecents(context, getAllRecentSearchesUseCase, searchHeaders)
                        .map {
                            mutableMapOf(
                                    SearchFragmentType.RECENT to it.toMutableList(),
                                    SearchFragmentType.ARTISTS to mutableListOf(),
                                    SearchFragmentType.ALBUMS to mutableListOf(),
                                    SearchFragmentType.SONGS to mutableListOf()
                            )
                        }
                        .map { Pair(it, input) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .asLiveData()
            } else {
                getAllSongsUseCase.execute()
                        .flatMap {
                            Observables.combineLatest(
                                    provideSearchByArtist(getAllArtistsUseCase, input),
                                    provideSearchByAlbum(getAllAlbumsUseCase, input),
                                    provideSearchBySong(getAllSongsUseCase, input)
                            ) { artists, albums, songs ->
                                mutableMapOf(
                                        SearchFragmentType.RECENT to mutableListOf(),
                                        SearchFragmentType.ARTISTS to artists.toMutableList(),
                                        SearchFragmentType.ALBUMS to albums.toMutableList(),
                                        SearchFragmentType.SONGS to songs.toMutableList()
                                )
                            }
                        }
                        .map { it to input }
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .asLiveData()
            }
        }
    }

    private fun provideSearchBySong(
            getAllSongsUseCase: GetAllSongsUseCase,
            query: String): Observable<MutableList<DisplayableItem>> {

        return getAllSongsUseCase.execute()
                .flatMapSingle { songs -> songs.toFlowable()
                        .filter { it.title.contains(query, true)  ||
                                it.artist.contains(query, true) ||
                                it.album.contains(query, true)
                        }.map { it.toSearchDisplayableItem() }
                        .toList()
                }
    }

    private fun provideSearchByAlbum(
            getAllAlbumsUseCase: GetAllAlbumsUseCase,
            query: String): Observable<MutableList<DisplayableItem>> {

        return getAllAlbumsUseCase.execute()
                .flatMapSingle { albums -> albums.toFlowable()
                        .filter { it.title.contains(query, true)  ||
                                it.artist.contains(query, true)
                        }.map { it.toSearchDisplayableItem() }
                        .toList()
                }
    }

    private fun provideSearchByArtist(
            getAllArtistsUseCase: GetAllArtistsUseCase,
            query: String): Observable<MutableList<DisplayableItem>> {

        return getAllArtistsUseCase.execute()
                .flatMapSingle { artists -> artists.toFlowable()
                        .filter { it.name.contains(query, true) }
                        .map { it.toSearchDisplayableItem() }
                        .toList()
                }
    }

    private fun provideRecents(
            context: Context,
            getAllRecentSearchesUseCase: GetAllRecentSearchesUseCase,
            searchHeaders: SearchFragmentHeaders): Observable<MutableList<DisplayableItem>> {

        return getAllRecentSearchesUseCase.execute()
                .mapToList { it.toSearchDisplayableItem(context) }
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
            DisplayableItem.adjustArtist(artist),
            image,
            true
    )
}

private fun Album.toSearchDisplayableItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_search_album,
            MediaId.albumId(id),
            title,
            DisplayableItem.adjustArtist(artist),
            image
    )
}

private fun Artist.toSearchDisplayableItem(): DisplayableItem {
    return DisplayableItem(
            R.layout.item_search_artist,
            MediaId.artistId(id),
            name,
            null,
            image
    )
}

private fun SearchResult.toSearchDisplayableItem(context: Context) : DisplayableItem{
    val subtitle = when (this.itemType) {
        RecentSearchesTypes.SONG -> context.getString(R.string.search_type_track)
        RecentSearchesTypes.ALBUM -> context.getString(R.string.search_type_album)
        RecentSearchesTypes.ARTIST -> context.getString(R.string.search_type_artist)
        else -> throw IllegalArgumentException("invalid item type $itemType")
    }

    val isPlayable = this.itemType == RecentSearchesTypes.SONG

    val layout = when (this.itemType){
        RecentSearchesTypes.ARTIST -> R.layout.item_search_recent_artist
        RecentSearchesTypes.ALBUM -> R.layout.item_search_recent_album
        else -> R.layout.item_search_recent
    }

    return DisplayableItem(
            layout,
            this.mediaId,
            this.title,
            subtitle,
            this.image,
            isPlayable
    )
}