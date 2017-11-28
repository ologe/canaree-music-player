package dev.olog.presentation.fragment_player

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.presentation.images.CoverUtils
import dev.olog.presentation.model.CoverModel
import dev.olog.presentation.model.DurationModel
import dev.olog.presentation.model.PlayerFragmentMetadata
import dev.olog.presentation.model.toPlayerMetadata
import dev.olog.presentation.music_service.RxMusicServiceControllerCallback
import dev.olog.presentation.utils.TextUtils.getReadableSongLength
import dev.olog.presentation.utils.asLiveData
import dev.olog.shared.TextUtils
import io.reactivex.functions.Predicate

class PlayerFragmentViewModel(
        private val controllerCallback: RxMusicServiceControllerCallback

) : ViewModel() {

    private var lastCoverPosition = 0

    private val filterPlaybackState : Predicate<Int> = Predicate { state ->
        state == PlaybackStateCompat.STATE_PAUSED || state == PlaybackStateCompat.STATE_PLAYING
    }

    val onMetadataChangedLiveData: LiveData<PlayerFragmentMetadata> = controllerCallback.onMetadataChanged()
            .map { it.toPlayerMetadata() }
            .asLiveData()

    fun onCoverChangedLiveData(context: Context): LiveData<CoverModel> {
        return controllerCallback.onMetadataChanged()
                .map { it.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI) }
                .map { cover ->
                    lastCoverPosition++
                    val drawable = TransitionDrawable(arrayOf(
                            CoverUtils.getGradient(context, lastCoverPosition - 1, 2),
                            CoverUtils.getGradient(context, lastCoverPosition, 2)
                    ))
                    CoverModel(cover, drawable)
                }.asLiveData()
    }

    val onPlaybackStateChangedLiveData: LiveData<Boolean> = controllerCallback
            .onPlaybackStateChanged()
            .map { it.state }
            .filter(filterPlaybackState)
            .map { state -> state == PlaybackStateCompat.STATE_PLAYING }
            .asLiveData()

    val onRepeatModeChangedLiveData: LiveData<Int> = controllerCallback
            .onRepeatModeChanged()
            .asLiveData()

    val onShuffleModeChangedLiveData : LiveData<Int> = controllerCallback
            .onShuffleModeChanged()
            .asLiveData()

    val onMaxChangedObservable: LiveData<DurationModel> = controllerCallback.onMetadataChanged()
            .map { it.getLong(MediaMetadataCompat.METADATA_KEY_DURATION) }
            .map { DurationModel(it.toInt(), TextUtils.MIDDLE_DOT_SPACED + getReadableSongLength(it)) }
            .asLiveData()


    val onBookmarkChangedObservable: LiveData<Int> = controllerCallback
            .onPlaybackStateChanged()
            .filter { playbackState ->
                val state = playbackState.state
                filterPlaybackState.test(state)
            }.map { it.position.toInt() }
            .asLiveData()

}