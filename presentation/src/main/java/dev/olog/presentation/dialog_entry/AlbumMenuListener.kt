package dev.olog.presentation.dialog_entry

import android.app.Application
import android.view.MenuItem
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.item.GetAlbumUseCase
import dev.olog.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.domain.interactor.dialog.GetPlaylistBlockingUseCase
import dev.olog.presentation.R
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.service_music.MusicController
import dev.olog.shared.MediaId
import javax.inject.Inject

class AlbumMenuListener @Inject constructor(
        application: Application,
        getSongListByParamUseCase: GetSongListByParamUseCase,
        private val navigator: Navigator,
        musicController: MusicController,
        private val getAlbumUseCase: GetAlbumUseCase,
        getPlaylistBlockingUseCase: GetPlaylistBlockingUseCase,
        addToPlaylistUseCase: AddToPlaylistUseCase

) : BaseMenuListener(application, getSongListByParamUseCase, navigator,
        musicController, getPlaylistBlockingUseCase, addToPlaylistUseCase) {

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId
        when (itemId){
            R.id.viewArtist -> {
                getAlbumUseCase.execute(item.mediaId)
                        .map { MediaId.artistId(it.artistId) }
                        .firstOrError()
                        .doOnSuccess { navigator.toDetailFragment(it) }
                        .toCompletable()
                        .subscribe()
                return true
            }
        }
        return super.onMenuItemClick(menuItem)
    }

}
