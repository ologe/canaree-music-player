package dev.olog.service.music.interfaces

import android.net.Uri
import android.os.Bundle
import dev.olog.core.MediaId
import dev.olog.service.music.model.PlayerMediaEntity
import dev.olog.service.music.model.PositionInQueue

internal interface Queue {

    fun getCurrentPositionInQueue(): PositionInQueue

    suspend fun prepare(): PlayerMediaEntity?

    fun handleSkipToNext(trackEnded: Boolean): PlayerMediaEntity?
    fun handleSkipToPrevious(playerBookmark: Long): PlayerMediaEntity?
    fun handleSkipToQueueItem(idInPlaylist: Long): PlayerMediaEntity?

    suspend fun handlePlayFromMediaId(mediaId: MediaId): PlayerMediaEntity?
    suspend fun handlePlayRecentlyAdded(mediaId: MediaId): PlayerMediaEntity?
    suspend fun handlePlayMostPlayed(mediaId: MediaId): PlayerMediaEntity?
    suspend fun handlePlayShuffle(mediaId: MediaId): PlayerMediaEntity?
    suspend fun handlePlayFromGoogleSearch(query: String, extras: Bundle): PlayerMediaEntity?
    suspend fun handlePlayFromUri(uri: Uri): PlayerMediaEntity?

    fun getPlayingSong(): PlayerMediaEntity?

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
