package dev.olog.msc.presentation.popup.menu.listener

import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.view.MenuItem
import dev.olog.msc.R
import dev.olog.msc.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.domain.interactor.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.msc.domain.interactor.dialog.GetPlaylistBlockingUseCase
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.navigator.Navigator
import io.reactivex.Completable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class PlaylistMenuListener @Inject constructor(
        @ProcessLifecycle lifecycle: Lifecycle,
        application: Application,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val navigator: Navigator,
        mediaProvider: MediaProvider,
        getPlaylistBlockingUseCase: GetPlaylistBlockingUseCase,
        addToPlaylistUseCase: AddToPlaylistUseCase

) : BaseMenuListener(lifecycle, application, getSongListByParamUseCase, navigator,
        mediaProvider, getPlaylistBlockingUseCase, addToPlaylistUseCase) {

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId
        when (itemId){
            R.id.rename -> {
                rename()
                return true
            }
            R.id.clear -> {
                clearPlaylist()
                return true
            }
        }
        return super.onMenuItemClick(menuItem)
    }

    private fun rename(){
        Completable.fromCallable { navigator.toRenameDialog(item.mediaId, item.title) }
                .subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    private fun clearPlaylist(){
        getSongListByParamUseCase.execute(item.mediaId)
                .firstOrError()
                .doOnSuccess { navigator.toClearPlaylistDialog(item.mediaId, it.size, item.title) }
                .toCompletable()
                .subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

}
