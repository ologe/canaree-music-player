package dev.olog.feature.dialog.play.later

import android.support.v4.media.session.MediaControllerCompat
import androidx.core.os.bundleOf
import dev.olog.core.mediaid.MediaId
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import dev.olog.lib.media.MusicServiceCustomAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlayLaterDialogPresenter @Inject constructor(
    private val getSongListByParamUseCase: GetSongListByParamUseCase
) {

    suspend fun execute(mediaController: MediaControllerCompat, mediaId: MediaId) = withContext(Dispatchers.IO) {
        val items = if (mediaId.isLeaf) {
            listOf(mediaId.leaf!!)
        } else {
            getSongListByParamUseCase(mediaId).map { it.id }
        }
        val bundle = bundleOf(
            MusicServiceCustomAction.ARGUMENT_IS_PODCAST to mediaId.isAnyPodcast,
            MusicServiceCustomAction.ARGUMENT_MEDIA_ID_LIST to items.toLongArray()
        )

        mediaController.transportControls.sendCustomAction(
            MusicServiceCustomAction.ADD_TO_PLAY_LATER.name,
            bundle
        )
    }

}