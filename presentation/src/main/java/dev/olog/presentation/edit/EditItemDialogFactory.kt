package dev.olog.presentation.edit

import android.content.Context
import dev.olog.core.MediaId
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import dev.olog.presentation.R
import dev.olog.shared.android.extensions.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import java.io.File
import java.io.IOException
import javax.inject.Inject

class EditItemDialogFactory @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getSongUseCase: SongGateway,
    private val getPodcastUseCase: PodcastGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase

) {

    fun toEditTrack(mediaId: MediaId, action: () -> Unit) = GlobalScope.launch(Dispatchers.IO) {
        try {
            if (mediaId.isAnyPodcast) {
                val song = getPodcastUseCase.getByParam(mediaId.resolveId)!!
                checkItem(song)
            } else {
                val song = getSongUseCase.getByParam(mediaId.resolveId)!!
                checkItem(song)
            }
            withContext(Dispatchers.Main) {
                action()
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
            withContext(Dispatchers.Main) {
                showError(ex)
            }
        }
    }

    fun toEditAlbum(mediaId: MediaId, action: () -> Unit) = GlobalScope.launch(Dispatchers.IO) {
        try {
            getSongListByParamUseCase.invoke(mediaId).forEach { checkItem(it) }
            withContext(Dispatchers.Main) {
                action()
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
            withContext(Dispatchers.Main) {
                showError(ex)
            }
        }
    }

    fun toEditArtist(mediaId: MediaId, action: () -> Unit) = GlobalScope.launch(Dispatchers.IO) {
        try {
            getSongListByParamUseCase.invoke(mediaId).forEach { checkItem(it) }
            withContext(Dispatchers.Main) {
                action()
            }
        } catch (ex: Throwable) {
            ex.printStackTrace()
            withContext(Dispatchers.Main) {
                showError(ex)
            }
        }
    }

    private fun checkItem(song: Song) {
        val file = File(song.path)
        val audioFile = AudioFileIO.read(file)
        audioFile.tagOrCreateAndSetDefault
    }

    private fun showError(error: Throwable) {
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