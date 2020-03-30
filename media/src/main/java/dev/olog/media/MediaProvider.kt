package dev.olog.media

import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortEntity
import dev.olog.media.model.*
import kotlinx.coroutines.flow.Flow

interface MediaProvider {

    fun observeMetadata(): Flow<PlayerMetadata>
    fun observePlaybackState(): Flow<PlayerPlaybackState>
    fun observeRepeat(): Flow<PlayerRepeatMode>
    fun observeShuffle(): Flow<PlayerShuffleMode>
    fun observeQueue(): Flow<List<PlayerItem>>

    fun playFromMediaId(mediaId: MediaId, filter: String?, sort: SortEntity?)
    fun playMostPlayed(mediaId: MediaId.Track)
    fun playRecentlyAdded(mediaId: MediaId.Track)
    fun playSpotifyPreview(mediaId: MediaId)

    fun skipToQueueItem(idInPlaylist: Int)
    fun shuffle(mediaId: MediaId.Category, filter: String?)
    fun skipToNext()
    fun skipToPrevious()
    fun playPause()
    fun seekTo(where: Long)
    fun toggleShuffleMode()
    fun toggleRepeatMode()

    fun addToPlayNext(mediaId: MediaId.Track)

    fun togglePlayerFavorite()

    fun swap(from: Int, to: Int)
    fun swapRelative(from: Int, to: Int)

    fun remove(position: Int)
    fun removeRelative(position: Int)

    fun moveRelative(position: Int)

    fun replayTenSeconds()
    fun forwardTenSeconds()

    fun replayThirtySeconds()
    fun forwardThirtySeconds()

}