package dev.olog.service.music.event.queue

import android.net.Uri
import android.os.Bundle
import dev.olog.core.MediaId

// TODO show track not found when needed, toast? snackbar?
sealed class MediaSessionEvent {

    object Prepare : MediaSessionEvent()

    data class PlayFromMediaId(
        val mediaId: MediaId,
        val filter: String?
    ) : MediaSessionEvent()

    data class PlayFromSearch(
        val query: String,
        val extras: Bundle
    ) : MediaSessionEvent()

    data class PlayFromUri(
        val uri: Uri,
    ) : MediaSessionEvent()

    data class PlayShuffle(
        val mediaId: MediaId,
        val filter: String?
    ) : MediaSessionEvent()

    data class PlayRecentlyAdded(
        val mediaId: MediaId
    ) : MediaSessionEvent()

    data class PlayMostPlayed(
        val mediaId: MediaId
    ) : MediaSessionEvent()

    data class Swap(
        val from: Int,
        val to: Int
    ) : MediaSessionEvent()

    data class SwapRelative(
        val from: Int,
        val to: Int
    ) : MediaSessionEvent()

    data class Remove(
        val position: Int
    ) : MediaSessionEvent()

    data class RemoveRelative(
        val position: Int
    ) : MediaSessionEvent()

    data class MoveRelative(
        val position: Int
    ) : MediaSessionEvent()

    data class AddToPlayLater(
        val ids: List<Long>,
        val isPodcast: Boolean,
    ) : MediaSessionEvent()

    data class AddToPlayNext(
        val ids: List<Long>,
        val isPodcast: Boolean,
    ) : MediaSessionEvent()

    object Resume : MediaSessionEvent()

    data class Pause(
        val stopService: Boolean,
        val releaseFocus: Boolean = true,
    ) : MediaSessionEvent()

    data class SeekTo(
        val millis: Long
    ) : MediaSessionEvent()

    object SkipToPrevious : MediaSessionEvent()
    data class SkipToNext(val ended: Boolean) : MediaSessionEvent()
    data class SkipToItem(val id: Long) : MediaSessionEvent()

    object Forward10Seconds : MediaSessionEvent()
    object Forward30Seconds : MediaSessionEvent()

    object Replay10Seconds : MediaSessionEvent()
    object Replay30Seconds : MediaSessionEvent()

    object ToggleFavorite : MediaSessionEvent()

    object RepeatModeChanged : MediaSessionEvent()
    object ShuffleModeChanged : MediaSessionEvent()

}