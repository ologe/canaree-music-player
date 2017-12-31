package dev.olog.presentation.dialog_entry

import android.app.Application
import android.view.MenuItem
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.domain.interactor.dialog.GetPlaylistBlockingUseCase
import dev.olog.presentation.R
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.service_music.MusicController
import io.reactivex.Completable
import javax.inject.Inject

class PlaylistMenuListener @Inject constructor(
        application: Application,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val navigator: Navigator,
        musicController: MusicController,
        getPlaylistBlockingUseCase: GetPlaylistBlockingUseCase,
        addToPlaylistUseCase: AddToPlaylistUseCase

) : BaseMenuListener(application, getSongListByParamUseCase, navigator,
        musicController, getPlaylistBlockingUseCase, addToPlaylistUseCase) {

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
