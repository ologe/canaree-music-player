package dev.olog.presentation.edit

import android.content.Context
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.domain.interactor.songlist.GetSongListByParamUseCase
import dev.olog.domain.schedulers.Schedulers
import dev.olog.presentation.PresentationId
import dev.olog.presentation.R
import dev.olog.presentation.toDomain
import dev.olog.core.ApplicationContext
import dev.olog.shared.android.extensions.toast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject

class EditItemDialogFactory @Inject constructor(
    @ApplicationContext private val context: Context,
    private val trackGateway: TrackGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase,
    private val schedulers: Schedulers

) {

    fun toEditTrack(
        mediaId: PresentationId.Track,
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
        mediaId: PresentationId.Category,
        action: () -> Unit
    ) = GlobalScope.launch(schedulers.io) {
        try {
            getSongListByParamUseCase.invoke(mediaId.toDomain()).forEach { checkItem(it) }
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
        mediaId: PresentationId.Category,
        action: () -> Unit
    ) = GlobalScope.launch(schedulers.io) {
        try {
            getSongListByParamUseCase.invoke(mediaId.toDomain()).forEach { checkItem(it) }
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
        val file = File(song.path)
        val audioFile = AudioFileIO.read(file)
        audioFile.tagOrCreateAndSetDefault
    }

    private fun showError(error: Exception) {
        when (error) {
            is CannotReadException -> context.toast(R.string.edit_song_error_can_not_read)
            is IOException -> context.toast(R.string.edit_song_error_io)
            is ReadOnlyFileException -> context.toast(R.string.edit_song_error_read_only)
            else -> {
                error.printStackTrace()
                context.toast(R.string.edit_song_error)
            }
        }
    }

}