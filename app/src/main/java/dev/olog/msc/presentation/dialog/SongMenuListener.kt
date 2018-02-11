package dev.olog.msc.presentation.dialog

import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.content.Intent
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import dev.olog.msc.R
import dev.olog.msc.dagger.ProcessLifecycle
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.detail.item.GetSongUseCase
import dev.olog.msc.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.msc.domain.interactor.dialog.GetPlaylistBlockingUseCase
import dev.olog.msc.presentation.base.music.service.MediaProvider
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.asHtml
import io.reactivex.Completable
import io.reactivex.rxkotlin.addTo
import org.jetbrains.anko.toast
import java.io.File
import javax.inject.Inject

class SongMenuListener @Inject constructor(
        @ProcessLifecycle lifecycle: Lifecycle,
        application: Application,
        private val activity: AppCompatActivity,
        getSongListByParamUseCase: GetSongListByParamUseCase,
        private val navigator: Navigator,
        mediaProvider: MediaProvider,
        private val getSongUseCase: GetSongUseCase,
        getPlaylistBlockingUseCase: GetPlaylistBlockingUseCase,
        addToPlaylistUseCase: AddToPlaylistUseCase

) : BaseMenuListener(lifecycle,application, getSongListByParamUseCase, navigator,
        mediaProvider, getPlaylistBlockingUseCase, addToPlaylistUseCase) {

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
        intent.putExtra(Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(activity, activity.applicationContext.packageName, File(song.path)))
        intent.type = "audio/*"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (intent.resolveActivity(activity.packageManager) != null){
            val string = activity.getString(R.string.share_song_x, song.title)
            activity.startActivity(Intent.createChooser(intent, string.asHtml()))
        } else {
            activity.toast("Could not share this file")
        }
    }

}