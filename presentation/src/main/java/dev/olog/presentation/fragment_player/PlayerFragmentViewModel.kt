package dev.olog.presentation.fragment_player

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.graphics.drawable.TransitionDrawable
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.presentation.images.CoverUtils
import dev.olog.presentation.model.*
import dev.olog.presentation.service_music.RxMusicServiceControllerCallback
import dev.olog.presentation.utils.TextUtils.getReadableSongLength
import dev.olog.presentation.utils.extension.asLiveData
import dev.olog.shared.TextUtils
import dev.olog.shared.constants.MetadataConstants
import io.reactivex.functions.Predicate

class PlayerFragmentViewModel(
        application: Application,
        controllerCallback: RxMusicServiceControllerCallback

) : AndroidViewModel(application) {

    private val filterPlaybackState : Predicate<Int> = Predicate { state ->
        state == PlaybackStateCompat.STATE_PAUSED || state == PlaybackStateCompat.STATE_PLAYING
    }

    val onMetadataChangedLiveData: LiveData<PlayerFragmentMetadata> = controllerCallback.onMetadataChanged()
            .map { it.toPlayerMetadata() }
            .distinctUntilChanged()
            .asLiveData()

    val onCoverChangedLiveData: LiveData<CoverModel> = controllerCallback.onMetadataChanged()
            .map { CoverTempModel(it) }
            .scan { old: CoverTempModel, new: CoverTempModel ->
                if (old.id.contains("|")){
                    val indexOf = old.id.indexOf("|")
                    CoverTempModel(new.uri, "${old.id.substring(indexOf + 1)}|${new.id}")
                } else {
                    CoverTempModel(new.uri, "${old.id}|${new.id}")
                }
             }
            .distinctUntilChanged()
            .map { model ->
                val indexOf = model.id.indexOf("|")
                if (indexOf != -1) {
                    val oldId = model.id.substring(0, indexOf).toInt()
                    val newId = model.id.substring(indexOf + 1).toInt()

                    val drawable = TransitionDrawable(arrayOf(
                            CoverUtils.getGradient(application, oldId),
                            CoverUtils.getGradient(application, newId)
                    ))
                    CoverModel(model.uri, drawable)
                } else {
                    CoverModel(model.uri, CoverUtils.getGradient(application, model.id.toInt()))
                }

            }.asLiveData()

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
                    bundle.clear()
                    animate
                }
                .filter { it != MetadataConstants.NOT_ANIMATE }
                .map { it == MetadataConstants.ANIMATE_TO_FAVORITE }
            .asLiveData()

}