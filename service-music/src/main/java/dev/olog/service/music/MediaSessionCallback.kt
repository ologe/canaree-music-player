package dev.olog.service.music

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import dev.olog.core.MediaId
import dev.olog.core.interactor.ToggleFavoriteUseCase
import dev.olog.injection.dagger.PerService
import dev.olog.service.music.interfaces.Player
import dev.olog.service.music.interfaces.Queue
import dev.olog.service.music.model.SkipType
import dev.olog.service.music.queue.SKIP_TO_PREVIOUS_THRESHOLD
import dev.olog.shared.MusicConstants
import dev.olog.shared.MusicServiceAction
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException
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

    private val subscriptions = CompositeDisposable()

    override fun onPrepare() = runBlocking<Unit> {
        val track = queue.prepare()
        if (track != null){
            player.prepare(track)
        }
    }

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        mediaId ?: return
        extras ?: return

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
        player.resume()
    }

    override fun onPlayFromSearch(query: String, extras: Bundle) {
        queue.handlePlayFromGoogleSearch(query, extras)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { updatePodcastPosition() }
            .subscribe(player::play, Throwable::printStackTrace)
            .addTo(subscriptions)
    }

    override fun onPlayFromUri(uri: Uri, extras: Bundle?) {
        queue.handlePlayFromUri(uri)
            .doOnSubscribe { updatePodcastPosition() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(player::play, Throwable::printStackTrace)
            .addTo(subscriptions)
    }

    override fun onPause() {
        updatePodcastPosition()
        player.pause(true)
    }

    override fun onStop() {
        onPause()
    }

    override fun onSkipToNext() {
        onSkipToNext(false)
    }

    override fun onSkipToPrevious() {
        updatePodcastPosition()
        queue.handleSkipToPrevious(player.getBookmark())?.let { metadata ->
            val skipType = if (player.getBookmark() < SKIP_TO_PREVIOUS_THRESHOLD) SkipType.SKIP_PREVIOUS else SkipType.RESTART
            player.playNext(metadata, skipType)
        }
    }

    private fun onTrackEnded() {
        onSkipToNext(true)
    }

    /**
     * Try to skip to next song, if can't, restart current and pause
     */
    private fun onSkipToNext(trackEnded: Boolean) {
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
        try {
            updatePodcastPosition()
            val mediaEntity = queue.handleSkipToQueueItem(id)
            player.play(mediaEntity)
        } catch (ex: Exception) {
        }

    }

    override fun onSeekTo(pos: Long) {
        updatePodcastPosition()
        player.seekTo(pos)
    }

    override fun onSetRating(rating: RatingCompat?) {
        onSetRating(rating, null)
    }

    override fun onSetRating(rating: RatingCompat?, extras: Bundle?) {
        toggleFavoriteUseCase.execute()
    }

    override fun onCustomAction(action: String?, extras: Bundle?) {
        action ?: return
        extras ?: return

        val musicAction = MusicServiceAction.valueOfOrNull(action)

        when (musicAction){
            MusicServiceAction.SHUFFLE -> {
                val mediaId = extras.getString(MusicServiceAction.ARGUMENT_MEDIA_ID)!!
                queue.handlePlayShuffle(MediaId.fromString(mediaId))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { updatePodcastPosition() }
                    .subscribe(player::play, Throwable::printStackTrace)
                    .addTo(subscriptions)
            }
            MusicServiceAction.SWAP -> {
                val from = extras.getInt(MusicServiceAction.ARGUMENT_SWAP_FROM, 0)
                val to = extras.getInt(MusicServiceAction.ARGUMENT_SWAP_TO, 0)
                queue.handleSwap(from, to)
            }
            MusicServiceAction.SWAP_RELATIVE -> {
                val from = extras.getInt(MusicServiceAction.ARGUMENT_SWAP_FROM, 0)
                val to = extras.getInt(MusicServiceAction.ARGUMENT_SWAP_TO, 0)
                queue.handleSwapRelative(from, to)
            }
            MusicServiceAction.REMOVE -> {
                val position = extras.getInt(MusicServiceAction.ARGUMENT_POSITION, -1)
                if (queue.handleRemove(position)) {
                    onStop()
                }
            }
            MusicServiceAction.REMOVE_RELATIVE -> {
                val position = extras.getInt(MusicServiceAction.ARGUMENT_POSITION, -1)
                if (queue.handleRemoveRelative(position)) {
                    onStop()
                }
            }
            MusicServiceAction.PLAY_RECENTLY_ADDED -> {
                val mediaId = extras.getString(MusicServiceAction.ARGUMENT_MEDIA_ID)!!
                queue.handlePlayRecentlyAdded(MediaId.fromString(mediaId))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { updatePodcastPosition() }
                    .subscribe(player::play, Throwable::printStackTrace)
                    .addTo(subscriptions)
            }
            MusicServiceAction.PLAY_MOST_PLAYED -> {
                val mediaId = extras.getString(MusicServiceAction.ARGUMENT_MEDIA_ID)!!
                queue.handlePlayMostPlayed(MediaId.fromString(mediaId))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { updatePodcastPosition() }
                    .subscribe(player::play, Throwable::printStackTrace)
                    .addTo(subscriptions)
            }
            MusicServiceAction.FORWARD_10 -> player.forwardTenSeconds()
            MusicServiceAction.FORWARD_30 -> player.forwardThirtySeconds()
            MusicServiceAction.REPLAY_10 -> player.replayTenSeconds()
            MusicServiceAction.REPLAY_30 -> player.replayThirtySeconds()
        }
    }

    override fun onSetRepeatMode(repeatMode: Int) {
        this.repeatMode.update()
        playerState.toggleSkipToActions(queue.getCurrentPositionInQueue())
        queue.onRepeatModeChanged()
    }

    override fun onSetShuffleMode(unused: Int) {
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
        if (player.isPlaying()) {
            player.pause(false)
        } else {
            onPlay()
        }
    }

    private fun updatePodcastPosition() {
        // TODO move somewhere else
        GlobalScope.launch {
            val bookmark = withContext(Dispatchers.Main){ player.getBookmark() }
            queue.updatePodcastPosition(bookmark)
        }
    }

}