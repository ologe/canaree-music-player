package dev.olog.presentation.edit

import android.content.Context
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.PresentationId
import dev.olog.presentation.R
import dev.olog.presentation.toDomain
import dev.olog.shared.ApplicationContext
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
    private val getSongUseCase: SongGateway,
    private val getPodcastUseCase: PodcastGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase,
    private val schedulers: Schedulers

) {

    fun toEditTrack(
        mediaId: PresentationId.Track,
        action: () -> Unit
    ) = GlobalScope.launch(schedulers.io) {
        try {
            if (mediaId.isAnyPodcast) {
                val song = getPodcastUseCase.getByParam(mediaId.id)!!
                checkItem(song)
            } else {
                val song = getSongUseCase.getByParam(mediaId.id)!!
                checkItem(song)
            }
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