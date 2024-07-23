package dev.olog.presentation.player.ui

import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import dev.olog.media.widget.CustomSeekBar
import dev.olog.presentation.databinding.PlayerLayoutBigImageBinding
import dev.olog.presentation.databinding.PlayerLayoutCleanBinding
import dev.olog.presentation.databinding.PlayerLayoutDefaultBinding
import dev.olog.presentation.databinding.PlayerLayoutFlatBinding
import dev.olog.presentation.databinding.PlayerLayoutFullscreenBinding
import dev.olog.presentation.databinding.PlayerLayoutMiniBinding
import dev.olog.presentation.databinding.PlayerLayoutSpotifyBinding
import dev.olog.presentation.widgets.LottieFavorite
import dev.olog.presentation.widgets.RepeatButton
import dev.olog.presentation.widgets.ShuffleButton
import dev.olog.presentation.widgets.imageview.PlayerImageView
import dev.olog.presentation.widgets.swipeableview.SwipeableView
import dev.olog.presentation.widgets.switcher.CustomViewSwitcher
import dev.olog.shared.widgets.AnimatedImageView
import dev.olog.shared.widgets.playpause.AnimatedPlayPauseImageView

class PlayerBindings(
    val root: View,
    val repeat: RepeatButton,
    val shuffle: ShuffleButton,
    val favorite: LottieFavorite,
    val lyrics: ImageButton,
    val next: AnimatedImageView,
    val playPause: AnimatedPlayPauseImageView,
    val previous: AnimatedImageView,
    val replay: View,
    val replay30: View,
    val forward: View,
    val forward30: View,
    val playbackSpeed: View,
    val seekBar: CustomSeekBar,
    val bookmark: TextView,
    val more: ImageButton,
    val title: TextView,
    val artist: TextView,
    val duration: TextView,
    val playerControls: View?,
    val podcastControls: View,
    val imageSwitcher: CustomViewSwitcher?,
    val miniCover: PlayerImageView?,
    val nowPlaying: View?,
    val swipeableView: SwipeableView?,
    val volume: View?,
) {

    val context: Context
        get() = root.context

}

fun PlayerLayoutDefaultBinding.bindPlayer() = PlayerBindings(
    root = root,
    repeat = playerControls.repeat,
    shuffle = playerControls.shuffle,
    favorite = playerToolbar.favorite,
    lyrics = playerToolbar.lyrics,
    next = playerControls.next,
    playPause = playerControls.playPause,
    previous = playerControls.previous,
    replay = playerControls.replay,
    replay30 = playerControls.replay30,
    forward = playerControls.forward,
    forward30 = playerControls.forward30,
    playbackSpeed = playerToolbar.playbackSpeed,
    seekBar = seekBar,
    bookmark = bookmark,
    more = more,
    title = title,
    artist = artist,
    duration = duration,
    playerControls = playerControls.player,
    podcastControls = playerControls.podcastControls,
    imageSwitcher = switcher.imageSwitcher,
    miniCover = null,
    nowPlaying = playerToolbar.nowPlaying,
    swipeableView = swipeableView,
    volume = playerToolbar.volume,
)

fun PlayerLayoutFlatBinding.bindPlayer() = PlayerBindings(
    root = root,
    repeat = playerControls.repeat,
    shuffle = playerControls.shuffle,
    favorite = playerToolbar.favorite,
    lyrics = playerToolbar.lyrics,
    next = playerControls.next,
    playPause = playerControls.playPause,
    previous = playerControls.previous,
    replay = playerControls.replay,
    replay30 = playerControls.replay30,
    forward = playerControls.forward,
    forward30 = playerControls.forward30,
    playbackSpeed = playerToolbar.playbackSpeed,
    seekBar = seekBar,
    bookmark = bookmark,
    more = more,
    title = title,
    artist = artist,
    duration = duration,
    playerControls = playerControls.player,
    podcastControls = playerControls.podcastControls,
    imageSwitcher = switcher.imageSwitcher,
    miniCover = null,
    nowPlaying = null,
    swipeableView = swipeableView,
    volume = playerToolbar.volume,
)

fun PlayerLayoutSpotifyBinding.bindPlayer() = PlayerBindings(
    root = root,
    repeat = playerControls.repeat,
    shuffle = playerControls.shuffle,
    favorite = favorite,
    lyrics = playerToolbar.lyrics,
    next = playerControls.next,
    playPause = playerControls.playPause,
    previous = playerControls.previous,
    replay = playerControls.replay,
    replay30 = playerControls.replay30,
    forward = playerControls.forward,
    forward30 = playerControls.forward30,
    playbackSpeed = playerToolbar.playbackSpeed,
    seekBar = seekBar,
    bookmark = bookmark,
    more = more,
    title = title,
    artist = artist,
    duration = duration,
    playerControls = playerControls.root,
    podcastControls = playerControls.podcastControls,
    imageSwitcher = switcher.imageSwitcher,
    miniCover = null,
    nowPlaying = null,
    swipeableView = swipeableView,
    volume = playerToolbar.volume,
)

fun PlayerLayoutFullscreenBinding.bindPlayer() = PlayerBindings(
    root = root,
    repeat = playerControls.repeat,
    shuffle = playerControls.shuffle,
    favorite = favorite,
    lyrics = playerToolbar.lyrics,
    next = playerControls.next,
    playPause = playerControls.playPause,
    previous = playerControls.previous,
    replay = playerControls.replay,
    replay30 = playerControls.replay30,
    forward = playerControls.forward,
    forward30 = playerControls.forward30,
    playbackSpeed = playerToolbar.playbackSpeed,
    seekBar = seekBar,
    bookmark = bookmark,
    more = more,
    title = title,
    artist = artist,
    duration = duration,
    playerControls = null,
    podcastControls = playerControls.podcastControls,
    imageSwitcher = switcher.imageSwitcher,
    miniCover = null,
    nowPlaying = null,
    swipeableView = swipeableView,
    volume = playerToolbar.volume,
)

fun PlayerLayoutBigImageBinding.bindPlayer() = PlayerBindings(
    root = root,
    repeat = repeat,
    shuffle = shuffle,
    favorite = favorite,
    lyrics = lyrics,
    next = playerControls.next,
    playPause = playerControls.playPause,
    previous = playerControls.previous,
    replay = playerControls.replay,
    replay30 = playerControls.replay30,
    forward = playerControls.forward,
    forward30 = playerControls.forward30,
    playbackSpeed = playbackSpeed,
    seekBar = seekBar,
    bookmark = bookmark,
    more = more,
    title = title,
    artist = artist,
    duration = duration,
    playerControls = null,
    podcastControls = playerControls.podcastControls,
    imageSwitcher = switcher.imageSwitcher,
    miniCover = null,
    nowPlaying = null,
    swipeableView = swipeableView,
    volume = null, // TODO add
)

fun PlayerLayoutCleanBinding.bindPlayer() = PlayerBindings(
    root = root,
    repeat = playerControls.repeat,
    shuffle = playerControls.shuffle,
    favorite = favorite,
    lyrics = lyrics,
    next = playerControls.next,
    playPause = playerControls.playPause,
    previous = playerControls.previous,
    replay = playerControls.replay,
    replay30 = playerControls.replay30,
    forward = playerControls.forward,
    forward30 = playerControls.forward30,
    playbackSpeed = playbackSpeed,
    seekBar = seekBar,
    bookmark = bookmark,
    more = more,
    title = title,
    artist = artist,
    duration = duration,
    playerControls = playerControls.player,
    podcastControls = playerControls.podcastControls,
    imageSwitcher = switcher.imageSwitcher,
    miniCover = null,
    nowPlaying = null,
    swipeableView = swipeableView,
    volume = null, // TODO add
)

fun PlayerLayoutMiniBinding.bindPlayer() = PlayerBindings(
    root = root,
    repeat = playerControls.repeat,
    shuffle = playerControls.shuffle,
    favorite = favorite,
    lyrics = lyrics,
    next = playerControls.next,
    playPause = playerControls.playPause,
    previous = playerControls.previous,
    replay = playerControls.replay,
    replay30 = playerControls.replay30,
    forward = playerControls.forward,
    forward30 = playerControls.forward30,
    playbackSpeed = playbackSpeed,
    seekBar = seekBar,
    bookmark = bookmark,
    more = more,
    title = title,
    artist = artist,
    duration = duration,
    playerControls = null,
    podcastControls = playerControls.podcastControls,
    imageSwitcher = null,
    miniCover = miniCover,
    nowPlaying = null,
    swipeableView = null,
    volume = volume,
)