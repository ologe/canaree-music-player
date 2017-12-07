package dev.olog.music_service.interfaces

import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import dev.olog.music_service.model.PlayerMediaEntity
import dev.olog.music_service.model.PositionInQueue
import io.reactivex.Single

interface Queue {

    fun getCurrentPositionInQueue(): PositionInQueue

    fun prepare(): Single<Pair<PlayerMediaEntity, Long>>

    fun handleSkipToNext(): PlayerMediaEntity

    fun handleSkipToPrevious(playerBookmark: Long): PlayerMediaEntity

    fun handlePlayFromMediaId(mediaId: String): Single<PlayerMediaEntity>

    fun handlePlayRecentlyPlayed(mediaId: String): Single<PlayerMediaEntity>

    fun handlePlayMostPlayed(mediaId: String): Single<PlayerMediaEntity>

    fun handleSkipToQueueItem(id: Long): PlayerMediaEntity

    fun handlePlayShuffle(bundle: Bundle): Single<PlayerMediaEntity>

    fun handlePlayFromSearch(extras: Bundle): Single<PlayerMediaEntity>

    fun handlePlayFromGoogleSearch(query: String, extras: Bundle): Single<PlayerMediaEntity>

    fun sort()

    fun shuffle()

    fun addItemToQueue(item: MediaDescriptionCompat)

}
