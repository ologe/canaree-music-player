package dev.olog.music_service.interfaces

import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray

interface ExoPlayerListenerWrapper : Player.EventListener {

    override fun onRepeatModeChanged(repeatMode: Int) {}

    override fun onTimelineChanged(timeline: Timeline, manifest: Any) {}

    override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {}

    override fun onLoadingChanged(isLoading: Boolean) {}

    override fun onPositionDiscontinuity() {}

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}

}