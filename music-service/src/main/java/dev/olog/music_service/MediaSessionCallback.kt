package dev.olog.music_service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import dev.olog.domain.interactor.favorite.ToggleFavoriteUseCase
import dev.olog.music_service.di.PerService
import dev.olog.music_service.di.ServiceLifecycle
import dev.olog.music_service.interfaces.Player
import dev.olog.music_service.interfaces.Queue
import dev.olog.shared.MediaId
import dev.olog.shared.constants.MusicConstants
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

@PerService
class MediaSessionCallback @Inject constructor(
        @ServiceLifecycle lifecycle: Lifecycle,
        private val queue: Queue,
        private val player: Player,
        private val repeatMode: RepeatMode,
        private val shuffleMode: ShuffleMode,
        private val mediaButton: MediaButton,
        private val playerState: PlayerState,
        private val toggleFavoriteUseCase: ToggleFavoriteUseCase

): MediaSessionCompat.Callback(), DefaultLifecycleObserver {

    private val subscriptions = CompositeDisposable()

    init {
        lifecycle.addObserver(this)
        onPrepare()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        subscriptions.clear()
    }

    override fun onPrepare() {
        queue.prepare()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(player::prepare, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    override fun onPlayFromMediaId(mediaIdAsString: String, extras: Bundle?) {
        if (extras != null){
            val mid = MediaId.fromString(mediaIdAsString)

            when {
                extras.isEmpty -> {
                    queue.handlePlayFromMediaId(mid)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(player::play, Throwable::printStackTrace)
                            .addTo(subscriptions)
                }
                extras.getBoolean(MusicConstants.BUNDLE_MOST_PLAYED, false) -> {
                    queue.handlePlayMostPlayed(mid)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(player::play, Throwable::printStackTrace)
                            .addTo(subscriptions)
                }
                extras.getBoolean(MusicConstants.BUNDLE_RECENTLY_PLAYED, false) -> {
                    queue.handlePlayRecentlyPlayed(mid)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(player::play, Throwable::printStackTrace)
                            .addTo(subscriptions)
                }
            }
        }
    }

    override fun onPlay() {
        player.resume()
    }

    override fun onPause() {
        player.pause(true)
    }

    override fun onSkipToNext() {
        val metadata = queue.handleSkipToNext()
        player.playNext(metadata, true)
    }

    override fun onSkipToPrevious() {
        val metadata = queue.handleSkipToPrevious(player.getBookmark())
        player.playNext(metadata, false)
    }

    override fun onSkipToQueueItem(id: Long) {
        val mediaEntity = queue.handleSkipToQueueItem(id)
        player.play(mediaEntity)
    }

    override fun onSeekTo(pos: Long) {
        player.seekTo(pos)
    }

    override fun onSetRating(rating: RatingCompat?, extras: Bundle?) {
        val songId = extras!!.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID).toLong()
        toggleFavoriteUseCase.execute(songId)
    }

    override fun onCustomAction(action: String?, extras: Bundle?) {
        val single = when (action) {
            MusicConstants.ACTION_PLAY_SHUFFLE -> {
                val mediaIdAsString = extras!!.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                val mediaId = MediaId.fromString(mediaIdAsString)
                queue.handlePlayShuffle(mediaId)
            }
            else -> Single.error(Throwable())
        }

        single.observeOn(AndroidSchedulers.mainThread())
                .subscribe(player::play, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    override fun onSetRepeatMode(repeatMode: Int) {
        this.repeatMode.update()
        playerState.toggleSkipToActions(queue.getCurrentPositionInQueue())
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

    override fun onAddQueueItem(description: MediaDescriptionCompat) {
        queue.addItemToQueue(description)
    }

    override fun onMediaButtonEvent(mediaButtonEvent: Intent): Boolean {
        val event = mediaButtonEvent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)

        if (event.action == KeyEvent.ACTION_DOWN) {
            val keyCode = event.keyCode

            when (keyCode) {
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> handlePlayPause()
                KeyEvent.KEYCODE_MEDIA_NEXT -> onSkipToNext()
                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> onSkipToPrevious()
                KeyEvent.KEYCODE_MEDIA_STOP -> player.stopService()
                KeyEvent.KEYCODE_MEDIA_PAUSE -> player.pause(false)
                else -> mediaButton.onNextEvent(mediaButtonEvent)
            }
        }

        return true
    }

    /**
     * DO NOT KILL service on pause
     */
    private fun handlePlayPause() {
        if (player.isPlaying()) {
            player.pause(false)
        } else {
            onPlay()
        }
    }

}