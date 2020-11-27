package dev.olog.service.music.interfaces

import android.net.Uri
import android.os.Bundle
import dev.olog.core.MediaId
import dev.olog.service.music.model.PlayerMediaEntity
import dev.olog.service.music.model.PositionInQueue

// TODO made get queue cancellable
internal interface IQueue {

    suspend fun getCurrentPositionInQueue(): PositionInQueue

    suspend fun prepare(): PlayerMediaEntity?
    suspend fun isEmpty(): Boolean

    suspend fun handleSkipToNext(trackEnded: Boolean): PlayerMediaEntity?
    suspend fun handleSkipToPrevious(playerBookmark: Long): PlayerMediaEntity?
    suspend fun handleSkipToQueueItem(idInPlaylist: Long): PlayerMediaEntity?

    suspend fun handlePlayFromMediaId(mediaId: MediaId, filter: String?): PlayerMediaEntity?
    suspend fun handlePlayRecentlyAdded(mediaId: MediaId): PlayerMediaEntity?
    suspend fun handlePlayMostPlayed(mediaId: MediaId): PlayerMediaEntity?
    suspend fun handlePlayShuffle(mediaId: MediaId, filter: String?): PlayerMediaEntity?
    suspend fun handlePlayFromGoogleSearch(query: String, extras: Bundle): PlayerMediaEntity?
    suspend fun handlePlayFromUri(uri: Uri): PlayerMediaEntity?

    suspend fun getPlayingSong(): PlayerMediaEntity?

    suspend fun handleSwap(from: Int, to: Int)
    suspend fun handleSwapRelative(from: Int, to: Int)

    suspend fun handleRemove(position: Int)
    suspend fun handleRemoveRelative(position: Int)
    suspend fun handleMoveRelative(position: Int)

    suspend fun sort()
    suspend fun shuffle()

    suspend fun onRepeatModeChanged()

    suspend fun playLater(songIds: List<Long>, isPodcast: Boolean): PositionInQueue
    suspend fun playNext(songIds: List<Long>, isPodcast: Boolean): PositionInQueue
//    fun moveToPlayNext(idInPlaylist: Int) : PositionInQueue

    suspend fun updatePodcastPosition(position: Long)

}
