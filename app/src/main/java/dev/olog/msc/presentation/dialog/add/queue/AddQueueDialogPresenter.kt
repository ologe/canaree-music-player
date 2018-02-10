package dev.olog.msc.presentation.dialog.add.queue

import android.app.Activity
import android.app.Application
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaControllerCompat
import android.text.TextUtils
import dev.olog.msc.R
import dev.olog.msc.domain.interactor.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.detail.item.GetSongUseCase
import dev.olog.msc.utils.MediaId
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.toast
import javax.inject.Inject

class AddQueueDialogPresenter @Inject constructor(
        private val application: Application,
        private val mediaId: MediaId,
        private val getSongUseCase: GetSongUseCase,
        private val getSongListByParamUseCase: GetSongListByParamUseCase
) {

    fun execute(activity: Activity): Completable {
        val controller = MediaControllerCompat.getMediaController(activity)
                ?: return Completable.error(AssertionError("null media controller"))

        val single = if (mediaId.isAll || mediaId.isLeaf){
            getSongUseCase.execute(mediaId)
                    .firstOrError()
                    .doOnSuccess { controller.addQueueItem(newMediaDescriptionItem(it.id.toString())) }
                    .map { it.title }
        } else {
            getSongListByParamUseCase.execute(mediaId)
                    .observeOn(Schedulers.computation())
                    .firstOrError()
                    .map { Pair(it.map { it.id }.joinToString(), it) }
                    .doOnSuccess { (songIds, _) -> controller.addQueueItem(newMediaDescriptionItem(songIds)) }
                    .map { it.second.size }
                    .map { it.toString() }
        }

        return single
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess { createSuccessMessage(it) }
                .doOnError { createErrorMessage() }
                .toCompletable()
    }

    private fun createSuccessMessage(string: String){
        val message = if (TextUtils.isDigitsOnly(string)){
            val size = string.toInt()
            application.resources.getQuantityString(R.plurals.xx_songs_added_to_queue, size, size)
        } else {
            application.getString(R.string.song_x_added_to_queue, string)
        }
        application.toast(message)
    }

    private fun createErrorMessage(){
        application.toast(application.getString(R.string.popup_error_message))
    }

    private fun newMediaDescriptionItem(songId: String): MediaDescriptionCompat {
        return MediaDescriptionCompat.Builder()
                .setMediaId(songId)
                .build()
    }

}