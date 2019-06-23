package dev.olog.msc.presentation.detail

import dev.olog.core.PlaylistConstants
import dev.olog.presentation.model.PlaylistType
import dev.olog.msc.domain.gateway.prefs.TutorialPreferenceGateway
import dev.olog.msc.domain.interactor.playlist.MoveItemInPlaylistUseCase
import dev.olog.msc.domain.interactor.playlist.RemoveFromPlaylistUseCase
import dev.olog.presentation.model.DisplayableItem
import dev.olog.core.MediaId
import io.reactivex.Completable
import javax.inject.Inject

class DetailFragmentPresenter @Inject constructor(
    private val mediaId: MediaId,
    private val removeFromPlaylistUseCase: RemoveFromPlaylistUseCase,
    private val headers: DetailFragmentHeaders,
    private val moveItemInPlaylistUseCase: MoveItemInPlaylistUseCase,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway

) {

    fun removeFromPlaylist(item: DisplayableItem): Completable {
        mediaId.assertPlaylist()
        val playlistId = mediaId.resolveId
        val playlistType = if (item.mediaId.isPodcast) PlaylistType.PODCAST else PlaylistType.TRACK
        if (playlistId == PlaylistConstants.FAVORITE_LIST_ID){
            // favorites use songId instead of idInPlaylist
            return removeFromPlaylistUseCase.execute(RemoveFromPlaylistUseCase.Input(
                    playlistId, item.mediaId.leaf!!, playlistType
            ))
        }
        return removeFromPlaylistUseCase.execute(RemoveFromPlaylistUseCase.Input(
                playlistId, item.trackNumber.toLong(), playlistType
        ))
    }

    fun moveInPlaylist(from: Int, to: Int){
        mediaId.assertPlaylist()
        val playlistId = mediaId.resolveId
        moveItemInPlaylistUseCase.execute(MoveItemInPlaylistUseCase.Input(playlistId, from, to,
                if (mediaId.isPodcastPlaylist) PlaylistType.PODCAST else PlaylistType.TRACK
        ))
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

        if (mediaId.isArtist){
            // swap albums to top
            return mutableMapOf(
                    DetailFragmentDataType.HEADER to item.toMutableList(),
                    DetailFragmentDataType.ALBUMS to handleAlbumsHeader(albums.toMutableList()),
                    DetailFragmentDataType.MOST_PLAYED to handleMostPlayedHeader(mostPlayed.toMutableList(), visibility[0]),
                    DetailFragmentDataType.RECENT to handleRecentlyAddedHeader(recent.toMutableList(), visibility[1]),
                    DetailFragmentDataType.SONGS to handleSongsHeader(songs.toMutableList()),
                    DetailFragmentDataType.ARTISTS_IN to handleRelatedArtistsHeader(artists.toMutableList(), visibility[2])
            )
        }

        return mutableMapOf(
                DetailFragmentDataType.HEADER to item.toMutableList(),
                DetailFragmentDataType.MOST_PLAYED to handleMostPlayedHeader(mostPlayed.toMutableList(), visibility[0]),
                DetailFragmentDataType.RECENT to handleRecentlyAddedHeader(recent.toMutableList(), visibility[1]),
                DetailFragmentDataType.SONGS to handleSongsHeader(songs.toMutableList()),
                DetailFragmentDataType.ARTISTS_IN to handleRelatedArtistsHeader(artists.toMutableList(), visibility[2]),
                DetailFragmentDataType.ALBUMS to handleAlbumsHeader(albums.toMutableList())
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

    private fun handleAlbumsHeader(list: MutableList<DisplayableItem>) : MutableList<DisplayableItem>{
        if (list.isNotEmpty()){
            list.clear()
            list.addAll(0, headers.albums())
        }
        return list
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