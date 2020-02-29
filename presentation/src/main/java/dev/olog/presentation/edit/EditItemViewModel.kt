package dev.olog.presentation.edit

import android.content.Context
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import dev.olog.core.MediaId
import dev.olog.shared.ApplicationContext
import dev.olog.core.entity.track.Song
import dev.olog.presentation.R
import dev.olog.presentation.edit.model.UpdateResult
import dev.olog.shared.android.extensions.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import java.io.FileNotFoundException
import javax.inject.Inject

class EditItemViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val presenter: EditItemPresenter

) : ViewModel() {

    suspend fun updateSong(data: UpdateSongInfo): UpdateResult {
        when {
            data.title.isBlank() -> return UpdateResult.EMPTY_TITLE
            data.year.isNotBlank() && !data.year.isDigitsOnly() -> return UpdateResult.ILLEGAL_YEAR
            data.disc.isNotBlank() && !data.disc.isDigitsOnly() -> return UpdateResult.ILLEGAL_DISC_NUMBER
            data.track.isNotBlank() && !data.track.isDigitsOnly() -> return UpdateResult.ILLEGAL_TRACK_NUMBER
        }
        withContext(Dispatchers.IO) {
            presenter.deleteTrack(data.originalSong.id)
            presenter.updateSingle(data)
        }

        withContext(Dispatchers.Main) {
            context.toast(R.string.edit_track_update_success)
        }

        return UpdateResult.OK
    }

    suspend fun updateAlbum(data: UpdateAlbumInfo): UpdateResult {
        when {
            data.title.isBlank() -> return UpdateResult.EMPTY_TITLE
            data.year.isNotBlank() && !data.year.isDigitsOnly() -> return UpdateResult.ILLEGAL_YEAR
        }

        withContext(Dispatchers.Main) {
            context.toast(R.string.edit_album_update_start)
        }

        withContext(Dispatchers.IO) {

            presenter.deleteAlbum(data.mediaId)
            presenter.updateAlbum(data)
        }
        withContext(Dispatchers.Main) {
            context.toast(R.string.edit_track_update_success)
        }

        return UpdateResult.OK
    }

    suspend fun updateArtist(data: UpdateArtistInfo): UpdateResult {
        when {
            data.name.isBlank() -> return UpdateResult.EMPTY_TITLE
        }

        withContext(Dispatchers.Main) {
            context.toast(R.string.edit_artist_update_start)
        }

        withContext(Dispatchers.IO) {
            presenter.deleteArtist(data.mediaId)
            presenter.updateArtist(data)
        }
        withContext(Dispatchers.Main) {
            context.toast(R.string.edit_track_update_success)
        }

        return UpdateResult.OK
    }

    private fun showErrorMessage(throwable: Throwable) {
        // TODO
        when (throwable) {
            is CannotReadException -> context.toast(R.string.edit_song_cannot_read)
            is ReadOnlyFileException -> context.toast(R.string.edit_song_read_only)
            is FileNotFoundException -> context.toast(R.string.edit_song_file_not_found)
            else -> context.toast(R.string.popup_error_message)
        }
    }

}

data class UpdateSongInfo(
    val originalSong: Song,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val album: String,
    val genre: String,
    val year: String,
    val disc: String,
    val track: String,
    val isPodcast: Boolean
)

data class UpdateAlbumInfo(
    val mediaId: MediaId,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val genre: String,
    val year: String,
    val isPodcast: Boolean
)

data class UpdateArtistInfo(
    val mediaId: MediaId,
    val name: String,
    val albumArtist: String,
    val isPodcast: Boolean
)