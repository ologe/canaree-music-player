package dev.olog.lib.media

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortEntity
import dev.olog.lib.media.model.*
import kotlinx.coroutines.flow.Flow

val FragmentActivity.mediaProvider: MediaProvider
    get() = this as MediaProvider

val Fragment.mediaProvider: MediaProvider
    get() = requireActivity().mediaProvider

interface MediaProvider {

    val metadata: Flow<PlayerMetadata>
    val playbackState: Flow<PlayerPlaybackState>
    val repeat: Flow<PlayerRepeatMode>
    val shuffle: Flow<PlayerShuffleMode>
    val queue: Flow<List<PlayerItem>>

    fun playFromMediaId(
        mediaId: MediaId,
        filter: String?,
        sort: SortEntity?
    )

    fun skipToQueueItem(idInPlaylist: Int)
    fun shuffle(mediaId: MediaId, filter: String?)
    fun skipToNext()
    fun skipToPrevious()
    fun playPause()
    fun seekTo(where: Long)
    fun toggleShuffleMode()
    fun toggleRepeatMode()

    fun addToPlayNext(mediaId: MediaId)

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