package dev.olog.feature.edit

import android.content.Context
import dev.olog.domain.MediaId
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.domain.interactor.songlist.GetSongListByParamUseCase
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.presentation.base.extensions.toast
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
    private val schedulers: Schedulers

) {

    fun toEditTrack(
        mediaId: MediaId.Track,
        action: () -> Unit
    ) = GlobalScope.launch(schedulers.io) {
        try {
            val track = trackGateway.getByParam(mediaId.id.toLong())!!
            checkItem(track)
            withContext(schedulers.main) {
                action()
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            withContext(schedulers.main) {
                showError(ex)
            }
        }
    }

    fun toEditAlbum(
        mediaId: MediaId.Category,
        action: () -> Unit
    ) = GlobalScope.launch(schedulers.io) {
        try {
            getSongListByParamUseCase.invoke(mediaId).forEach { checkItem(it) }
            withContext(schedulers.main) {
                action()
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            withContext(schedulers.main) {
                showError(ex)
            }
        }
    }

    fun toEditArtist(
        mediaId: MediaId.Category,
        action: () -> Unit
    ) = GlobalScope.launch(schedulers.io) {
        try {
            getSongListByParamUseCase.invoke(mediaId).forEach { checkItem(it) }
            withContext(schedulers.main) {
                action()
            }
        } catch (ex: Exception) {
            Timber.e(ex)
            withContext(schedulers.main) {
                showError(ex)
            }
        }
    }

    private fun checkItem(song: Song) {
//        val file = File(song.path) TODO
//        val audioFile = AudioFileIO.read(file)
//        audioFile.tagOrCreateAndSetDefault
    }

    private fun showError(error: Exception) {
//        when (error) {
//            is CannotReadException -> context.toast(R.string.edit_song_error_can_not_read)
//            is IOException -> context.toast(R.string.edit_song_error_io)
//            is ReadOnlyFileException -> context.toast(R.string.edit_song_error_read_only)
//            else -> {
//                error.printStackTrace()
//                context.toast(R.string.edit_song_error)
//            }
//        }
    }

}