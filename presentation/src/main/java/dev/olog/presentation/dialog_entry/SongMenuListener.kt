package dev.olog.presentation.dialog_entry

import android.app.Application
import android.arch.lifecycle.Lifecycle
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
import dev.olog.presentation.R
import dev.olog.presentation.navigation.Navigator
import dev.olog.presentation.service_music.MusicController
import dev.olog.presentation.utils.extension.asHtml
import dev.olog.shared.MediaId
import dev.olog.shared.ProcessLifecycle
import io.reactivex.Completable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class SongMenuListener @Inject constructor(
        @ProcessLifecycle lifecycle: Lifecycle,
        application: Application,
        private val activity: AppCompatActivity,
        getSongListByParamUseCase: GetSongListByParamUseCase,
        private val navigator: Navigator,
        musicController: MusicController,
        private val getSongUseCase: GetSongUseCase,
        getPlaylistBlockingUseCase: GetPlaylistBlockingUseCase,
        addToPlaylistUseCase: AddToPlaylistUseCase

) : BaseMenuListener(lifecycle,application, getSongListByParamUseCase, navigator,
        musicController, getPlaylistBlockingUseCase, addToPlaylistUseCase) {

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val itemId = menuItem.itemId
        when (itemId){
            R.id.viewInfo -> {
                viewInfo()
                return true
            }

            R.id.viewAlbum -> {
                viewAlbum()
                return true
            }
            R.id.viewArtist -> {
                viewArtist()
                return true
            }
            R.id.share -> {
                share()
                return true
            }
            R.id.setRingtone -> {
                setRingtone()
                return true
            }
        }
        return super.onMenuItemClick(menuItem)
    }

    private fun viewInfo(){
        navigator.toEditInfoFragment(item.mediaId)
    }

    private fun viewAlbum(){
        getSongUseCase.execute(item.mediaId)
                .map { MediaId.albumId(it.albumId) }
                .firstOrError()
                .doOnSuccess { navigator.toDetailFragment(it) }
                .toCompletable()
                .subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    private fun viewArtist(){
        getSongUseCase.execute(item.mediaId)
                .map { MediaId.artistId(it.artistId) }
                .firstOrError()
                .doOnSuccess { navigator.toDetailFragment(it) }
                .toCompletable()
                .subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    private fun setRingtone(){
        Completable.fromCallable { navigator.toSetRingtoneDialog(item.mediaId, item.title) }
                .subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    private fun share(){
        getSongUseCase.execute(item.mediaId)
                .firstOrError()
                .doOnSuccess { shareImpl(it) }
                .toCompletable()
                .subscribe({}, Throwable::printStackTrace)
                .addTo(subscriptions)
    }

    private fun shareImpl(song: Song){
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