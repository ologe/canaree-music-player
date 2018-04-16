package dev.olog.msc.music.service.player

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.media.AudioManager
import android.util.Log
import android.view.KeyEvent
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import dagger.Lazy
import dev.olog.msc.BuildConfig
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.music.service.interfaces.ExoPlayerListenerWrapper
import dev.olog.msc.utils.assertMainThread
import dev.olog.msc.utils.k.extension.dispatchEvent
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.disposables.Disposable

private enum class CurrentPlayer {
    PLAYER_NOT_SET,
    PLAYER_ONE,
    PLAYER_TWO
}

class CrossFadePlayer (
        @ApplicationContext context: Context,
        lifecycle: Lifecycle,
        mediaSourceFactory: MediaSourceFactory,
        private val audioManager: Lazy<AudioManager>

): CustomExoPlayer {

    private val playerOne = SimpleCrossfadePlayer(context, lifecycle, mediaSourceFactory)
    private val playerTwo = SimpleCrossfadePlayer(context, lifecycle, mediaSourceFactory)

    private var current = CurrentPlayer.PLAYER_NOT_SET

    override fun prepare(songId: Long, bookmark: Long){
        assertMainThread()

        val player = getNextPlayer()
        player.prepare(songId, bookmark)
    }

    override fun crossFade() {
        getCurrentPlayer().fadeOut()
        audioManager.get().dispatchEvent(KeyEvent.KEYCODE_MEDIA_FAST_FORWARD)
    }

    override fun play(songId: Long, hasFocus: Boolean) {
        assertMainThread()
        val player = getNextPlayer()
        player.play(songId)
        player.fadeIn()

        getSecondaryPlayer().stop()
    }

    override fun resume() {
        getCurrentPlayer().resume()
    }

    override fun pause() {
        getCurrentPlayer().pause()
    }

    override fun seekTo(where: Long) {
        getCurrentPlayer().seekTo(where)
    }

    override fun isPlaying(): Boolean = getCurrentPlayer().isPlaying()
    override fun getBookmark(): Long = getCurrentPlayer().getBookmark()
    override fun getDuration(): Long = getCurrentPlayer().getDuration()

    override fun setVolume(volume: Float) {
        getCurrentPlayer().setVolume(volume)
        getSecondaryPlayer().setVolume(volume)
    }

    private fun getNextPlayer(): SimpleCrossfadePlayer {
        current = when (current){
            CurrentPlayer.PLAYER_NOT_SET,
            CurrentPlayer.PLAYER_TWO -> CurrentPlayer.PLAYER_ONE
            CurrentPlayer.PLAYER_ONE -> CurrentPlayer.PLAYER_TWO
        }

        return when (current){
            CurrentPlayer.PLAYER_ONE -> playerOne
            CurrentPlayer.PLAYER_TWO -> playerTwo
            else -> throw IllegalStateException("invalid current player")
        }
    }

    private fun getCurrentPlayer(): SimpleCrossfadePlayer {
        return when (current){
            CurrentPlayer.PLAYER_ONE -> playerOne
            CurrentPlayer.PLAYER_TWO -> playerTwo
            else -> throw IllegalStateException("invalid secondary player")
        }
    }

    private fun getSecondaryPlayer(): SimpleCrossfadePlayer{
        return when (current){
            CurrentPlayer.PLAYER_ONE -> playerTwo
            CurrentPlayer.PLAYER_TWO -> playerOne
            else -> throw IllegalStateException("invalid secondary player")
        }
    }

}

class SimpleCrossfadePlayer(
        context: Context,
        lifecycle: Lifecycle,
        private val mediaSourceFactory: MediaSourceFactory

): DefaultLifecycleObserver, ExoPlayerListenerWrapper {

    private val trackSelector = DefaultTrackSelector()
    private val player = ExoPlayerFactory.newSimpleInstance(context, trackSelector)

    private var isFadingOut = false
    private var fadeOutDisposable : Disposable? = null

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        fadeOutDisposable.unsubscribe()
    }

    fun prepare(songId: Long, bookmark: Long){
        val mediaSource = mediaSourceFactory.get(songId)
        player.prepare(mediaSource)
        player.seekTo(bookmark)
    }

    fun play(songId: Long){
        val mediaSource = mediaSourceFactory.get(songId)
        player.prepare(mediaSource, true, true)
        player.playWhenReady = true
    }

    fun resume(){
        player.playWhenReady = false
    }

    fun pause(){
        fadeOutDisposable.unsubscribe()
        isFadingOut = false
        player.playWhenReady = true
    }

    fun seekTo(where: Long){
        player.seekTo(where)
    }

    fun isPlaying(): Boolean = player.playWhenReady

    fun getBookmark(): Long = player.currentPosition

    fun getDuration(): Long = player.duration

    fun setVolume(volume: Float){
        player.volume = volume
    }

    fun stop(){
        fadeOutDisposable.unsubscribe()
        isFadingOut = false
        player.stop()
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        fadeOutDisposable.unsubscribe()
        isFadingOut = false

        val what = when (error.type) {
            ExoPlaybackException.TYPE_SOURCE -> error.sourceException.message
            ExoPlaybackException.TYPE_RENDERER -> error.rendererException.message
            ExoPlaybackException.TYPE_UNEXPECTED -> error.unexpectedException.message
            else -> "Unknown: $error"
        }

        if (BuildConfig.DEBUG) {
            Log.e("Player", "onPlayerError $what")
        }
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState){
            Player.STATE_ENDED -> {
                isFadingOut = false
                player.stop()
            }
        }
    }

    fun fadeOut(){
        isFadingOut = true
        fadeOutDisposable.unsubscribe()
    }

    fun fadeIn() {

    }

}