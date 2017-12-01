package dev.olog.presentation.dialog_entry

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import dev.olog.domain.entity.Song
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.item.GetSongUseCase
import dev.olog.presentation.R
import dev.olog.presentation.navigation.Navigator
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable

class SongMenuListener(
        private val activity: AppCompatActivity,
        private val mediaId: String,
        getSongListByParamUseCase: GetSongListByParamUseCase,
        private val navigator: Navigator,
        private val getSongUseCase: GetSongUseCase

) : BaseMenuListener(mediaId, getSongListByParamUseCase, navigator) {


    override fun onMenuItemClick(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId){
            R.id.viewAlbum -> {
                getSongUseCase.execute(mediaId)
                        .map { MediaIdHelper.albumId(it.albumId) }
                        .firstOrError()
                        .doOnSuccess { navigator.toDetailActivity(it, 0) }
                        .toCompletable()
                        .subscribe()
            }
            R.id.viewArtist -> {
                getSongUseCase.execute(mediaId)
                        .map { MediaIdHelper.artistId(it.artistId) }
                        .firstOrError()
                        .doOnSuccess { navigator.toDetailActivity(it, 0) }
                        .toCompletable()
                        .subscribe()
            }
            R.id.share -> {
                getSongUseCase.execute(mediaId)
                        .firstOrError()
                        .doOnSuccess { share(activity, it) }
                        .toCompletable()
                        .subscribe()
            }
            R.id.setRingtone -> {
                Completable.fromCallable { navigator.toSetRingtoneDialog(mediaId) }
                        .subscribe()
            }
        }
        return super.onMenuItemClick(item)
    }

    private fun share(activity: AppCompatActivity, song: Song){
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://${song.path}"))
        intent.type = "audio/*"
        if (intent.resolveActivity(activity.packageManager) != null){
            activity.startActivity(Intent.createChooser(intent, "share ${song.title}?"))
        } else {
            Log.e("DialogItem", "share failed, null package manager")
        }
    }

}