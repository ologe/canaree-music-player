package dev.olog.service.music

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.KeyEvent
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.MediaId
import dev.olog.intents.MusicServiceCustomAction
import dev.olog.service.music.interfaces.IPlayer
import dev.olog.service.music.event.queue.MediaSessionEvent
import dev.olog.service.music.event.queue.MediaSessionEventHandler
import javax.inject.Inject

@ServiceScoped
internal class MediaSessionCallback @Inject constructor(
    private val eventDispatcher: MediaSessionEventHandler,
    private val player: IPlayer,
    private val mediaButton: MediaButton,
) : MediaSessionCompat.Callback() {

    override fun onPrepare() {
        eventDispatcher.nextEvent(MediaSessionEvent.Prepare)
    }

    override fun onPlayFromMediaId(stringMediaId: String, extras: Bundle?) {
        val mediaId = MediaId.fromString(stringMediaId)
        val filter = extras?.getString(MusicServiceCustomAction.ARGUMENT_FILTER)

        val event = MediaSessionEvent.PlayFromMediaId(mediaId, filter)
        eventDispatcher.nextEvent(event)
    }

    override fun onPlay() {
        eventDispatcher.nextEvent(MediaSessionEvent.Resume)
    }

    override fun onPause() {
        eventDispatcher.nextEvent(MediaSessionEvent.Pause(true))
    }

    override fun onPlayFromSearch(query: String, extras: Bundle) {
        val event = MediaSessionEvent.PlayFromSearch(query, extras)
        eventDispatcher.nextEvent(event)
    }

    override fun onPlayFromUri(uri: Uri, extras: Bundle?) {
        val event = MediaSessionEvent.PlayFromUri(uri)
        eventDispatcher.nextEvent(event)
    }

    override fun onStop() {
        onPause()
    }

    override fun onSkipToNext() {
        onSkipToNext(false)
    }

    private fun onTrackEnded() {
        onSkipToNext(true)
    }

    override fun onSkipToPrevious() {
        eventDispatcher.nextEvent(MediaSessionEvent.SkipToPrevious)
    }


    /**
     * Try to skip to next song, if can't, restart current and pause
     */
    private fun onSkipToNext(trackEnded: Boolean)  {
        val event = MediaSessionEvent.SkipToNext(trackEnded)
        eventDispatcher.nextEvent(event)
    }

    override fun onSkipToQueueItem(id: Long) {
        val event = MediaSessionEvent.SkipToItem(id)
        eventDispatcher.nextEvent(event)
    }

    override fun onSeekTo(pos: Long) {
        val event = MediaSessionEvent.SeekTo(pos)
        eventDispatcher.nextEvent(event)
    }

    override fun onSetRating(rating: RatingCompat?) {
        onSetRating(rating, null)
    }

    override fun onSetRating(rating: RatingCompat?, extras: Bundle?) {
        eventDispatcher.nextEvent(MediaSessionEvent.ToggleFavorite)
    }

    override fun onCustomAction(action: String, extras: Bundle?) {

        val musicAction = MusicServiceCustomAction.values().find { it.name == action }
            ?: return // other apps can request custom action

        val event = when (musicAction) {
            MusicServiceCustomAction.SHUFFLE -> {
                requireNotNull(extras)
                val mediaId = extras.getString(MusicServiceCustomAction.ARGUMENT_MEDIA_ID)!!
                val filter = extras.getString(MusicServiceCustomAction.ARGUMENT_FILTER)

                MediaSessionEvent.PlayShuffle(
                    MediaId.fromString(mediaId),
                    filter
                )
            }
            MusicServiceCustomAction.SWAP -> {
                requireNotNull(extras)
                val from = extras.getInt(MusicServiceCustomAction.ARGUMENT_SWAP_FROM, 0)
                val to = extras.getInt(MusicServiceCustomAction.ARGUMENT_SWAP_TO, 0)
                MediaSessionEvent.Swap(from, to)
            }
            MusicServiceCustomAction.SWAP_RELATIVE -> {
                requireNotNull(extras)
                val from = extras.getInt(MusicServiceCustomAction.ARGUMENT_SWAP_FROM, 0)
                val to = extras.getInt(MusicServiceCustomAction.ARGUMENT_SWAP_TO, 0)
                MediaSessionEvent.SwapRelative(from, to)
            }
            MusicServiceCustomAction.REMOVE -> {
                requireNotNull(extras)
                val position = extras.getInt(MusicServiceCustomAction.ARGUMENT_POSITION, -1)
                MediaSessionEvent.Remove(position)
            }
            MusicServiceCustomAction.REMOVE_RELATIVE -> {
                requireNotNull(extras)
                val position = extras.getInt(MusicServiceCustomAction.ARGUMENT_POSITION, -1)
                MediaSessionEvent.RemoveRelative(position)
            }
            MusicServiceCustomAction.PLAY_RECENTLY_ADDED -> {
                requireNotNull(extras)
                val mediaId = extras.getString(MusicServiceCustomAction.ARGUMENT_MEDIA_ID)!!
                MediaSessionEvent.PlayRecentlyAdded(MediaId.fromString(mediaId))
            }
            MusicServiceCustomAction.PLAY_MOST_PLAYED -> {
                requireNotNull(extras)
                val mediaId = extras.getString(MusicServiceCustomAction.ARGUMENT_MEDIA_ID)!!
                MediaSessionEvent.PlayRecentlyAdded(MediaId.fromString(mediaId))
            }
            MusicServiceCustomAction.FORWARD_10 -> MediaSessionEvent.Forward10Seconds
            MusicServiceCustomAction.FORWARD_30 -> MediaSessionEvent.Forward30Seconds
            MusicServiceCustomAction.REPLAY_10 -> MediaSessionEvent.Replay10Seconds
            MusicServiceCustomAction.REPLAY_30 -> MediaSessionEvent.Replay30Seconds
            MusicServiceCustomAction.TOGGLE_FAVORITE -> {
                onSetRating(null)
                null
            }
            MusicServiceCustomAction.ADD_TO_PLAY_LATER -> {
                requireNotNull(extras)
                val mediaIds =
                    extras.getLongArray(MusicServiceCustomAction.ARGUMENT_MEDIA_ID_LIST)!!
                val isPodcast = extras.getBoolean(MusicServiceCustomAction.ARGUMENT_IS_PODCAST)

                MediaSessionEvent.AddToPlayLater(
                    mediaIds.toList(),
                    isPodcast
                )
            }
            MusicServiceCustomAction.ADD_TO_PLAY_NEXT -> {
                requireNotNull(extras)
                val mediaIds =
                    extras.getLongArray(MusicServiceCustomAction.ARGUMENT_MEDIA_ID_LIST)!!
                val isPodcast = extras.getBoolean(MusicServiceCustomAction.ARGUMENT_IS_PODCAST)

                MediaSessionEvent.AddToPlayNext(
                    mediaIds.toList(),
                    isPodcast
                )
            }
            MusicServiceCustomAction.MOVE_RELATIVE -> {
                requireNotNull(extras)
                val position = extras.getInt(MusicServiceCustomAction.ARGUMENT_POSITION)
                MediaSessionEvent.MoveRelative(position)
            }
        }
        if (event != null) {
            eventDispatcher.nextEvent(event)
        }
    }

    override fun onSetRepeatMode(repeatMode: Int) {
        eventDispatcher.nextEvent(MediaSessionEvent.RepeatModeChanged)
    }

    override fun onSetShuffleMode(unused: Int) {
        eventDispatcher.nextEvent(MediaSessionEvent.ShuffleModeChanged)
    }

    override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
        val event = mediaButtonIntent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)!!
        if (event.action == KeyEvent.ACTION_DOWN) {

            when (event.keyCode) {
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> handlePlayPause()
                KeyEvent.KEYCODE_MEDIA_NEXT -> onSkipToNext()
                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> onSkipToPrevious()
                KeyEvent.KEYCODE_MEDIA_STOP -> {
                    val sessionEvent = MediaSessionEvent.Pause(true)
                    eventDispatcher.nextEvent(sessionEvent)
                }
                KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                    val sessionEvent = MediaSessionEvent.Pause(false)
                    eventDispatcher.nextEvent(sessionEvent)
                }
                KeyEvent.KEYCODE_MEDIA_PLAY -> onPlay()
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> onTrackEnded()
                KeyEvent.KEYCODE_HEADSETHOOK -> mediaButton.onHeatSetHookClick()
                else -> {
                    Log.e("MediaSessionCallback", "not handled ${event.action}")
                }
            }
        }

        return true
    }

    /**
     * this function DO NOT KILL service on pause
     */
    fun handlePlayPause() {
        val event = if (player.isPlaying()) {
            MediaSessionEvent.Pause(false)
        } else {
            MediaSessionEvent.Resume
        }
        eventDispatcher.nextEvent(event)
    }



}