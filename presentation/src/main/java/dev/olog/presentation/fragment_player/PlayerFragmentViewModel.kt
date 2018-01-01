package dev.olog.presentation.fragment_player

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.domain.entity.AnimateFavoriteEnum
import dev.olog.domain.interactor.favorite.IsFavoriteSongUseCase
import dev.olog.domain.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.presentation.model.CoverModel
import dev.olog.presentation.model.DurationModel
import dev.olog.presentation.model.PlayerFragmentMetadata
import dev.olog.presentation.model.toPlayerMetadata
import dev.olog.shared.MediaId
import dev.olog.shared_android.CoverUtils
import dev.olog.shared_android.TextUtils
import dev.olog.shared_android.extension.asLiveData
import dev.olog.shared_android.music_service.RxMusicServiceControllerCallback
import io.reactivex.functions.Predicate

class PlayerFragmentViewModel(
        application: Application,
        controllerCallback: RxMusicServiceControllerCallback,
        observeFavoriteAnimationUseCase: ObserveFavoriteAnimationUseCase,
        isFavoriteSongUseCase: IsFavoriteSongUseCase

) : AndroidViewModel(application) {

    private val filterPlaybackState : Predicate<Int> = Predicate { state ->
        state == PlaybackStateCompat.STATE_PAUSED || state == PlaybackStateCompat.STATE_PLAYING
    }

    val onMetadataChangedLiveData: LiveData<PlayerFragmentMetadata> = controllerCallback.onMetadataChanged()
            .map { it.toPlayerMetadata() }
            .distinctUntilChanged()
            .asLiveData()

    val onCoverChangedLiveData: LiveData<CoverModel> = controllerCallback.onMetadataChanged()
            .map {
                val image = it.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)
                val mediaId = MediaId.fromString(it.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID))
                val id = mediaId.leaf!!
                CoverModel(image, CoverUtils.getGradient(application, id.toInt()))
            }.distinctUntilChanged()
            .asLiveData()

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
            .map { DurationModel(it.toInt(), TextUtils.MIDDLE_DOT_SPACED + TextUtils.getReadableSongLength(it)) }
            .asLiveData()


    val onBookmarkChangedObservable: LiveData<Int> = controllerCallback
            .onPlaybackStateChanged()
            .filter { playbackState ->
                val state = playbackState.state
                filterPlaybackState.test(state)
            }.map { it.position.toInt() }
            .asLiveData()

    val onFavoriteStateChangedObservable: LiveData<Boolean> = controllerCallback.onMetadataChanged()
            .map { it.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) }
            .map { MediaId.fromString(it) }
            .map { it.leaf!! }
            .distinctUntilChanged()
            .flatMapSingle { isFavoriteSongUseCase.execute(it) }
            .asLiveData()

    val onFavoriteAnimateRequestObservable: LiveData<Boolean> = observeFavoriteAnimationUseCase
            .execute()
            .map { it.animateTo == AnimateFavoriteEnum.TO_FAVORITE }
            .asLiveData()

}