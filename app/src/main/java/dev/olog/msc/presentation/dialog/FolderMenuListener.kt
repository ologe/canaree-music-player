package dev.olog.msc.presentation.dialog

import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.view.MenuItem
import dev.olog.msc.R
import dev.olog.msc.dagger.ProcessLifecycle
import dev.olog.msc.domain.interactor.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.msc.domain.interactor.dialog.GetPlaylistBlockingUseCase
import dev.olog.msc.presentation.MusicController
import dev.olog.msc.presentation.navigator.Navigator
import io.reactivex.Completable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class FolderMenuListener @Inject constructor(
        @ProcessLifecycle lifecycle: Lifecycle,
        application: Application,
        getSongListByParamUseCase: GetSongListByParamUseCase,
        private val navigator: Navigator,
        musicController: MusicController,
        getPlaylistBlockingUseCase: GetPlaylistBlockingUseCase,
        addToPlaylistUseCase: AddToPlaylistUseCase

) : BaseMenuListener(lifecycle, application, getSongListByParamUseCase, navigator,
        musicController, getPlaylistBlockingUseCase, addToPlaylistUseCase) {

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId
        when (itemId){
            R.id.rename -> {
                rename()
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

}
