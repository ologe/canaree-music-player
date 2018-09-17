package dev.olog.msc.music.service.interfaces

import android.os.Bundle
import dev.olog.msc.music.service.model.PlayerMediaEntity
import dev.olog.msc.music.service.model.PositionInQueue
import dev.olog.msc.utils.MediaId
import io.reactivex.Single

interface Queue {

    fun isReady() : Boolean

    fun getCurrentPositionInQueue(): PositionInQueue

    fun prepare(): Single<Pair<PlayerMediaEntity, Long>>

    fun handleSkipToNext(trackEnded: Boolean): PlayerMediaEntity?

    fun handleSkipToPrevious(playerBookmark: Long): PlayerMediaEntity?

    fun handlePlayFromMediaId(mediaId: MediaId, extras: Bundle?): Single<PlayerMediaEntity>

    fun handlePlayRecentlyPlayed(mediaId: MediaId): Single<PlayerMediaEntity>

    fun handlePlayMostPlayed(mediaId: MediaId): Single<PlayerMediaEntity>

    fun handleSkipToQueueItem(idInPlaylist: Long): PlayerMediaEntity

    fun handlePlayShuffle(mediaId: MediaId): Single<PlayerMediaEntity>

    fun handlePlayFolderTree(mediaId: MediaId): Single<PlayerMediaEntity>

    fun handlePlayFromGoogleSearch(query: String, extras: Bundle): Single<PlayerMediaEntity>

    fun getPlayingSong(): PlayerMediaEntity

    fun handleSwap(extras: Bundle)
    fun handleSwapRelative(extras: Bundle)

    fun handleRemove(extras: Bundle)
    fun handleRemoveRelative(extras: Bundle)

    fun sort()

    fun shuffle()

    fun onRepeatModeChanged()

    fun playLater(songIds: List<Long>) : PositionInQueue

    fun playNext(songIds: List<Long>) : PositionInQueue
    fun moveToPlayNext(idInPlaylist: Int) : PositionInQueue

}
