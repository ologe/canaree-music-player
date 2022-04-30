package dev.olog.feature.media.interfaces

import android.net.Uri
import android.os.Bundle
import dev.olog.core.MediaId
import dev.olog.feature.media.model.PlayerMediaEntity
import dev.olog.feature.media.model.PositionInQueue

internal interface IQueue {

    fun getCurrentPositionInQueue(): PositionInQueue

    fun prepare(): PlayerMediaEntity?
    fun isEmpty(): Boolean

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

    fun handleSwap(from: Int, to: Int)
    fun handleSwapRelative(from: Int, to: Int)

    fun handleRemove(position: Int)
    fun handleRemoveRelative(position: Int)
    fun handleMoveRelative(position: Int)

    fun sort()
    fun shuffle()

    fun onRepeatModeChanged()

    suspend fun playLater(songIds: List<Long>, isPodcast: Boolean): PositionInQueue
    suspend fun playNext(songIds: List<Long>, isPodcast: Boolean): PositionInQueue
//    fun moveToPlayNext(idInPlaylist: Int) : PositionInQueue

    fun updatePodcastPosition(position: Long)

}
