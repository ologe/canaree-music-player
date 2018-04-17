package dev.olog.msc.music.service.player

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.media.AudioManager
import android.view.KeyEvent
import dagger.Lazy
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.music.service.equalizer.OnAudioSessionIdChangeListener
import dev.olog.msc.music.service.volume.IPlayerVolume
import dev.olog.msc.utils.k.extension.dispatchEvent
import javax.inject.Inject

class SimplePlayer @Inject constructor(
        @ApplicationContext context: Context,
        @ServiceLifecycle lifecycle: Lifecycle,
        mediaSourceFactory: MediaSourceFactory,
        volume: IPlayerVolume,
        private val audioManager: Lazy<AudioManager>,
        private val onAudioSessionIdChangeListener: OnAudioSessionIdChangeListener

): DefaultPlayer(context, lifecycle, mediaSourceFactory, volume), CustomExoPlayer {

    init {
        player.addListener(this)
        player.addAudioDebugListener(onAudioSessionIdChangeListener)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        player.removeListener(this)
        player.removeAudioDebugListener(onAudioSessionIdChangeListener)
        onAudioSessionIdChangeListener.release()
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == com.google.android.exoplayer2.Player.STATE_ENDED) {
//            audioManager.get().dispatchEvent(KeyEvent.KEYCODE_MEDIA_NEXT)
            audioManager.get().dispatchEvent(KeyEvent.KEYCODE_MEDIA_FAST_FORWARD)
        }
    }

}