package dev.olog.msc.presentation.dialog

import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.view.MenuItem
import dev.olog.msc.R
import dev.olog.msc.dagger.ProcessLifecycle
import dev.olog.msc.domain.interactor.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.detail.item.GetAlbumUseCase
import dev.olog.msc.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.msc.domain.interactor.dialog.GetPlaylistBlockingUseCase
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.utils.MediaId
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class AlbumMenuListener @Inject constructor(
        @ProcessLifecycle lifecycle: Lifecycle,
        application: Application,
        getSongListByParamUseCase: GetSongListByParamUseCase,
        private val navigator: Navigator,
        mediaProvider: MediaProvider,
        private val getAlbumUseCase: GetAlbumUseCase,
        getPlaylistBlockingUseCase: GetPlaylistBlockingUseCase,
        addToPlaylistUseCase: AddToPlaylistUseCase

) : BaseMenuListener(lifecycle, application, getSongListByParamUseCase, navigator,
        mediaProvider, getPlaylistBlockingUseCase, addToPlaylistUseCase) {

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId
        when (itemId){
            R.id.viewArtist -> {
                viewArtist()
                return true
            }
        }
        return super.onMenuItemClick(menuItem)
    }

    private fun viewArtist(){
        getAlbumUseCase.execute(item.mediaId)
                .map { MediaId.artistId(it.artistId) }
                .firstOrError()
                .doOnSuccess { navigator.toDetailFragment(it) }
                .toCompletable()
                .subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

}
