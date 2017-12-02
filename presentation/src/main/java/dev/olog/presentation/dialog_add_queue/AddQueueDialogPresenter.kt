package dev.olog.presentation.dialog_add_queue

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaControllerCompat
import dev.olog.domain.entity.Song
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable
import io.reactivex.rxkotlin.toFlowable
import javax.inject.Inject

class AddQueueDialogPresenter @Inject constructor(
        private val mediaId: String,
        private val getSongListByParamUseCase: GetSongListByParamUseCase
) {

    fun execute(activity: Activity): Completable {
        val controller = MediaControllerCompat.getMediaController(activity)
                ?: return Completable.complete()

        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .flatMap { it.toFlowable()
                        .map { it.toMediaDescriptionItem() }
                        .doOnNext { controller.addQueueItem(it) }
                        .toList()

                }.toCompletable()
    }

    private fun Song.toMediaDescriptionItem(): MediaDescriptionCompat {
        val bundle = Bundle()
        bundle.putBoolean("remix", isRemix)
        bundle.putBoolean("explicit", isExplicit)
        bundle.putLong("duration", duration)
        return MediaDescriptionCompat.Builder()
                .setMediaId(MediaIdHelper.songId(this.id))
                .setTitle(this.title)
                .setSubtitle(this.artist)
                .setDescription(this.album)
                .setMediaUri(Uri.parse(this.image))
                .setExtras(bundle)
                .build()
    }

}