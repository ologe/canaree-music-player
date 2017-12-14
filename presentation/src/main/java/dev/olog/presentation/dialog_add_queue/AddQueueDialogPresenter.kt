package dev.olog.presentation.dialog_add_queue

import android.app.Activity
import android.app.Application
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaControllerCompat
import android.text.TextUtils
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.item.GetSongUseCase
import dev.olog.presentation.R
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.toast
import javax.inject.Inject

class AddQueueDialogPresenter @Inject constructor(
        private val application: Application,
        private val mediaId: String,
        private val getSongUseCase: GetSongUseCase,
        private val getSongListByParamUseCase: GetSongListByParamUseCase
) {

    fun execute(activity: Activity): Completable {
        val controller = MediaControllerCompat.getMediaController(activity)
                ?: return Completable.error(AssertionError("null media controller"))

        val isSong = MediaIdHelper.isSong(mediaId)
        val category = MediaIdHelper.extractCategory(mediaId)

        val single = if (category == MediaIdHelper.MEDIA_ID_BY_ALL || isSong){
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
            application.resources.getQuantityString(R.plurals.added_xx_songs_to_queue, size, size)
        } else {
            application.getString(R.string.added_song_x_to_queue, string)
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