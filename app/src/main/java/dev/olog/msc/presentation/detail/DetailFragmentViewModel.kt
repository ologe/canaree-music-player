package dev.olog.msc.presentation.detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import dagger.Lazy
import dev.olog.msc.domain.entity.SortArranging
import dev.olog.msc.domain.entity.SortType
import dev.olog.msc.domain.interactor.MoveItemInPlaylistUseCase
import dev.olog.msc.domain.interactor.RemoveFromPlaylistUseCase
import dev.olog.msc.domain.interactor.detail.GetDetailTabsVisibilityUseCase
import dev.olog.msc.domain.interactor.detail.item.GetArtistFromAlbumUseCase
import dev.olog.msc.domain.interactor.detail.sorting.*
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.k.extension.asLiveData
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.toFlowable

class DetailFragmentViewModel(
        private val mediaId: MediaId,
        item: Map<MediaIdCategory, @JvmSuppressWildcards Observable<DisplayableItem>>,
        albums: Map<MediaIdCategory, @JvmSuppressWildcards Observable<List<DisplayableItem>>>,
        data: Map<String, @JvmSuppressWildcards Observable<List<DisplayableItem>>>,
        private val headers: DetailFragmentHeaders,
        private val getArtistFromAlbumUseCase: GetArtistFromAlbumUseCase,
        private val setSortOrderUseCase: SetSortOrderUseCase,
        private val observeSortOrderUseCase: GetSortOrderUseCase,
        private val setSortArrangingUseCase: SetSortArrangingUseCase,
        private val getSortArrangingUseCase: GetSortArrangingUseCase,
        private val moveItemInPlaylistUseCase: Lazy<MoveItemInPlaylistUseCase>,
        getVisibleTabsUseCase : GetDetailTabsVisibilityUseCase,
        val getDetailSortDataUseCase: GetDetailSortDataUseCase,
        private val removeFromPlaylistUseCase: RemoveFromPlaylistUseCase

) : ViewModel() {

    companion object {
        const val RECENTLY_ADDED = "RECENTLY_ADDED"
        const val MOST_PLAYED = "MOST_PLAYED"
        const val RELATED_ARTISTS = "RELATED_ARTISTS"
        const val SONGS = "SONGS"
    }

    private val currentCategory = mediaId.category

    val itemLiveData: LiveData<DisplayableItem> = item[currentCategory]!!.asLiveData()

    fun artistMediaId(mediaId: MediaId) : Single<MediaId> {
        return if (mediaId.isAlbum){
            getArtistFromAlbumUseCase.execute(mediaId)
                    .firstOrError()
                    .map { MediaId.artistId(it.id) }
        } else {
            Single.error(Throwable("not an album"))
        }

    }

    val mostPlayedFlowable: LiveData<List<DisplayableItem>> = data[MOST_PLAYED]!!
            .asLiveData()

    val recentlyAddedFlowable: LiveData<List<DisplayableItem>> = data[RECENTLY_ADDED]!!
            .flatMapSingle { it.toFlowable().take(10).toList() }
            .asLiveData()

    val data : LiveData<MutableMap<DetailFragmentDataType, MutableList<DisplayableItem>>> = Observables.combineLatest(
            item[currentCategory]!!.distinctUntilChanged(),
            data[MOST_PLAYED]!!.distinctUntilChanged(),
            data[RECENTLY_ADDED]!!.distinctUntilChanged(),
            albums[currentCategory]!!.distinctUntilChanged(),
            data[RELATED_ARTISTS]!!.distinctUntilChanged(),
            data[SONGS]!!.distinctUntilChanged(),
            getVisibleTabsUseCase.execute(),
            { item, mostPlayed, recent, albums, artists, songs, visibility ->

                mutableMapOf(
                        DetailFragmentDataType.HEADER to handleItemHeader(item),
                        DetailFragmentDataType.MOST_PLAYED to handleMostPlayedHeader(mostPlayed.toMutableList(), visibility[0]),
                        DetailFragmentDataType.RECENT to handleRecentlyAddedHeader(recent.toMutableList(), visibility[1]),
                        DetailFragmentDataType.SONGS to handleSongsHeader(songs.toMutableList()),
                        DetailFragmentDataType.ARTISTS_IN to handleArtistsInHeader(artists.toMutableList(), visibility[2]),
                        DetailFragmentDataType.ALBUMS to handleAlbumsHeader(albums.toMutableList())
                ) }
    ).asLiveData()

    private fun handleItemHeader(item: DisplayableItem): MutableList<DisplayableItem>{
        return mutableListOf(item)
    }

    private fun handleMostPlayedHeader(list: MutableList<DisplayableItem>, isEnabled: Boolean) : MutableList<DisplayableItem>{
        if (list.isNotEmpty() && isEnabled){
            list.clear()
            list.addAll(0, headers.mostPlayed)
        } else {
            list.clear()
        }
        return list
    }

    private fun handleRecentlyAddedHeader(list: MutableList<DisplayableItem>, isEnabled: Boolean) : MutableList<DisplayableItem>{
        if (list.isNotEmpty() && isEnabled){
            if (list.size > 10){
                list.clear()
                list.addAll(0, headers.recentWithSeeAll)
            } else {
                list.clear()
                list.addAll(0, headers.recent)
            }
        } else {
            list.clear()
        }
        return list
    }

    private fun handleAlbumsHeader(list: MutableList<DisplayableItem>) : MutableList<DisplayableItem>{
        val albumsList = list.take(4).toMutableList()
        if (albumsList.isNotEmpty()){
            if (list.size > 4){
                albumsList.add(0, headers.albumsWithSeeAll)
            } else {
                albumsList.add(0, headers.albums)
            }
        }

        return albumsList
    }

    private fun handleArtistsInHeader(list: MutableList<DisplayableItem>, isEnabled: Boolean) : MutableList<DisplayableItem>{
        if (list.isNotEmpty()){
            val (_, _, title) = list[0]
            if (title == "" || !isEnabled){
                list.clear()
            }
        }
        return list
    }

    private fun handleSongsHeader(list: MutableList<DisplayableItem>) : MutableList<DisplayableItem>{
        if (list.isNotEmpty()) {
            list.addAll(0, headers.songs)
        } else {
            list.add(headers.no_songs)
        }
        return list
    }

    fun updateSortType(sortType: SortType): Completable {
        return setSortOrderUseCase.execute(SetSortOrderRequestModel(
                mediaId, sortType))
    }

    fun toggleSortArranging(): Completable {
        return setSortArrangingUseCase.execute()
    }

    fun observeSortOrder(): Observable<SortType> {
        return observeSortOrderUseCase.execute(mediaId)
    }

    fun getSortArranging(): Observable<SortArranging> {
        return getSortArrangingUseCase.execute()
    }

    fun moveItemInPlaylist(from: Int, to: Int){
        if (!mediaId.isPlaylist){
            throw IllegalStateException("not a playlist")
        }
        val playlistId = mediaId.categoryValue.toLong()
        moveItemInPlaylistUseCase.get().execute(playlistId, from, to)
    }

    fun removeFromPlaylist(idInPlaylist: Long): Completable {
        if (!mediaId.isPlaylist){
            throw IllegalStateException("not a playlist")
        }
        return removeFromPlaylistUseCase.execute(mediaId.categoryValue.toLong() to idInPlaylist)
    }

}