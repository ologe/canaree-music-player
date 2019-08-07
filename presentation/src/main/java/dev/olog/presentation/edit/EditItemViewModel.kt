package dev.olog.presentation.edit

import android.content.Context
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.MediaId
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.track.Song
import dev.olog.presentation.R
import dev.olog.presentation.edit.model.SaveImageType
import dev.olog.presentation.edit.model.UpdateResult
import dev.olog.shared.android.extensions.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
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

    override fun onCleared() {
        viewModelScope.cancel()
    }

}

class UpdateSongInfo(
    @JvmField
    val originalSong: Song,
    @JvmField
    val title: String,
    @JvmField
    val artist: String,
    @JvmField
    val albumArtist: String,
    @JvmField
    val album: String,
    @JvmField
    val genre: String,
    @JvmField
    val year: String,
    @JvmField
    val disc: String,
    @JvmField
    val track: String,
    @JvmField
    val image: SaveImageType,
    @JvmField
    val isPodcast: Boolean
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UpdateSongInfo

        if (originalSong != other.originalSong) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (albumArtist != other.albumArtist) return false
        if (album != other.album) return false
        if (genre != other.genre) return false
        if (year != other.year) return false
        if (disc != other.disc) return false
        if (track != other.track) return false
        if (image != other.image) return false
        if (isPodcast != other.isPodcast) return false

        return true
    }

    override fun hashCode(): Int {
        var result = originalSong.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + albumArtist.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + genre.hashCode()
        result = 31 * result + year.hashCode()
        result = 31 * result + disc.hashCode()
        result = 31 * result + track.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + isPodcast.hashCode()
        return result
    }
}

class UpdateAlbumInfo(
    @JvmField
    val mediaId: MediaId,
    @JvmField
    val title: String,
    @JvmField
    val artist: String,
    @JvmField
    val albumArtist: String,
    @JvmField
    val genre: String,
    @JvmField
    val year: String,
    @JvmField
    val image: SaveImageType,
    @JvmField
    val isPodcast: Boolean
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UpdateAlbumInfo

        if (mediaId != other.mediaId) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (albumArtist != other.albumArtist) return false
        if (genre != other.genre) return false
        if (year != other.year) return false
        if (image != other.image) return false
        if (isPodcast != other.isPodcast) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mediaId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + albumArtist.hashCode()
        result = 31 * result + genre.hashCode()
        result = 31 * result + year.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + isPodcast.hashCode()
        return result
    }
}

class UpdateArtistInfo(
    @JvmField
    val mediaId: MediaId,
    @JvmField
    val name: String,
    @JvmField
    val albumArtist: String,
    @JvmField
    val image: SaveImageType,
    @JvmField
    val isPodcast: Boolean
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UpdateArtistInfo

        if (mediaId != other.mediaId) return false
        if (name != other.name) return false
        if (albumArtist != other.albumArtist) return false
        if (image != other.image) return false
        if (isPodcast != other.isPodcast) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mediaId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + albumArtist.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + isPodcast.hashCode()
        return result
    }
}