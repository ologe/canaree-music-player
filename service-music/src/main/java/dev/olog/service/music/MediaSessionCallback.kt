package dev.olog.service.music

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.KeyEvent
import dev.olog.core.MediaId
import dev.olog.core.interactor.ToggleFavoriteUseCase
import dev.olog.injection.dagger.PerService
import dev.olog.service.music.interfaces.Player
import dev.olog.service.music.interfaces.Queue
import dev.olog.service.music.model.SkipType
import dev.olog.service.music.queue.SKIP_TO_PREVIOUS_THRESHOLD
import dev.olog.service.music.state.MusicServicePlaybackState
import dev.olog.service.music.state.MusicServiceRepeatMode
import dev.olog.service.music.state.MusicServiceShuffleMode
import dev.olog.shared.MusicConstants
import dev.olog.shared.MusicServiceCustomAction
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import javax.inject.Inject

@PerService
internal class MediaSessionCallback @Inject constructor(
    private val queue: Queue,
    private val player: Player,
    private val repeatMode: MusicServiceRepeatMode,
    private val shuffleMode: MusicServiceShuffleMode,
    private val mediaButton: MediaButton,
    private val playerState: MusicServicePlaybackState,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase

) : MediaSessionCompat.Callback() {

    companion object {
        @JvmStatic
        private val TAG = "SM:${MediaSessionCallback::class.java.simpleName}"
    }

    private val subscriptions = CompositeDisposable()

    override fun onPrepare() = runBlocking<Unit> {
        val track = queue.prepare()
        if (track != null) {
            player.prepare(track)
        }
        Log.v(TAG, "onPrepare with track=${track?.mediaEntity?.title}")
    }

    override fun onPlayFromMediaId(mediaId: String, extras: Bundle?) {
        Log.v(TAG, "onPlayFromMediaId mediaId=$mediaId, extras=$extras")

        when (val mediaId = MediaId.fromString(mediaId)) {
            MediaId.shuffleId() -> queue.handlePlayShuffle(mediaId)
            else -> queue.handlePlayFromMediaId(mediaId, extras)
        }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.computation())
            .doOnSubscribe { updatePodcastPosition() }
            .subscribe(player::play, Throwable::printStackTrace)
            .addTo(subscriptions)
    }

    override fun onPlay() {
        Log.v(TAG, "onPlay")
        player.resume()
    }

    override fun onPlayFromSearch(query: String, extras: Bundle) {
        Log.v(TAG, "onPlayFromSearch query=$query, extras=$extras")

        queue.handlePlayFromGoogleSearch(query, extras)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { updatePodcastPosition() }
            .subscribe(player::play, Throwable::printStackTrace)
            .addTo(subscriptions)
    }

    override fun onPlayFromUri(uri: Uri, extras: Bundle?) {
        Log.v(TAG, "onPlayFromUri uri=$uri, extras=$extras")

        queue.handlePlayFromUri(uri)
            .doOnSubscribe { updatePodcastPosition() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(player::play, Throwable::printStackTrace)
            .addTo(subscriptions)
    }

    override fun onPause() {
        Log.v(TAG, "onPause")
        updatePodcastPosition()
        player.pause(true)
    }

    override fun onStop() {
        Log.v(TAG, "onStop")
        onPause()
    }

    override fun onSkipToNext() {
        Log.v(TAG, "onSkipToNext")
        onSkipToNext(false)
    }

    override fun onSkipToPrevious() {
        Log.v(TAG, "onSkipToPrevious")

        updatePodcastPosition()
        queue.handleSkipToPrevious(player.getBookmark())?.let { metadata ->
            val skipType =
                if (player.getBookmark() < SKIP_TO_PREVIOUS_THRESHOLD) SkipType.SKIP_PREVIOUS else SkipType.RESTART
            player.playNext(metadata, skipType)
        }
    }

    private fun onTrackEnded() {
        Log.v(TAG, "onTrackEnded")
        onSkipToNext(true)
    }

    /**
     * Try to skip to next song, if can't, restart current and pause
     */
    private fun onSkipToNext(trackEnded: Boolean) {
        Log.v(TAG, "onSkipToNext internal track ended=$trackEnded")
        updatePodcastPosition()
        val metadata = queue.handleSkipToNext(trackEnded)
        if (metadata != null) {
            val skipType = if (trackEnded) SkipType.TRACK_ENDED else SkipType.SKIP_NEXT
            player.playNext(metadata, skipType)
        } else {
            val currentSong = queue.getPlayingSong()
            player.play(currentSong)
            player.pause(true)
            player.seekTo(0L)
        }
    }

    override fun onSkipToQueueItem(id: Long) {
        Log.v(TAG, "onSkipToQueueItem id=$id")

        updatePodcastPosition()
        val mediaEntity = queue.handleSkipToQueueItem(id)
        player.play(mediaEntity)
    }

    override fun onSeekTo(pos: Long) {
        Log.v(TAG, "onSeekTo pos=$pos")
        updatePodcastPosition()
        player.seekTo(pos)
    }

    override fun onSetRating(rating: RatingCompat?) {
        onSetRating(rating, null)
    }

    override fun onSetRating(rating: RatingCompat?, extras: Bundle?) {
        Log.v(TAG, "onSetRating rating=$rating, extras=$extras")
        toggleFavoriteUseCase.execute()
    }

    override fun onCustomAction(action: String, extras: Bundle?) {
        Log.v(TAG, "onSetRating rating=$action, extras=$extras")

        when (MusicServiceCustomAction.valueOf(action)) {
            MusicServiceCustomAction.SHUFFLE -> {
                requireNotNull(extras)
                val mediaId = extras.getString(MusicServiceCustomAction.ARGUMENT_MEDIA_ID)!!
                queue.handlePlayShuffle(MediaId.fromString(mediaId))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { updatePodcastPosition() }
                    .subscribe(player::play, Throwable::printStackTrace)
                    .addTo(subscriptions)
            }
            MusicServiceCustomAction.SWAP -> {
                requireNotNull(extras)
                val from = extras.getInt(MusicServiceCustomAction.ARGUMENT_SWAP_FROM, 0)
                val to = extras.getInt(MusicServiceCustomAction.ARGUMENT_SWAP_TO, 0)
                queue.handleSwap(from, to)
            }
            MusicServiceCustomAction.SWAP_RELATIVE -> {
                requireNotNull(extras)
                val from = extras.getInt(MusicServiceCustomAction.ARGUMENT_SWAP_FROM, 0)
                val to = extras.getInt(MusicServiceCustomAction.ARGUMENT_SWAP_TO, 0)
                queue.handleSwapRelative(from, to)
            }
            MusicServiceCustomAction.REMOVE -> {
                requireNotNull(extras)
                val position = extras.getInt(MusicServiceCustomAction.ARGUMENT_POSITION, -1)
                if (queue.handleRemove(position)) {
                    onStop()
                }
            }
            MusicServiceCustomAction.REMOVE_RELATIVE -> {
                requireNotNull(extras)
                val position = extras.getInt(MusicServiceCustomAction.ARGUMENT_POSITION, -1)
                if (queue.handleRemoveRelative(position)) {
                    onStop()
                }
            }
            MusicServiceCustomAction.PLAY_RECENTLY_ADDED -> {
                requireNotNull(extras)
                val mediaId = extras.getString(MusicServiceCustomAction.ARGUMENT_MEDIA_ID)!!
                queue.handlePlayRecentlyAdded(MediaId.fromString(mediaId))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { updatePodcastPosition() }
                    .subscribe(player::play, Throwable::printStackTrace)
                    .addTo(subscriptions)
            }
            MusicServiceCustomAction.PLAY_MOST_PLAYED -> {
                requireNotNull(extras)
                val mediaId = extras.getString(MusicServiceCustomAction.ARGUMENT_MEDIA_ID)!!
                queue.handlePlayMostPlayed(MediaId.fromString(mediaId))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { updatePodcastPosition() }
                    .subscribe(player::play, Throwable::printStackTrace)
                    .addTo(subscriptions)
            }
            MusicServiceCustomAction.FORWARD_10 -> player.forwardTenSeconds()
            MusicServiceCustomAction.FORWARD_30 -> player.forwardThirtySeconds()
            MusicServiceCustomAction.REPLAY_10 -> player.replayTenSeconds()
            MusicServiceCustomAction.REPLAY_30 -> player.replayThirtySeconds()
            MusicServiceCustomAction.TOGGLE_FAVORITE -> onSetRating(null)
        }
    }

    override fun onSetRepeatMode(repeatMode: Int) {
        Log.v(TAG, "onSetRepeatMode")

        this.repeatMode.update()
        playerState.toggleSkipToActions(queue.getCurrentPositionInQueue())
        queue.onRepeatModeChanged()
    }

    override fun onSetShuffleMode(unused: Int) {
        Log.v(TAG, "onSetShuffleMode")

        val newShuffleMode = this.shuffleMode.update()
        if (newShuffleMode) {
            queue.shuffle()
        } else {
            queue.sort()
        }
        playerState.toggleSkipToActions(queue.getCurrentPositionInQueue())
    }

    override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
        val event = mediaButtonIntent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
        Log.v(TAG, "onMediaButtonEvent, action=${event.action}, keycode=${event.keyCode}")
        if (event.action == KeyEvent.ACTION_DOWN) { // TODO or maybe is better action up??

            when (event.keyCode) {
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> handlePlayPause()
                KeyEvent.KEYCODE_MEDIA_NEXT -> onSkipToNext()
                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> onSkipToPrevious()
                KeyEvent.KEYCODE_MEDIA_STOP -> player.stopService()
                KeyEvent.KEYCODE_MEDIA_PAUSE -> player.pause(false)
                KeyEvent.KEYCODE_MEDIA_PLAY -> onPlay()
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> onTrackEnded()
                KeyEvent.KEYCODE_HEADSETHOOK -> mediaButton.onHeatSetHookClick()
                else -> throw IllegalArgumentException("not handled")
            }
        }

        return true
    }


    /**
    Play later
     */
    override fun onAddQueueItem(description: MediaDescriptionCompat) {
        Log.v(TAG, "onAddQueueItem, item=$description")

        val split = description.mediaId!!.split(",")
        val position = queue.playLater(
            split.map { it.trim().toLong() },
            description.extras!!.getBoolean(MusicConstants.IS_PODCAST)
        )
        playerState.toggleSkipToActions(position)
    }

    /**
    When [index] == [Int.MAX_VALUE] -> play next
    When [index] == [Int.MAX_VALUE-1] -> move to play next
     */
    override fun onAddQueueItem(description: MediaDescriptionCompat, index: Int) {
        Log.v(TAG, "onAddQueueItem, item=$description, index=$index")

        when (index) {
            Int.MAX_VALUE -> {
                // play next
                val split = description.mediaId!!.split(",")
                val position = queue.playNext(
                    split.map { it.trim().toLong() },
                    description.extras!!.getBoolean(MusicConstants.IS_PODCAST)
                )
                playerState.toggleSkipToActions(position)
            }
//            Int.MAX_VALUE - 1 -> {
//                // move to next
//                val split = description.mediaId!!.split(",")
//                val position = queue.moveToPlayNext(split.map { it.trim().toInt() }.first())
//                playerState.toggleSkipToActions(position)
//            }
        }
    }

    /**
     * this function DO NOT KILL service on pause
     */
    fun handlePlayPause() {
        Log.v(TAG, "handlePlayPause")

        if (player.isPlaying()) {
            player.pause(false)
        } else {
            onPlay()
        }
    }

    private fun updatePodcastPosition() {
        Log.v(TAG, "updatePodcastPosition")

        // TODO move somewhere else
        GlobalScope.launch {
            val bookmark = withContext(Dispatchers.Main) { player.getBookmark() }
            queue.updatePodcastPosition(bookmark)
        }
    }

}