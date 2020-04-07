package dev.olog.presentation.playermini

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import dev.olog.lib.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.shared.lazyFast
import dev.olog.shared.widgets.playpause.AnimatedPlayPauseImageView
import kotlinx.android.synthetic.main.fragment_mini_player_buttons_podcast.view.*
import kotlinx.android.synthetic.main.fragment_mini_player_buttons_song.view.*

class MiniPlayerButtons(
    context: Context,
    attrs: AttributeSet
) : FrameLayout(context, attrs) {

    private val mediaProvider by lazyFast { context as MediaProvider }
    private val transition by lazyFast {
        AutoTransition().apply {
            duration = 150
        }
    }

    private val playPauseSong by lazyFast {
        rootSong.findViewById<AnimatedPlayPauseImageView>(R.id.playPause)
    }
    private val playPausePodcast by lazyFast {
        rootPodcast.findViewById<AnimatedPlayPauseImageView>(R.id.playPause)
    }

    init {
        View.inflate(context, R.layout.fragment_mini_player_buttons_song, this)
        View.inflate(context, R.layout.fragment_mini_player_buttons_podcast, this)

        if (isInEditMode) {
            rootPodcast.isVisible = false
        } else {
            rootSong.isVisible = false
            rootPodcast.isVisible = false
        }
        playPauseSong.setOnClickListener {
            mediaProvider.playPause()
        }
        playPausePodcast.setOnClickListener {
            mediaProvider.playPause()
        }
        next.setOnClickListener {
            mediaProvider.skipToNext()
        }
        previous.setOnClickListener {
            mediaProvider.skipToPrevious()
        }
        forward30.setOnClickListener {
            mediaProvider.forwardThirtySeconds()
        }
        replay.setOnClickListener {
            mediaProvider.replayTenSeconds()
        }
    }

    fun toggleNextButton(show: Boolean) {
        next.updateVisibility(show)
    }

    fun togglePreviousButton(show: Boolean) {
        previous.updateVisibility(show)
    }

    fun startPlayAnimation(animate: Boolean) {
        playPauseSong.animationPlay(animate)
        playPausePodcast.animationPlay(animate)
    }

    fun startPauseAnimation(animate: Boolean) {
        playPauseSong.animationPause(animate)
        playPausePodcast.animationPause(animate)
    }

    fun startSkipNextAnimation() {
        next.playAnimation()
    }

    fun startSkipPreviousAnimation() {
        previous.playAnimation()
    }

    fun onTrackChanged(isPodcast: Boolean) {
        TransitionManager.beginDelayedTransition(this, transition)
        if (isPodcast) {
            showForPodcast()
        } else {
            showForSong()
        }
    }

    private fun showForPodcast() {
        rootSong.isVisible = false
        rootPodcast.isVisible = true
    }

    private fun showForSong() {
        rootSong.isVisible = true
        rootPodcast.isVisible = false
    }

}