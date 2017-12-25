package dev.olog.presentation.dialog_entry

import android.content.Context
import android.view.MenuItem
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.GetDetailTabsVisibilityUseCase
import dev.olog.domain.interactor.detail.SetDetailTabsVisiblityUseCase
import dev.olog.presentation.R
import dev.olog.presentation.dagger.ActivityContext
import dev.olog.presentation.navigation.Navigator
import io.reactivex.Completable
import javax.inject.Inject

class PlaylistMenuListener @Inject constructor(
        @ActivityContext context: Context,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val navigator: Navigator,
        getDetailTabVisibilityUseCase: GetDetailTabsVisibilityUseCase,
        setDetailTabVisibilityUseCase: SetDetailTabsVisiblityUseCase

) : BaseMenuListener(context, getSongListByParamUseCase, navigator,
        getDetailTabVisibilityUseCase, setDetailTabVisibilityUseCase) {

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId
        when (itemId){
            R.id.rename -> {
                Completable.fromCallable { navigator.toRenameDialog(item.mediaId, item.title) }
                        .subscribe()
                return true
            }
            R.id.clear -> {
                getSongListByParamUseCase.execute(item.mediaId)
                        .firstOrError()
                        .doOnSuccess { navigator.toClearPlaylistDialog(item.mediaId, it.size, item.title) }
                        .toCompletable()
                        .subscribe()
                return true
            }
        }
        return super.onMenuItemClick(menuItem)
    }

}
