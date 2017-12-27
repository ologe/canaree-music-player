package dev.olog.presentation.dialog_entry

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import dev.olog.domain.entity.Song
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.item.GetSongUseCase
import dev.olog.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.domain.interactor.dialog.GetPlaylistBlockingUseCase
import dev.olog.domain.interactor.floating_info.SetFloatingInfoRequestUseCase
import dev.olog.presentation.R
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.service_floating_info.FloatingInfoServiceBinder
import dev.olog.presentation.service_floating_info.FloatingInfoServiceHelper
import dev.olog.presentation.utils.extension.asHtml
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable
import javax.inject.Inject

class SongMenuListener @Inject constructor(
        private val application: Application,
        private val activity: AppCompatActivity,
        getSongListByParamUseCase: GetSongListByParamUseCase,
        private val navigator: Navigator,
        private val getSongUseCase: GetSongUseCase,
        private val floatingInfoServiceBinder: FloatingInfoServiceBinder,
        private val setFloatingInfoRequestUseCase: SetFloatingInfoRequestUseCase,
        getPlaylistBlockingUseCase: GetPlaylistBlockingUseCase,
        addToPlaylistUseCase: AddToPlaylistUseCase

) : BaseMenuListener(application, getSongListByParamUseCase, navigator,
        getPlaylistBlockingUseCase, addToPlaylistUseCase) {

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId
        when (itemId){
            R.id.viewInfo -> {
                navigator.toEditInfoFragment(item.mediaId)
            }

            R.id.viewAlbum -> {
                getSongUseCase.execute(item.mediaId)
                        .map { MediaIdHelper.albumId(it.albumId) }
                        .firstOrError()
                        .doOnSuccess { navigator.toDetailFragment(it) }
                        .toCompletable()
                        .subscribe()
                return true
            }
            R.id.viewArtist -> {
                getSongUseCase.execute(item.mediaId)
                        .map { MediaIdHelper.artistId(it.artistId) }
                        .firstOrError()
                        .doOnSuccess { navigator.toDetailFragment(it) }
                        .toCompletable()
                        .subscribe()
                return true
            }
            R.id.lyrics_video -> {
                getSongUseCase.execute(item.mediaId)
                        .firstOrError()
                        .map { item.title } // todo vedere che dati prendere
                        .doOnSuccess { setFloatingInfoRequestUseCase.execute(it) }
                        .subscribe()

                FloatingInfoServiceHelper.startServiceOrRequestOverlayPermission(activity, floatingInfoServiceBinder)
                return true
            }
            R.id.share -> {
                getSongUseCase.execute(item.mediaId)
                        .firstOrError()
                        .doOnSuccess { share(activity, it) }
                        .toCompletable()
                        .subscribe()
                return true
            }
            R.id.setRingtone -> {
                Completable.fromCallable { navigator.toSetRingtoneDialog(item.mediaId, item.title) }
                        .subscribe()
                return true
            }
        }
        return super.onMenuItemClick(menuItem)
    }

    private fun share(activity: AppCompatActivity, song: Song){
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://${song.path}"))
        intent.type = "audio/*"
        if (intent.resolveActivity(activity.packageManager) != null){
            val string = activity.getString(R.string.share_song_x, song.title)
            activity.startActivity(Intent.createChooser(intent, string.asHtml()))
        } else {
            Log.e("DialogItem", "share failed, null package manager")
        }
    }

}