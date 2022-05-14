package dev.olog.feature.main.dialog.play.next

import android.support.v4.media.session.MediaControllerCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import dev.olog.feature.media.api.MusicServiceCustomAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlayNextDialogViewModel @Inject constructor(
    private val getSongListByParamUseCase: GetSongListByParamUseCase
) : ViewModel() {

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
            MusicServiceCustomAction.ADD_TO_PLAY_NEXT.name,
            bundle
        )
    }

}