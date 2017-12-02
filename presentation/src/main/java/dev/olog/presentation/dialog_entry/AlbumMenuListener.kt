package dev.olog.presentation.dialog_entry

import android.view.MenuItem
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.item.GetAlbumUseCase
import dev.olog.presentation.R
import dev.olog.presentation.navigation.Navigator
import dev.olog.shared.MediaIdHelper
import javax.inject.Inject

class AlbumMenuListener @Inject constructor(
        getSongListByParamUseCase: GetSongListByParamUseCase,
        private val navigator: Navigator,
        private val getAlbumUseCase: GetAlbumUseCase

) : BaseMenuListener(getSongListByParamUseCase, navigator) {

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId
        when (itemId){
            R.id.viewArtist -> {
                getAlbumUseCase.execute(item.mediaId)
                        .map { MediaIdHelper.artistId(it.artistId) }
                        .firstOrError()
                        .doOnSuccess { navigator.toDetailActivity(it, 0) }
                        .toCompletable()
                        .subscribe()
            }
        }
        return super.onMenuItemClick(menuItem)
    }

}
