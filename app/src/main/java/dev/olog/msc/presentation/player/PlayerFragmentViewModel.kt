package dev.olog.msc.presentation.player

import dev.olog.msc.R
import dev.olog.msc.domain.entity.AnimateFavoriteEnum
import dev.olog.msc.domain.interactor.favorite.IsFavoriteSongUseCase
import dev.olog.msc.domain.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class PlayerFragmentViewModel @Inject constructor(
        observeFavoriteAnimationUseCase: ObserveFavoriteAnimationUseCase,
        val isFavoriteSongUseCase: IsFavoriteSongUseCase

) {

    val footerLoadMore = DisplayableItem(R.layout.item_playing_queue_load_more, MediaId.headerId("load more"), "")

    val playerControls = DisplayableItem(R.layout.fragment_player_controls,
            MediaId.headerId("player controls id"), "")

    val onFavoriteAnimateRequestObservable: Observable<Boolean> = observeFavoriteAnimationUseCase
            .execute()
            .map { it.animateTo == AnimateFavoriteEnum.TO_FAVORITE }

}