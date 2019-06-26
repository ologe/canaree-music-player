package dev.olog.msc.presentation.dialog.play.next

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.core.os.bundleOf
import dev.olog.shared.MusicConstants
import dev.olog.msc.domain.interactor.all.ObserveSongListByParamUseCase
import dev.olog.core.MediaId
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.rx2.asFlowable
import javax.inject.Inject

class PlayNextDialogPresenter @Inject constructor(
    private val mediaId: MediaId,
    private val getSongListByParamUseCase: ObserveSongListByParamUseCase
) {

    fun execute(mediaController: MediaControllerCompat): Completable {
        return if (mediaId.isLeaf){
            Single.fromCallable { "${mediaId.leaf!!}" }.subscribeOn(Schedulers.io())
        } else {
            getSongListByParamUseCase(mediaId)
                    .asFlowable()
                    .firstOrError()
                    .map { songList -> songList.asSequence().map { it.id }.joinToString() }
        }.map { mediaController.addQueueItem(newMediaDescriptionItem(it), Int.MAX_VALUE) }
                .ignoreElement()
    }

    private fun newMediaDescriptionItem(songId: String): MediaDescriptionCompat {
        return MediaDescriptionCompat.Builder()
                .setMediaId(songId)
                .setExtras(bundleOf(MusicConstants.IS_PODCAST to mediaId.isAnyPodcast))
                .build()
    }

}