package dev.olog.presentation.fragment_search

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.text.TextUtils
import dev.olog.domain.interactor.search.*
import dev.olog.domain.interactor.tab.GetAllAlbumsUseCase
import dev.olog.domain.interactor.tab.GetAllArtistsUseCase
import dev.olog.domain.interactor.tab.GetAllSongsUseCase
import dev.olog.presentation.R
import dev.olog.presentation.fragment_search.mapper.toDisplayableItem
import dev.olog.presentation.fragment_search.mapper.toSearchDisplayableItem
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.toDisplayableItem
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers

class SearchFragmentViewModel(
        application: Application,
        private val getAllSongsUseCase: GetAllSongsUseCase,
        private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
        private val getAllArtistsUseCase: GetAllArtistsUseCase,
        private val searchHeaders: SearchHeaders,
        getAllRecentSearchesUseCase: GetAllRecentSearchesUseCase,
        private val insertSearchSongUseCase: InsertRecentSearchSongUseCase,
        private val insertSearchAlbumUseCase: InsertRecentSearchAlbumUseCase,
        private val insertSearchArtistUseCase: InsertRecentSearchArtistUseCase,
        private val deleteRecentSearchSongUseCase: DeleteRecentSearchSongUseCase,
        private val deleteRecentSearchAlbumUseCase: DeleteRecentSearchAlbumUseCase,
        private val deleteRecentSearchArtistUseCase: DeleteRecentSearchArtistUseCase,
        private val clearRecentSearchesUseCase: ClearRecentSearchesUseCase

) : AndroidViewModel(application) {

    private val queryText = MutableLiveData<String>()

    fun setNewQuery(newQuery: String){
        queryText.value = newQuery
    }

    val data : LiveData<Pair<MutableMap<SearchType, MutableList<DisplayableItem>>, String>> = Transformations.switchMap(queryText, { input ->
        if (TextUtils.isEmpty(input)){
            recentsData.map { mutableMapOf(
                    SearchType.RECENT to it,
                    SearchType.ARTISTS to mutableListOf(),
                    SearchType.ALBUMS to mutableListOf(),
                    SearchType.SONGS to mutableListOf()
                    ) }
                    .map { it.to(input) }
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .asLiveData()
        } else {
            Flowables.zip(
                    setupArtistsUseCase(input), setupAlbumsUseCase(input), setupSongsUseCase(input),
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

    fun adjustDataMap(data: MutableMap<SearchType, MutableList<DisplayableItem>>) {
        val albums = data[SearchType.ALBUMS]!!
        if (albums.isNotEmpty()){
            val albumsHeaders = searchHeaders.albumsHeaders(albums.size)
            if (albums[0].mediaId == albumsHeaders[0].mediaId){
                albumsHeaders.forEachIndexed { index, displayableItem ->
                    albums[index] = displayableItem
                }
            } else {
                albums.clear()
                albums.addAll(0, albumsHeaders)
            }

        }

        val artists = data[SearchType.ARTISTS]!!
        if (artists.isNotEmpty()){
            val artistsHeaders = searchHeaders.artistsHeaders(artists.size)
            if (artists[0].mediaId == artistsHeaders[0].mediaId){
                artistsHeaders.forEachIndexed { index, displayableItem ->
                    artists[index] = displayableItem
                }
            } else {
                artists.clear()
                artists.addAll(0, artistsHeaders)
            }
        }

        val songs = data[SearchType.SONGS]!!
        if (songs.isNotEmpty()){
            val songHeaders = searchHeaders.songsHeaders(songs.size)
            if (songs[0].mediaId == songHeaders[0].mediaId){
                songHeaders.forEachIndexed { index, displayableItem ->
                    songs[index] = displayableItem
                }
            } else {
                songs.addAll(0, songHeaders)
            }
        }
    }

    private fun setupSongsUseCase(query: String): Flowable<MutableList<DisplayableItem>> {
        return getAllSongsUseCase.execute()
                .map { if (TextUtils.isEmpty(query)) listOf() else it }
                .flatMapSingle { it.toFlowable()
                        .filter { it.title.contains(query, true)  ||
                                it.artist.contains(query, true) ||
                                it.album.contains(query, true)
                        }.map { it.toDisplayableItem() }
                        .toList()
                }
    }

    private fun setupAlbumsUseCase(query: String): Flowable<MutableList<DisplayableItem>> {
        return getAllAlbumsUseCase.execute()
                .map { if (TextUtils.isEmpty(query)) listOf() else it }
                .flatMapSingle { it.toFlowable()
                        .filter { it.title.contains(query, true)  ||
                                it.artist.contains(query, true)
                        }.map { it.toSearchDisplayableItem() }
                        .toList()
                }
    }

    private fun setupArtistsUseCase(query: String): Flowable<MutableList<DisplayableItem>> {
        return getAllArtistsUseCase.execute()
                .map { if (TextUtils.isEmpty(query)) listOf() else it }
                .flatMapSingle { it.toFlowable()
                        .filter { it.name.contains(query, true) }
                        .map { it.toSearchDisplayableItem() }
                        .toList()
                }
    }

    private val recentsData : Flowable<MutableList<DisplayableItem>> = getAllRecentSearchesUseCase.execute()
            .flatMapSingle { it.toFlowable().map { it.toDisplayableItem(application) }.toList() }
            .map {
                if (it.isNotEmpty()){
                    it.add(DisplayableItem(R.layout.item_recent_search_footer, "clear recent id", ""))
                    it.addAll(0, searchHeaders.recents)
                }
                it
            }

    fun insertSongToRecents(mediaId: String): Completable {
        val songId = MediaIdHelper.extractLeaf(mediaId).toLong()
        return insertSearchSongUseCase.execute(songId)
    }

    fun insertAlbumToRecents(mediaId: String): Completable {
        val albumId = MediaIdHelper.extractCategoryValue(mediaId).toLong()
        return insertSearchAlbumUseCase.execute(albumId)
    }

    fun insertArtistToRecents(mediaId: String): Completable {
        val artistId = MediaIdHelper.extractCategoryValue(mediaId).toLong()
        return insertSearchArtistUseCase.execute(artistId)
    }

    fun deleteFromRecents(mediaId: String): Completable{
        val category = MediaIdHelper.extractCategory(mediaId)
        return when (category) {
            MediaIdHelper.MEDIA_ID_BY_ALBUM -> {
                val albumId = MediaIdHelper.extractCategoryValue(mediaId).toLong()
                deleteRecentSearchAlbumUseCase.execute(albumId)
            }
            MediaIdHelper.MEDIA_ID_BY_ARTIST -> {
                val artistId = MediaIdHelper.extractCategoryValue(mediaId).toLong()
                deleteRecentSearchArtistUseCase.execute(artistId)
            }
            MediaIdHelper.MEDIA_ID_BY_ALL -> {
                val songId = MediaIdHelper.extractLeaf(mediaId).toLong()
                return deleteRecentSearchSongUseCase.execute(songId)
            }
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }

    fun clearRecentSearches(): Completable {
        return clearRecentSearchesUseCase.execute()
    }

}