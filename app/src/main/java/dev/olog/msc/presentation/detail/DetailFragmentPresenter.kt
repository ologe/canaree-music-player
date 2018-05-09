package dev.olog.msc.presentation.detail

import dev.olog.msc.constants.PlaylistConstants
import dev.olog.msc.domain.interactor.playlist.MoveItemInPlaylistUseCase
import dev.olog.msc.domain.interactor.playlist.RemoveFromPlaylistUseCase
import dev.olog.msc.domain.interactor.item.GetArtistFromAlbumUseCase
import dev.olog.msc.domain.interactor.prefs.TutorialPreferenceUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject

class DetailFragmentPresenter @Inject constructor(
        private val mediaId: MediaId,
        private val getArtistFromAlbumUseCase: GetArtistFromAlbumUseCase,
        private val removeFromPlaylistUseCase: RemoveFromPlaylistUseCase,
        private val headers: DetailFragmentHeaders,
        private val moveItemInPlaylistUseCase: MoveItemInPlaylistUseCase,
        private val tutorialPreferenceUseCase: TutorialPreferenceUseCase

) {

    fun artistMediaId() : Maybe<MediaId> {
        return if (mediaId.isAlbum){
            getArtistFromAlbumUseCase
                    .execute(mediaId)
                    .firstElement()
                    .map { MediaId.artistId(it.id) }
        } else {
            Maybe.empty()
        }
    }

    fun removeFromPlaylist(item: DisplayableItem): Completable {
        mediaId.assertPlaylist()
        val playlistId = mediaId.resolveId
        if (playlistId == PlaylistConstants.FAVORITE_LIST_ID){
            // favorites use songId instead of idInPlaylist
            return removeFromPlaylistUseCase.execute(playlistId to item.mediaId.leaf!!)
        }
        return removeFromPlaylistUseCase.execute(playlistId to item.trackNumber.toLong())
    }

    fun moveInPlaylist(from: Int, to: Int){
        mediaId.assertPlaylist()
        val playlistId = mediaId.resolveId
        moveItemInPlaylistUseCase.execute(playlistId, from, to)
    }

    fun createDataMap(
            item: List<DisplayableItem>,
            mostPlayed: List<DisplayableItem>,
            recent: List<DisplayableItem>,
            albums: List<DisplayableItem>,
            artists: List<DisplayableItem>,
            songs: List<DisplayableItem>,
            visibility: BooleanArray

    ): MutableMap<DetailFragmentDataType, MutableList<DisplayableItem>> {

        return mutableMapOf(
                DetailFragmentDataType.HEADER to item.toMutableList(),
                DetailFragmentDataType.MOST_PLAYED to handleMostPlayedHeader(mostPlayed.toMutableList(), visibility[0]),
                DetailFragmentDataType.RECENT to handleRecentlyAddedHeader(recent.toMutableList(), visibility[1]),
                DetailFragmentDataType.SONGS to handleSongsHeader(songs.toMutableList()),
                DetailFragmentDataType.ARTISTS_IN to handleRelatedArtistsHeader(artists.toMutableList(), visibility[2]),
                DetailFragmentDataType.ALBUMS to handleAlbumsHeader(albums.toMutableList(), item)
        )
    }

    private fun handleMostPlayedHeader(list: MutableList<DisplayableItem>, isEnabled: Boolean) : MutableList<DisplayableItem>{
        if (isEnabled && list.isNotEmpty()){
            list.clear()
            list.addAll(0, headers.mostPlayed)
        } else {
            list.clear()
        }
        return list
    }

    private fun handleRecentlyAddedHeader(list: MutableList<DisplayableItem>, isEnabled: Boolean) : MutableList<DisplayableItem>{
        if (isEnabled && list.isNotEmpty()){
            val size = list.size
            list.clear()
            list.addAll(0, headers.recent(size, size > DetailFragmentViewModel.VISIBLE_RECENTLY_ADDED_PAGES))
        } else {
            list.clear()
        }
        return list
    }

    private fun handleAlbumsHeader(list: MutableList<DisplayableItem>, item: List<DisplayableItem>) : MutableList<DisplayableItem>{
        val albumsList = list.toMutableList()
        if (albumsList.isNotEmpty()){
            val artist = when {
                mediaId.isAlbum -> item[1].subtitle
                else -> null
            }
            albumsList.add(0, headers.albums(artist))
        }

        return albumsList
    }

    private fun handleRelatedArtistsHeader(list: MutableList<DisplayableItem>, isEnabled: Boolean) : MutableList<DisplayableItem>{
        if (isEnabled && list.isNotEmpty()){
            val size = list.size
            list.clear()
            list.addAll(0, headers.relatedArtists(size > 10))
        } else {
            list.clear()
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

    fun showSortByTutorialIfNeverShown(): Completable {
        return tutorialPreferenceUseCase.sortByTutorial()
    }

}