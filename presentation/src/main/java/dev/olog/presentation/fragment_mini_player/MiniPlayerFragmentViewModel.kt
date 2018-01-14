package dev.olog.presentation.fragment_mini_player

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.domain.interactor.music_service.ToggleSkipToNextVisibilityUseCase
import dev.olog.domain.interactor.music_service.ToggleSkipToPreviousVisibilityUseCase
import dev.olog.presentation.fragment_mini_player.model.MiniPlayerMedatata
import dev.olog.presentation.fragment_mini_player.model.toMiniPlayerMetadata
import dev.olog.shared_android.extension.asLiveData
import dev.olog.shared_android.music_service.RxMusicServiceControllerCallback

class MiniPlayerFragmentViewModel(
        controllerCallback: RxMusicServiceControllerCallback,
        toggleSkipToPreviousVisibilityUseCase: ToggleSkipToPreviousVisibilityUseCase,
        toggleSkipToNextVisibilityUseCase: ToggleSkipToNextVisibilityUseCase

) : ViewModel() {

    val isPlayingSingle = controllerCallback.onPlaybackStateChanged()
            .map { it.playbackState == PlaybackStateCompat.STATE_PLAYING }
            .firstOrError()

    val onMetadataChangedLiveData: LiveData<MiniPlayerMedatata> = controllerCallback
            .onMetadataChanged()
            .map { it.toMiniPlayerMetadata() }
            .distinctUntilChanged()
            .asLiveData()

    val animatePlayPauseLiveData: LiveData<Int> = controllerCallback
            .onPlaybackStateChanged()
            .map { it.state }
            .filter { state -> state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_PAUSED }
            .distinctUntilChanged()
            .skip(1)
            .asLiveData()

    val skipToNextVisibility = toggleSkipToNextVisibilityUseCase.observe().asLiveData()
    val skipToPreviousVisibility = toggleSkipToPreviousVisibilityUseCase.observe().asLiveData()

    val animateSkipToLiveData: LiveData<Boolean> = controllerCallback.onPlaybackStateChanged()
            .map { it.state }
            .filter { state -> state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT || state == PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS }
            .map { state -> state == PlaybackStateCompat.STATE_SKIPPING_TO_NEXT }
            .asLiveData()

    val onBookmarkChangedLiveData: LiveData<Long> = controllerCallback.onPlaybackStateChanged()
                .filter { playbackState ->
                    val state = playbackState.state
                    state == PlaybackStateCompat.STATE_PAUSED || state == PlaybackStateCompat.STATE_PLAYING
                }.map { it.position }
                .asLiveData()

    val onMaxChangedLiveData: LiveData<Long> = controllerCallback
            .onMetadataChanged()
            .map { metadata -> metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION) }
            .asLiveData()

    val handleProgressBarLiveData: LiveData<Boolean> = controllerCallback.onPlaybackStateChanged()
            .map { it.state }
            .filter { state -> state == PlaybackStateCompat.STATE_PAUSED || state == PlaybackStateCompat.STATE_PLAYING }
            .map { state -> state == PlaybackStateCompat.STATE_PLAYING }
            .distinctUntilChanged()
            .asLiveData()

}