package dev.olog.presentation.edit

import android.content.Context
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.edit.EditItemPresenter
import dev.olog.feature.edit.model.UpdateAlbumInfo
import dev.olog.feature.edit.model.UpdateArtistInfo
import dev.olog.feature.edit.model.UpdateSongInfo
import dev.olog.presentation.R
import dev.olog.presentation.edit.model.UpdateResult
import dev.olog.feature.presentation.base.extensions.toast
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EditItemViewModel @Inject constructor(
    private val context: Context,
    private val presenter: EditItemPresenter,
    private val schedulers: Schedulers

) : ViewModel() {

    suspend fun updateSong(data: UpdateSongInfo): UpdateResult {
        when {
            data.tags.title!!.isBlank() -> return UpdateResult.EMPTY_TITLE
            data.tags.year!!.isNotBlank() && !data.tags.year!!.isDigitsOnly() -> return UpdateResult.ILLEGAL_YEAR
            data.tags.discNo!!.isNotBlank() && !data.tags.discNo!!.isDigitsOnly() -> return UpdateResult.ILLEGAL_DISC_NUMBER
            data.tags.trackNo!!.isNotBlank() && !data.tags.trackNo!!.isDigitsOnly() -> return UpdateResult.ILLEGAL_TRACK_NUMBER
        }
        withContext(schedulers.io) {
            presenter.deleteTrack(data.trackId)
            presenter.updateSingle(data)
        }

        withContext(schedulers.main) {
            context.toast(R.string.edit_track_update_success)
        }

        return UpdateResult.OK
    }

    suspend fun updateAlbum(data: UpdateAlbumInfo): UpdateResult {
        when {
            data.tags.album!!.isBlank() -> return UpdateResult.EMPTY_TITLE
            data.tags.year!!.isNotBlank() && !data.tags.year!!.isDigitsOnly() -> return UpdateResult.ILLEGAL_YEAR
        }

        withContext(schedulers.main) {
            context.toast(R.string.edit_album_update_start)
        }

        withContext(schedulers.io) {

            presenter.deleteAlbum(data.mediaId)
            presenter.updateAlbum(data)
        }
        withContext(schedulers.main) {
            context.toast(R.string.edit_track_update_success)
        }

        return UpdateResult.OK
    }

    suspend fun updateArtist(data: UpdateArtistInfo): UpdateResult {
        when {
            data.tags.artist!!.isBlank() -> return UpdateResult.EMPTY_TITLE
        }

        withContext(schedulers.main) {
            context.toast(R.string.edit_artist_update_start)
        }

        withContext(schedulers.io) {
            presenter.deleteArtist(data.mediaId)
            presenter.updateArtist(data)
        }
        withContext(schedulers.main) {
            context.toast(R.string.edit_track_update_success)
        }

        return UpdateResult.OK
    }

    private fun showErrorMessage(throwable: Exception) {
        // TODO
//        when (throwable) {
//            is CannotReadException -> context.toast(R.string.edit_song_cannot_read)
//            is ReadOnlyFileException -> context.toast(R.string.edit_song_read_only)
//            is FileNotFoundException -> context.toast(R.string.edit_song_file_not_found)
//            else -> context.toast(R.string.popup_error_message)
//        }
    }

}

