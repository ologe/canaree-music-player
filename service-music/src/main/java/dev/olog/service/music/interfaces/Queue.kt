package dev.olog.service.music.interfaces

import android.net.Uri
import android.os.Bundle
import dev.olog.service.music.model.PlayerMediaEntity
import dev.olog.service.music.model.PositionInQueue
import dev.olog.core.MediaId
import io.reactivex.Single

interface Queue {

    fun isReady() : Boolean

    fun getCurrentPositionInQueue(): dev.olog.service.music.model.PositionInQueue

    fun prepare(): Single<dev.olog.service.music.model.PlayerMediaEntity>

    fun handleSkipToNext(trackEnded: Boolean): dev.olog.service.music.model.PlayerMediaEntity?

    fun handleSkipToPrevious(playerBookmark: Long): dev.olog.service.music.model.PlayerMediaEntity?

    fun handlePlayFromMediaId(mediaId: MediaId, extras: Bundle?): Single<dev.olog.service.music.model.PlayerMediaEntity>

    fun handlePlayRecentlyPlayed(mediaId: MediaId): Single<dev.olog.service.music.model.PlayerMediaEntity>

    fun handlePlayMostPlayed(mediaId: MediaId): Single<dev.olog.service.music.model.PlayerMediaEntity>

    fun handleSkipToQueueItem(idInPlaylist: Long): dev.olog.service.music.model.PlayerMediaEntity

    fun handlePlayShuffle(mediaId: MediaId): Single<dev.olog.service.music.model.PlayerMediaEntity>

    fun handlePlayFolderTree(mediaId: MediaId): Single<dev.olog.service.music.model.PlayerMediaEntity>

    fun handlePlayFromGoogleSearch(query: String, extras: Bundle): Single<dev.olog.service.music.model.PlayerMediaEntity>

    fun handlePlayFromUri(uri: Uri): Single<dev.olog.service.music.model.PlayerMediaEntity>

    fun getPlayingSong(): dev.olog.service.music.model.PlayerMediaEntity

    fun handleSwap(extras: Bundle)
    fun handleSwapRelative(extras: Bundle)

    fun handleRemove(extras: Bundle): Boolean
    fun handleRemoveRelative(extras: Bundle): Boolean

    fun sort()

    fun shuffle()

    fun onRepeatModeChanged()

    fun playLater(songIds: List<Long>, isPodcast: Boolean) : dev.olog.service.music.model.PositionInQueue

    fun playNext(songIds: List<Long>, isPodcast: Boolean) : dev.olog.service.music.model.PositionInQueue
//    fun moveToPlayNext(idInPlaylist: Int) : PositionInQueue

    fun updatePodcastPosition(position: Long)

}
