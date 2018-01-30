package dev.olog.music_service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Intent
import android.os.Bundle
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
import dev.olog.shared.unsubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject
import kotlin.system.measureTimeMillis

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
    private var prepareDisposable: Disposable? = null

    init {
        lifecycle.addObserver(this)
        onPrepare()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        subscriptions.clear()
        prepareDisposable.unsubscribe()
    }

    override fun onPrepare() {
        prepareDisposable = queue.prepare()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(player::prepare, Throwable::printStackTrace)
    }

    override fun onPlayFromMediaId(mediaIdAsString: String, extras: Bundle?) {
        if (extras != null){
            val mediaId = MediaId.fromString(mediaIdAsString)

            when {
                extras.isEmpty ||
                        extras.getString(MusicConstants.ARGUMENT_SORT_TYPE) != null ||
                        extras.getString(MusicConstants.ARGUMENT_SORT_ARRANGING) != null -> {
                    queue.handlePlayFromMediaId(mediaId, extras)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(player::play, Throwable::printStackTrace)
                            .addTo(subscriptions)
                }
                extras.getBoolean(MusicConstants.BUNDLE_MOST_PLAYED, false) -> {
                    queue.handlePlayMostPlayed(mediaId)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(player::play, Throwable::printStackTrace)
                            .addTo(subscriptions)
                }
                extras.getBoolean(MusicConstants.BUNDLE_RECENTLY_PLAYED, false) -> {
                    queue.handlePlayRecentlyPlayed(mediaId)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(player::play, Throwable::printStackTrace)
                            .addTo(subscriptions)
                }
            }
        }
    }

    override fun onPlay() {
        doWhenReady {
            player.resume()
        }
    }

    override fun onPlayFromSearch(query: String, extras: Bundle) {
        queue.handlePlayFromGoogleSearch(query, extras)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(player::play, {
                    playerState.setEmptyQueue()
                    it.printStackTrace()
                })
                .addTo(subscriptions)
    }

    override fun onPause() {
        player.pause(true)
    }

    override fun onStop() {
        onPause()
    }

    override fun onSkipToNext() {
        measureTimeMillis {
            doWhenReady {
                val metadata = queue.handleSkipToNext()
                player.playNext(metadata, true)
            }
        }

    }

    override fun onSkipToPrevious() {
        doWhenReady {
            val metadata = queue.handleSkipToPrevious(player.getBookmark())
            player.playNext(metadata, false)
        }
    }

    private fun doWhenReady(action: () -> Unit){
        prepareDisposable.unsubscribe()
        if (queue.isReady()){
            action()
        } else {
            prepareDisposable = queue.prepare()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        player.prepare(it)
                        action()
                    }, Throwable::printStackTrace)
                    .addTo(subscriptions)
        }
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
        if (action != null){
            when (action){
                MusicConstants.ACTION_SWAP -> queue.handleSwap(extras!!)
                MusicConstants.ACTION_SWAP_RELATIVE -> queue.handleSwapRelative(extras!!)
                MusicConstants.ACTION_REMOVE -> queue.handleRemove(extras!!)
                MusicConstants.ACTION_REMOVE_RELATIVE -> queue.handleRemoveRelative(extras!!)
                MusicConstants.SKIP_TO_ITEM -> {
                    val mediaIdAsString = extras!!.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                    val mediaId = MediaId.fromString(mediaIdAsString)
                    val mediaEntity = queue.handleSkipToQueueItemWithIdInPlaylist(mediaId.leaf!!)
                    player.play(mediaEntity)
                }
                MusicConstants.ACTION_PLAY_SHUFFLE -> {
                    doWhenReady {
                        val mediaIdAsString = extras!!.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
                        val mediaId = MediaId.fromString(mediaIdAsString)
                        queue.handlePlayShuffle(mediaId)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(player::play, Throwable::printStackTrace)
                                .addTo(subscriptions)
                    }
                }
            }
        }
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
                KeyEvent.KEYCODE_MEDIA_PLAY -> onPlay()
                else -> mediaButton.onNextEvent(mediaButtonEvent)
            }
        }

        return true
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

}