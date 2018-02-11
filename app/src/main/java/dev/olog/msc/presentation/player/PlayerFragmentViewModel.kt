package dev.olog.msc.presentation.player

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.msc.R
import dev.olog.msc.domain.entity.AnimateFavoriteEnum
import dev.olog.msc.domain.entity.PlayingQueueSong
import dev.olog.msc.domain.interactor.favorite.IsFavoriteSongUseCase
import dev.olog.msc.domain.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.msc.domain.interactor.music.service.GetMiniPlayingQueueUseCase
import dev.olog.msc.presentation.model.*
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.TextUtils
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.asLiveData
import dev.olog.msc.utils.k.extension.groupMap
import dev.olog.shared_android.music_service.IRxMusicServiceControllerCallback
import io.reactivex.functions.Predicate

class PlayerFragmentViewModel(
        application: Application,
        controllerCallback: IRxMusicServiceControllerCallback,
        observeFavoriteAnimationUseCase: ObserveFavoriteAnimationUseCase,
        isFavoriteSongUseCase: IsFavoriteSongUseCase,
        getMiniPlayingQueueUseCase: GetMiniPlayingQueueUseCase

) : AndroidViewModel(application) {

    private val footerLoadMore = DisplayableItem(R.layout.item_playing_queue_load_more, MediaId.headerId("load more"), "")

    val miniQueue: LiveData<MutableList<DisplayableItem>> = getMiniPlayingQueueUseCase
            .execute()
            .groupMap { it.toPlayingQueueDisplayableItem() }
            .map { it.toMutableList() }
            .map {
                if (it.size > 50) {
                    it[50] = footerLoadMore
                }
                it
            }
            .distinctUntilChanged()
            .asLiveData()

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

    private fun PlayingQueueSong.toPlayingQueueDisplayableItem(): DisplayableItem{
        return DisplayableItem(
                R.layout.item_mini_queue,
                MediaId.songId(this.id),
                title,
                "$artist${TextUtils.MIDDLE_DOT_SPACED}$album",
                image,
                true,
                isRemix,
                isExplicit,
                this.idInPlaylist.toString()
        )
    }

}