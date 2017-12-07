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
import dev.olog.presentation.service_music.RxMusicServiceControllerCallback
import dev.olog.presentation.utils.TextUtils.getReadableSongLength
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.shared.TextUtils
import dev.olog.shared.constants.MetadataConstants
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
            .distinctUntilChanged()
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
            .distinctUntilChanged()
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

    val onFavoriteStateChangedObservable: LiveData<Boolean> = controllerCallback.onMetadataChanged()
                .map { it.getRating(MediaMetadataCompat.METADATA_KEY_USER_RATING) }
                .map { it.hasHeart() }
                .asLiveData()

    val onFavoriteAnimateRequestObservable: LiveData<Boolean> = controllerCallback.onExtrasChanged()
                .map { bundle ->
                    val animate = bundle.getInt(MetadataConstants.IS_FAVORITE, MetadataConstants.NOT_ANIMATE)
                    bundle.putInt(MetadataConstants.IS_FAVORITE, MetadataConstants.NOT_ANIMATE)
                    animate
                }
                .filter { it != MetadataConstants.NOT_ANIMATE }
                .map { it == MetadataConstants.ANIMATE_TO_FAVORITE }
            .asLiveData()

}