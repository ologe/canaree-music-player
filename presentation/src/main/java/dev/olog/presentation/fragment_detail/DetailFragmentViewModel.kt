package dev.olog.presentation.fragment_detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import dagger.Lazy
import dev.olog.domain.SortArranging
import dev.olog.domain.entity.SortType
import dev.olog.domain.interactor.MoveItemInPlaylistUseCase
import dev.olog.domain.interactor.detail.item.GetArtistFromAlbumUseCase
import dev.olog.domain.interactor.detail.sorting.*
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.toFlowable

class DetailFragmentViewModel(
        private val mediaId: String,
        item: Map<String, @JvmSuppressWildcards Flowable<DisplayableItem>>,
        data: Map<String, @JvmSuppressWildcards Flowable<List<DisplayableItem>>>,
        private val headers: DetailFragmentHeaders,
        private val getArtistFromAlbumUseCase: GetArtistFromAlbumUseCase,
        private val setSortOrderUseCase: SetSortOrderUseCase,
        private val getSortOrderUseCase: GetSortOrderUseCase,
        private val setSortArrangingUseCase: SetSortArrangingUseCase,
        private val getSortArrangingUseCase: GetSortArrangingUseCase,
        private val moveItemInPlaylistUseCase: Lazy<MoveItemInPlaylistUseCase>

) : ViewModel() {

    companion object {
        const val RECENTLY_ADDED = "RECENTLY_ADDED"
        const val MOST_PLAYED = "MOST_PLAYED"
        const val RELATED_ARTISTS = "RELATED_ARTISTS"
        const val SONGS = "SONGS"
    }

    private val category = MediaIdHelper.extractCategory(mediaId)

    val itemLiveData: LiveData<DisplayableItem> = item[category]!!.asLiveData()

    fun artistMediaId(mediaId: String) : Single<String> {
        val category = MediaIdHelper.extractCategory(mediaId)
        return if (category == MediaIdHelper.MEDIA_ID_BY_ALBUM){
            getArtistFromAlbumUseCase.execute(mediaId)
                    .firstOrError()
                    .map { MediaIdHelper.artistId(it.id) }
        } else {
            Single.error(Throwable("not an album"))
        }

    }

    val mostPlayedFlowable: LiveData<List<DisplayableItem>> = data[MOST_PLAYED]!!
            .asLiveData()

    val recentlyAddedFlowable: LiveData<List<DisplayableItem>> = data[RECENTLY_ADDED]!!
            .flatMapSingle { it.toFlowable().take(10).toList() }
            .asLiveData()

    val data : LiveData<MutableMap<DetailFragmentDataType, MutableList<DisplayableItem>>> = Flowables.combineLatest(
            item[category]!!,
            data[MOST_PLAYED]!!,
            data[RECENTLY_ADDED]!!,
            data[category]!!,
            data[RELATED_ARTISTS]!!,
            data[SONGS]!!,
            { item, mostPlayed, recent, albums, artists, songs ->

        mutableMapOf(
                DetailFragmentDataType.HEADER to mutableListOf(item),
                DetailFragmentDataType.MOST_PLAYED to handleMostPlayedHeader(mostPlayed.toMutableList()),
                DetailFragmentDataType.RECENT to handleRecentlyAddedHeader(recent.toMutableList()),
                DetailFragmentDataType.ALBUMS to handleAlbumsHeader(albums.toMutableList()),
                DetailFragmentDataType.ARTISTS_IN to handleArtistsInHeader(artists.toMutableList()),
                DetailFragmentDataType.SONGS to handleSongsHeader(songs.toMutableList())
        ) }
    ).asLiveData()

    private fun handleMostPlayedHeader(list: MutableList<DisplayableItem>) : MutableList<DisplayableItem>{
        if (list.isNotEmpty()){
            list.clear()
            list.addAll(0, headers.mostPlayed)
        }
        return list
    }

    private fun handleRecentlyAddedHeader(list: MutableList<DisplayableItem>) : MutableList<DisplayableItem>{
        if (list.isNotEmpty()){
            if (list.size > 10){
                list.clear()
                list.addAll(0, headers.recentWithSeeAll)
            } else {
                list.clear()
                list.addAll(0, headers.recent)
            }
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

    private fun handleArtistsInHeader(list: MutableList<DisplayableItem>) : MutableList<DisplayableItem>{
        val (_, _, title) = list[0]
        if (title == ""){
            list.clear()
        }
        return list
    }

    private fun handleSongsHeader(list: MutableList<DisplayableItem>) : MutableList<DisplayableItem>{
        list.addAll(0, headers.songs)
        return list
    }

    fun updateSortType(sortType: SortType): Completable {
        return setSortOrderUseCase.execute(SetSortOrderRequestModel(
                mediaId, sortType))
    }

    fun toggleSortArranging(): Completable {
        return setSortArrangingUseCase.execute()
    }

    fun getSortOrder(): Flowable<SortType> {
        return getSortOrderUseCase.execute(mediaId)
    }

    fun getSortArranging(): Flowable<SortArranging> {
        return getSortArrangingUseCase.execute()
    }

    fun moveItemInPlaylist(from: Int, to: Int){
        val category = MediaIdHelper.extractCategory(mediaId)
        if (category != MediaIdHelper.MEDIA_ID_BY_PLAYLIST){
            throw IllegalArgumentException("not a playlist")
        }
        val playlistId = MediaIdHelper.extractCategoryValue(mediaId).toLong()
        val success = moveItemInPlaylistUseCase.get().execute(playlistId, from, to)
        println(success)
    }

}