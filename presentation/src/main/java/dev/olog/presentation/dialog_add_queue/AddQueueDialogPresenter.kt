package dev.olog.presentation.dialog_add_queue

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaControllerCompat
import dev.olog.domain.entity.Song
import dev.olog.domain.interactor.GetSongListByParamUseCase
import dev.olog.domain.interactor.detail.item.GetSongUseCase
import dev.olog.shared.MediaIdHelper
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AddQueueDialogPresenter @Inject constructor(
        private val mediaId: String,
        private val getSongUseCase: GetSongUseCase,
        private val getSongListByParamUseCase: GetSongListByParamUseCase
) {

    fun execute(activity: Activity): Single<String> {
        val controller = MediaControllerCompat.getMediaController(activity)
                ?: return Single.error(AssertionError("null media controller"))

        if (MediaIdHelper.extractCategory(mediaId) == MediaIdHelper.MEDIA_ID_BY_ALL){
            return getSongUseCase.execute(mediaId)
                    .firstOrError()
                    .map { it.toMediaDescriptionItem() }
                    .doOnSuccess { controller.addQueueItem(it) }
                    .map { it.title.toString() }
        }

        return getSongListByParamUseCase.execute(mediaId)
                .observeOn(Schedulers.computation())
                .firstOrError()
                .flatMap { it.toFlowable()
                        .map { it.toMediaDescriptionItem() }
                        .doOnNext { controller.addQueueItem(it) }
                        .toList()

                }.map { it.size.toString() }
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