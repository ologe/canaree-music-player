package dev.olog.msc.presentation.player

import dev.olog.msc.R
import dev.olog.msc.dagger.scope.PerFragment
import dev.olog.msc.domain.entity.FavoriteEnum
import dev.olog.msc.domain.interactor.favorite.ObserveFavoriteAnimationUseCase
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import dev.olog.msc.interfaces.pro.IBilling
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

@PerFragment
class PlayerFragmentViewModel @Inject constructor(
        observeFavoriteAnimationUseCase: ObserveFavoriteAnimationUseCase,
        private val billing: IBilling,
        private val appPrefsUseCase: AppPreferencesUseCase

) {

    fun showMiniQueue(): Boolean {
        return billing.isPremium() && appPrefsUseCase.showMiniQueue()
    }

    fun showPlayerControls(): Boolean {
        return billing.isPremium() && appPrefsUseCase.showPlayerControls()
    }

    private val progressPublisher = BehaviorSubject.createDefault(0)

    val observeProgress : Observable<Int> = progressPublisher

    fun updateProgress(progress: Int){
        progressPublisher.onNext(progress)
    }

    val footerLoadMore = DisplayableItem(R.layout.item_playing_queue_load_more, MediaId.headerId("load more"), "")

    val playerControls = DisplayableItem(R.layout.fragment_player_controls,
            MediaId.headerId("player controls id"), "")

    val onFavoriteStateChanged: Observable<FavoriteEnum> = observeFavoriteAnimationUseCase.execute()

}