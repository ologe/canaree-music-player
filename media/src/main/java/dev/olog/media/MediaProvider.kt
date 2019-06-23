package dev.olog.media

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortEntity

interface MediaProvider {

    fun observeMetadata(): LiveData<MediaMetadataCompat>
    fun observePlaybackState(): LiveData<PlaybackStateCompat>
    fun observeRepeat(): LiveData<Int>
    fun observeShuffle(): LiveData<Int>
    fun observeQueue(): LiveData<List<MediaSessionCompat.QueueItem>>
    fun observeQueueTitle(): LiveData<String>
    fun observeExtras(): LiveData<Bundle>

    fun playFromMediaId(mediaId: MediaId, sort: SortEntity? = null)
    fun playMostPlayed(mediaId: MediaId)
    fun playRecentlyAdded(mediaId: MediaId)

    fun skipToQueueItem(idInPlaylist: Long)
    fun shuffle(mediaId: MediaId)
    fun skipToNext()
    fun skipToPrevious()
    fun playPause()
    fun seekTo(where: Long)
    fun toggleShuffleMode()
    fun toggleRepeatMode()

    fun addToPlayNext(mediaId: MediaId)
    fun moveToPlayNext(mediaId: MediaId)

    fun togglePlayerFavorite()

    fun swap(from: Int, to: Int)
    fun swapRelative(from: Int, to: Int)

    fun remove(position: Int)
    fun removeRelative(position: Int)

    fun replayTenSeconds()
    fun forwardTenSeconds()

    fun replayThirtySeconds()
    fun forwardThirtySeconds()

}