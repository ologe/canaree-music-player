package dev.olog.msc.presentation.dialog.play.later

import android.support.v4.media.session.MediaControllerCompat
import androidx.core.os.bundleOf
import dev.olog.core.MediaId
import dev.olog.core.interactor.songlist.ObserveSongListByParamUseCase
import dev.olog.intents.MusicServiceCustomAction
import dev.olog.shared.mapListItem
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.rx2.asFlowable
import javax.inject.Inject

class PlayLaterDialogPresenter @Inject constructor(
    private val mediaId: MediaId,
    private val getSongListByParamUseCase: ObserveSongListByParamUseCase
) {

    fun execute(mediaController: MediaControllerCompat): Completable {
        return if (mediaId.isLeaf) {
            Single.fromCallable { listOf(mediaId.leaf!!) }
        } else {
            getSongListByParamUseCase(mediaId)
                .mapListItem { it.id }
                .asFlowable()
                .firstOrError()
        }.map {
            val bundle = bundleOf(
                MusicServiceCustomAction.ARGUMENT_IS_PODCAST to mediaId.isAnyPodcast,
                MusicServiceCustomAction.ARGUMENT_MEDIA_ID_LIST to it.toLongArray()
            )

            mediaController.transportControls.sendCustomAction(
                MusicServiceCustomAction.ADD_TO_PLAY_LATER.name,
                bundle
            )
        }.ignoreElement()
    }

}