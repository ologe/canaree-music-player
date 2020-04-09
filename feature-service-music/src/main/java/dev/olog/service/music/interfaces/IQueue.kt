package dev.olog.service.music.interfaces

import android.net.Uri
import android.os.Bundle
import dev.olog.domain.MediaId
import dev.olog.service.music.model.PlayerMediaEntity
import dev.olog.service.music.model.PositionInQueue

internal interface IQueue {

    fun getCurrentPositionInQueue(): PositionInQueue

    fun prepare(): PlayerMediaEntity?
    fun isEmpty(): Boolean

    suspend fun handleSkipToNext(trackEnded: Boolean): PlayerMediaEntity?
    suspend fun handleSkipToPrevious(playerBookmark: Long): PlayerMediaEntity?
    suspend fun handleSkipToQueueItem(idInPlaylist: Long): PlayerMediaEntity?

    suspend fun handlePlayFromMediaId(mediaId: MediaId, filter: String?): PlayerMediaEntity?
    suspend fun handlePlayRecentlyAdded(mediaId: MediaId.Track): PlayerMediaEntity?
    suspend fun handlePlayMostPlayed(mediaId: MediaId.Track): PlayerMediaEntity?
    suspend fun handlePlayShuffle(mediaId: MediaId.Category, filter: String?): PlayerMediaEntity?
    suspend fun handlePlayFromGoogleSearch(query: String, extras: Bundle): PlayerMediaEntity?
    suspend fun handlePlayFromUri(uri: Uri): PlayerMediaEntity?
    suspend fun handlePlaySpotifyPreview(mediaId: MediaId.Track): PlayerMediaEntity?

    suspend fun getPlayingSong(): PlayerMediaEntity?

    fun handleSwap(from: Int, to: Int)
    fun handleSwapRelative(from: Int, to: Int)

    fun handleRemove(position: Int)
    fun handleRemoveRelative(position: Int)
    fun handleMoveRelative(position: Int)

    fun sort()
    fun shuffle()

    fun onRepeatModeChanged()

    suspend fun playLater(songIds: List<Long>): PositionInQueue
    suspend fun playNext(songIds: List<Long>): PositionInQueue
//    fun moveToPlayNext(idInPlaylist: Int) : PositionInQueue

    fun updatePodcastPosition(position: Long)

}
