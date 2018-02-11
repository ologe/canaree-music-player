package dev.olog.msc.presentation.player

import dev.olog.msc.R
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import javax.inject.Inject

class PlayerFragmentViewModel @Inject constructor(


) {

    val footerLoadMore = DisplayableItem(R.layout.item_playing_queue_load_more, MediaId.headerId("load more"), "")

    val playerControls = DisplayableItem(R.layout.fragment_player_controls,
            MediaId.headerId("player controls id"), "")

//    val onBookmarkChangedObservable: LiveData<Int> = controllerCallback
//            .onPlaybackStateChanged()
//            .filter { playbackState ->
//                val state = playbackState.state
//                filterPlaybackState.test(state)
//            }.map { it.position.toInt() }
//            .asLiveData()
//
//    val onFavoriteStateChangedObservable: LiveData<Boolean> = controllerCallback.onMetadataChanged()
//            .map { it.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) }
//            .map { MediaId.fromString(it) }
//            .map { it.leaf!! }
//            .distinctUntilChanged()
//            .flatMapSingle { isFavoriteSongUseCase.execute(it) }
//            .asLiveData()

//    val onFavoriteAnimateRequestObservable: LiveData<Boolean> = observeFavoriteAnimationUseCase
//            .execute()
//            .map { it.animateTo == AnimateFavoriteEnum.TO_FAVORITE }
//            .asLiveData()

}