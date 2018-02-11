package dev.olog.msc.presentation.base.music.service

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.msc.presentation.detail.sort.DetailSort
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable

interface MediaProvider {

    fun onMetadataChanged(): Observable<MediaMetadataCompat>
    fun onStateChanged(): Observable<PlaybackStateCompat>
    fun onRepeatModeChanged(): Observable<Int>
    fun onShuffleModeChanged(): Observable<Int>
    fun onQueueChanged(): Observable<List<MediaSessionCompat.QueueItem>>

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

}