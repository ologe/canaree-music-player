package dev.olog.msc.presentation.base.music.service

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.msc.presentation.detail.sort.DetailSort
import dev.olog.core.MediaId
import io.reactivex.Observable

interface MediaProvider {

    fun onMetadataChanged(): Observable<MediaMetadataCompat>
    fun onStateChanged(): Observable<PlaybackStateCompat>
    fun onRepeatModeChanged(): Observable<Int>
    fun onShuffleModeChanged(): Observable<Int>
    fun onQueueChanged(): Observable<List<MediaSessionCompat.QueueItem>>
    fun onQueueTitleChanged(): Observable<String>
    fun onExtrasChanged(): Observable<Bundle>

    fun playFromMediaId(mediaId: MediaId, sort: DetailSort? = null)
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