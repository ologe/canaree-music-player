package dev.olog.feature.dialog.play.next

import android.support.v4.media.session.MediaControllerCompat
import androidx.core.os.bundleOf
import androidx.hilt.Assisted
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dev.olog.domain.entity.track.Track
import dev.olog.domain.interactor.songlist.GetSongListByParamUseCase
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.schedulers.Schedulers
import dev.olog.lib.media.MusicServiceCustomAction
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.argument
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlayNextDialogViewModel @Inject constructor(
    @Assisted private val state: SavedStateHandle,
    private val schedulers: Schedulers,
    private val getSongListByParamUseCase: GetSongListByParamUseCase
) : ViewModel() {

    private val mediaId = state.argument(Params.MEDIA_ID, MediaId::fromString)


    suspend fun execute(
        mediaController: MediaControllerCompat
    ) = withContext(schedulers.cpu) {

        val items = when (mediaId) {
            is MediaId.Track -> listOf(mediaId.id)
            is MediaId.Category -> getSongListByParamUseCase(mediaId).map(Track::id)
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