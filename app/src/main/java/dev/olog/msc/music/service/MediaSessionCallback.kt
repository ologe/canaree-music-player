package dev.olog.msc.music.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.RatingCompat
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.msc.R
import dev.olog.msc.constants.MusicConstants
import dev.olog.core.dagger.ApplicationContext
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.dagger.scope.PerService
import dev.olog.msc.domain.interactor.favorite.ToggleFavoriteUseCase
import dev.olog.msc.music.service.interfaces.Player
import dev.olog.msc.music.service.interfaces.Queue
import dev.olog.msc.music.service.interfaces.SkipType
import dev.olog.core.MediaId
import dev.olog.msc.utils.k.extension.toast
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

@PerService
class MediaSessionCallback @Inject constructor(
    @ApplicationContext private val context: Context,
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
                }
                extras.getBoolean(MusicConstants.BUNDLE_MOST_PLAYED, false) -> {
                    queue.handlePlayMostPlayed(mediaId)
                }
                extras.getBoolean(MusicConstants.BUNDLE_RECENTLY_PLAYED, false) -> {
                    queue.handlePlayRecentlyPlayed(mediaId)
                }
                else -> Single.error(Throwable("invalid case $extras"))
            }.observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { updatePodcastPosition() }
                    .subscribe(player::play, Throwable::printStackTrace)
                    .addTo(subscriptions)
        }
    }

    @Suppress("MoveLambdaOutsideParentheses")
    override fun onPlay() {
        doWhenReady ({
            player.resume()
        })
    }

    override fun onPlayFromSearch(query: String, extras: Bundle) {
        queue.handlePlayFromGoogleSearch(query, extras)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { updatePodcastPosition() }
                .subscribe(player::play) {
                    playerState.setEmptyQueue()
                    it.printStackTrace()
                }
                .addTo(subscriptions)
    }

    override fun onPlayFromUri(uri: Uri, extras: Bundle?) {
        queue.handlePlayFromUri(uri)
                .doOnSubscribe { updatePodcastPosition() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(player::play) {
                    playerState.setEmptyQueue()
                    it.printStackTrace()
                }
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
        doWhenReady ({
            updatePodcastPosition()
            queue.handleSkipToPrevious(player.getBookmark())?.let { metadata ->
                player.playNext(metadata, SkipType.SKIP_PREVIOUS)
            }
        }, { context.toast(R.string.popup_error_message) })
    }

    private fun onTrackEnded(){
        onSkipToNext(true)
    }

    /**
     * Try to skip to next song, if can't, restart current
     */
    private fun onSkipToNext(trackEnded: Boolean){
        doWhenReady ({
            updatePodcastPosition()
            val metadata = queue.handleSkipToNext(trackEnded)
            if (metadata != null){
                val skipType = if (trackEnded) SkipType.TRACK_ENDED else SkipType.SKIP_NEXT
                player.playNext(metadata, skipType)
            } else {
                val currentSong = queue.getPlayingSong()
                player.play(currentSong)
                player.pause(true)
                player.seekTo(0L)
            }
        }, { context.toast(R.string.popup_error_message) })
    }

    private fun doWhenReady(action: () -> Unit, error: (() -> Unit)? = null){
        prepareDisposable.unsubscribe()
        if (queue.isReady()){
            try {
                action()
            } catch (ex: Exception){
                error?.invoke()
            }
        } else {
            prepareDisposable = queue.prepare()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        try {
                            player.prepare(it)
                            action()
                        } catch (ex: Exception){
                            error?.invoke()
                        }
                    }, Throwable::printStackTrace)
                    .addTo(subscriptions)
        }
    }

    override fun onSkipToQueueItem(id: Long) {
        try {
            updatePodcastPosition()
            val mediaEntity = queue.handleSkipToQueueItem(id)
            player.play(mediaEntity)
        } catch (ex: Exception){}

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

    @Suppress("MoveLambdaOutsideParentheses")
    override fun onCustomAction(action: String?, extras: Bundle?) {
        if (action != null){
            when (action){
                MusicConstants.ACTION_SWAP -> queue.handleSwap(extras!!)
                MusicConstants.ACTION_SWAP_RELATIVE -> queue.handleSwapRelative(extras!!)
                MusicConstants.ACTION_REMOVE -> {
                    if (queue.handleRemove(extras!!)) {
                        onStop()
                    }
                }
                MusicConstants.ACTION_REMOVE_RELATIVE -> {
                    if (queue.handleRemoveRelative(extras!!)){
                        onStop()
                    }
                }
                MusicConstants.ACTION_SHUFFLE -> {
                    doWhenReady ({
                        updatePodcastPosition()
                        val mediaIdAsString = extras!!.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)!!
                        val mediaId = MediaId.fromString(mediaIdAsString)
                        queue.handlePlayShuffle(mediaId)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(player::play, Throwable::printStackTrace)
                                .addTo(subscriptions)
                    })
                }
                MusicConstants.ACTION_FORWARD_10_SECONDS -> player.forwardTenSeconds()
                MusicConstants.ACTION_REPLAY_10_SECONDS -> player.replayTenSeconds()
                MusicConstants.ACTION_FORWARD_30_SECONDS -> player.forwardThirtySeconds()
                MusicConstants.ACTION_REPLAY_30_SECONDS -> player.replayThirtySeconds()
            }
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
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> onTrackEnded()
                else -> mediaButton.onNextEvent(mediaButtonEvent)
            }
        }

        return true
    }


    /**
        Play later
     */
    override fun onAddQueueItem(description: MediaDescriptionCompat) {

        val split = description.mediaId!!.split(",")
        val position = queue.playLater(split.map { it.trim().toLong() },
                description.extras!!.getBoolean(MusicConstants.IS_PODCAST))
        playerState.toggleSkipToActions(position)
    }

    /**
        When [index] == [Int.MAX_VALUE] -> play next
        When [index] == [Int.MAX_VALUE-1] -> move to play next
     */
    override fun onAddQueueItem(description: MediaDescriptionCompat, index: Int) {
        when (index){
            Int.MAX_VALUE -> {
                // play next
                val split = description.mediaId!!.split(",")
                val position = queue.playNext(split.map { it.trim().toLong() },
                        description.extras!!.getBoolean(MusicConstants.IS_PODCAST))
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

    private fun updatePodcastPosition(){
        queue.updatePodcastPosition(player.getBookmark())
    }

}