package dev.olog.feature.media.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.fragment.app.Fragment
import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortEntity
import dev.olog.feature.media.api.model.PlayerItem
import dev.olog.feature.media.api.model.PlayerMetadata
import dev.olog.feature.media.api.model.PlayerPlaybackState
import dev.olog.feature.media.api.model.PlayerRepeatMode
import dev.olog.feature.media.api.model.PlayerShuffleMode
import dev.olog.shared.extension.findInContext
import kotlinx.coroutines.flow.Flow

val Fragment.mediaProvider: MediaProvider
    get() = requireActivity().findInContext()

// todo bad api, refactor
val LocalMediaProvider: MediaProvider
    @Composable
    get() = if (LocalInspectionMode.current) {
        DummyMediaProvider
    } else {
        LocalContext.current.findInContext()
    }

interface MediaProvider {

    fun observeMetadata(): Flow<PlayerMetadata>
    fun observePlaybackState(): Flow<PlayerPlaybackState>
    fun observeRepeat(): Flow<PlayerRepeatMode>
    fun observeShuffle(): Flow<PlayerShuffleMode>
    // is a flow instead of livedata because list operations may be expensive, so they can be
    // moved to a background thread
    fun observeQueue(): Flow<List<PlayerItem>>

    fun playFromMediaId(mediaId: MediaId, filter: String?, sort: SortEntity?)
    fun playMostPlayed(mediaId: MediaId)
    fun playRecentlyAdded(mediaId: MediaId)

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