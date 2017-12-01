package dev.olog.presentation.dialog_entry

import android.view.MenuItem
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.presentation.R
import dev.olog.presentation.navigation.Navigator
import io.reactivex.Completable

class PlaylistMenuListener(
        private val mediaId: String,
        getSongListByParamUseCase: GetSongListByParamUseCase,
        private val navigator: Navigator

) : BaseMenuListener(mediaId, getSongListByParamUseCase, navigator) {

    override fun onMenuItemClick(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId){
            R.id.rename -> {
                Completable.fromCallable { navigator.toRenameDialog(mediaId) }
                        .subscribe()
            }
        }
        return super.onMenuItemClick(item)
    }

}
