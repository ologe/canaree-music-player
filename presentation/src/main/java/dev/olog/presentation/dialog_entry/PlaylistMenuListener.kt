package dev.olog.presentation.dialog_entry

import android.view.MenuItem
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.item.GetPlaylistUseCase
import dev.olog.presentation.R
import dev.olog.presentation.navigation.Navigator
import io.reactivex.Completable
import io.reactivex.CompletableSource
import javax.inject.Inject

class PlaylistMenuListener @Inject constructor(
        getSongListByParamUseCase: GetSongListByParamUseCase,
        private val navigator: Navigator,
        private val getPlaylistUseCase: GetPlaylistUseCase

) : BaseMenuListener(getSongListByParamUseCase, navigator) {

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId
        when (itemId){
            R.id.rename -> {
                getPlaylistUseCase.execute(item.mediaId)
                        .firstOrError()
                        .flatMapCompletable { playlist -> CompletableSource {
                            navigator.toRenameDialog(item.mediaId, playlist.title)
                        }}.subscribe()
            }
            R.id.clear -> {
                Completable.complete() // todo
            }
        }
        return super.onMenuItemClick(menuItem)
    }

}
