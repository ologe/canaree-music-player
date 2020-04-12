package dev.olog.feature.edit

import android.content.Context
import dev.olog.domain.MediaId
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.domain.interactor.songlist.GetSongListByParamUseCase
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.presentation.base.extensions.toast
import dev.olog.lib.audio.tagger.AudioTagger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject

class EditItemDialogFactory @Inject constructor(
    private val context: Context,
    private val trackGateway: TrackGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase,
    private val schedulers: Schedulers,
    private val audioTagger: AudioTagger

) {

    fun toEditTrack(
        mediaId: MediaId.Track,
        action: () -> Unit
    ) = GlobalScope.launch(schedulers.main) {
        val canBeHandled = withContext(schedulers.io) {
            canBeHandled(trackGateway.getByParam(mediaId.id.toLong())!!)
        }
        if (canBeHandled) {
            action()
        } else {
            showError()
        }
    }

    fun toEditAlbum(
        mediaId: MediaId.Category,
        action: () -> Unit
    ) = GlobalScope.launch(schedulers.main) {
        val canBeHandled = withContext(schedulers.io) {
            getSongListByParamUseCase.invoke(mediaId).all { canBeHandled(it) }
        }
        if (canBeHandled) {
            action()
        } else {
            showError()
        }
    }

    fun toEditArtist(
        mediaId: MediaId.Category,
        action: () -> Unit
    ) = GlobalScope.launch(schedulers.main) {
        val canBeHandled = withContext(schedulers.io) {
            getSongListByParamUseCase.invoke(mediaId).all { canBeHandled(it) }
        }
        if (canBeHandled) {
            action()
        } else {
            showError()
        }
    }

    private fun canBeHandled(song: Song): Boolean {
        val file = File(song.path)
        return audioTagger.canBeHandled(file)
    }

    private fun showError() {
        // TODO show snackbar
        context.toast(R.string.edit_song_error)
    }

}