package dev.olog.service.music

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.MediaId
import dev.olog.intents.MusicServiceCustomAction
import dev.olog.service.music.event.queue.MediaSessionEvent
import dev.olog.service.music.event.queue.MediaSessionEventHandler
import dev.olog.shared.android.extensions.toJavaUri
import dev.olog.shared.android.toMap
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.milliseconds

@ServiceScoped
internal class MediaSessionEventDispatcher @Inject constructor(
    private val eventHandler: MediaSessionEventHandler,
    private val mediaButtonHandler: MediaButton,
) : MediaSessionCompat.Callback() {

    init {
        onPrepare()
    }

    // region prepare

    override fun onPrepare() {
        Timber.v("onPrepare")
        eventHandler.nextEvent(MediaSessionEvent.Prepare.LastQueue)
    }

    override fun onPrepareFromMediaId(mediaId: String, extras: Bundle?) {
        Timber.v("onPrepareFromMediaId mediaId=$mediaId, extras=$extras")

        val event = MediaSessionEvent.Prepare.FromMediaId(
            mediaId = MediaId.fromString(mediaId), // TODO exit if is not valid
            extras = extras.toMap()
        )
        eventHandler.nextEvent(event)
    }

    override fun onPrepareFromSearch(query: String?, extras: Bundle?) {
        Timber.v("onPrepareFromSearch query=$query, extras=$extras")
        val event = MediaSessionEvent.Prepare.FromSearch(
            query = query,
            extras = extras.toMap()
        )
        eventHandler.nextEvent(event)
    }

    override fun onPrepareFromUri(uri: Uri, extras: Bundle?) {
        Timber.v("onPrepareFromUri, uri=$uri, extras=$extras")
        val event = MediaSessionEvent.Prepare.FromUri(
            uri = uri.toJavaUri(),
            extras = extras.toMap()
        )
        eventHandler.nextEvent(event)
    }

    // endregion

    // region play from

    override fun onPlayFromMediaId(mediaId: String, extras: Bundle?) {
        Timber.v("onPlayFromMediaId mediaId=$mediaId, extras=$extras")
        val event = MediaSessionEvent.Play.FromMediaId(
            mediaId = MediaId.fromString(mediaId), // TODO exit if is not valid
            extras = extras.toMap()
        )
        eventHandler.nextEvent(event)
    }

    override fun onPlayFromSearch(query: String?, extras: Bundle) {
        Timber.v("onPlayFromSearch query=$query, extras=$extras")
        val event = MediaSessionEvent.Play.FromSearch(
            query = query,
            extras = extras.toMap()
        )
        eventHandler.nextEvent(event)
    }

    override fun onPlayFromUri(uri: Uri, extras: Bundle?) {
        Timber.v("onPlayFromUri uri=$uri, extras=$extras")
        val event = MediaSessionEvent.Play.FromUri(
            uri = uri.toJavaUri(),
            extras = extras.toMap()
        )
        eventHandler.nextEvent(event)
    }

    // endregion

    // region player actions

    override fun onPlay() {
        Timber.v("onPlay")
        eventHandler.nextEvent(MediaSessionEvent.PlayerAction.Resume)
    }

    override fun onPause() {
        Timber.v("onPause")
        val event = MediaSessionEvent.PlayerAction.Pause(
            stopService = true // TODO stop?
        )
        eventHandler.nextEvent(event)
    }

    override fun onStop() {
        Timber.v("onStop")
        onPause()
    }

    override fun onSkipToNext() {
        Timber.v("onSkipToNext")
        val event = MediaSessionEvent.PlayerAction.SkipToNext(
            ended = false
        )
        eventHandler.nextEvent(event)
    }

    private fun onTrackEnded() {
        val event = MediaSessionEvent.PlayerAction.SkipToNext(
            ended = true
        )
        eventHandler.nextEvent(event)
    }

    override fun onSkipToPrevious() {
        Timber.v("onSkipToPrevious")
        eventHandler.nextEvent(MediaSessionEvent.PlayerAction.SkipToPrevious)
    }

    override fun onSkipToQueueItem(id: Long) {
        Timber.v("onSkipToQueueItem id=$id")
        val event = MediaSessionEvent.PlayerAction.SkipToItem(
            progressive = id.toInt()
        )
        eventHandler.nextEvent(event)
    }

    override fun onSeekTo(pos: Long) {
        Timber.v("onSeekTo pos=$pos")
        val event = MediaSessionEvent.PlayerAction.SeekTo(
            millis = pos.milliseconds
        )
        eventHandler.nextEvent(event)
    }

    override fun onFastForward() {
        Timber.v("onFastForward")
    }

    override fun onRewind() {
        Timber.v("onRewind")
    }

    // endregion

    override fun onSetRating(rating: RatingCompat?) {
        onSetRating(rating, null)
    }

    override fun onSetRating(rating: RatingCompat?, extras: Bundle?) {
        eventHandler.nextEvent(MediaSessionEvent.ToggleFavorite)
    }

    override fun onCustomAction(action: String, extras: Bundle?) {
        val customAction = MusicServiceCustomAction.values().find { it.name == action } ?: return

        eventHandler.nextEvent(MediaSessionEvent.CustomAction(customAction, extras.toMap()))
    }

    override fun onSetRepeatMode(repeatMode: Int) {
        eventHandler.nextEvent(MediaSessionEvent.RepeatModeChanged)
    }

    override fun onSetShuffleMode(unused: Int) {
        eventHandler.nextEvent(MediaSessionEvent.ShuffleModeChanged)
    }

    override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
        val event = mediaButtonIntent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)!!
        if (event.action == KeyEvent.ACTION_DOWN) {

            when (event.keyCode) {
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> handlePlayPause()
                KeyEvent.KEYCODE_MEDIA_NEXT -> onSkipToNext()
                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> onSkipToPrevious()
                KeyEvent.KEYCODE_MEDIA_STOP -> {
                    val sessionEvent = MediaSessionEvent.PlayerAction.Pause(stopService = true)
                    eventHandler.nextEvent(sessionEvent)
                }
                KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                    val sessionEvent = MediaSessionEvent.PlayerAction.Pause(stopService = false)
                    eventHandler.nextEvent(sessionEvent)
                }
                KeyEvent.KEYCODE_MEDIA_PLAY -> onPlay()
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> onTrackEnded()
                KeyEvent.KEYCODE_HEADSETHOOK -> mediaButtonHandler.onHeatSetHookClick()
                else -> Timber.e("not handled ${event.action}")
            }
        }

        return true
    }

    fun handlePlayPause() {
        eventHandler.nextEvent(MediaSessionEvent.PlayerAction.PlayPause)
    }

}