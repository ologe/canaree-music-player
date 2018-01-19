package dev.olog.music_service.interfaces

import android.os.Bundle
import dev.olog.music_service.model.PlayerMediaEntity
import dev.olog.music_service.model.PositionInQueue
import dev.olog.shared.MediaId
import io.reactivex.Single

interface Queue {

    fun getCurrentPositionInQueue(): PositionInQueue

    fun prepare(): Single<Pair<PlayerMediaEntity, Long>>

    fun handleSkipToNext(): PlayerMediaEntity

    fun handleSkipToPrevious(playerBookmark: Long): PlayerMediaEntity

    fun handlePlayFromMediaId(mediaId: MediaId, extras: Bundle?): Single<PlayerMediaEntity>

    fun handlePlayRecentlyPlayed(mediaId: MediaId): Single<PlayerMediaEntity>

    fun handlePlayMostPlayed(mediaId: MediaId): Single<PlayerMediaEntity>

    fun handleSkipToQueueItem(id: Long): PlayerMediaEntity

    fun handleSkipToQueueItemWithIdInPlaylist(idInPlaylist: Long): PlayerMediaEntity

    fun handlePlayShuffle(mediaId: MediaId): Single<PlayerMediaEntity>

    fun handlePlayFromSearch(extras: Bundle): Single<PlayerMediaEntity>

    fun handlePlayFromGoogleSearch(query: String, extras: Bundle): Single<PlayerMediaEntity>

    fun handleSwap(extras: Bundle)
    fun handleSwapRelative(extras: Bundle)

    fun handleRemove(extras: Bundle)
    fun handleRemoveRelative(extras: Bundle)

    fun sort()

    fun shuffle()

    fun doWhenReady(func: () -> Unit)

}
